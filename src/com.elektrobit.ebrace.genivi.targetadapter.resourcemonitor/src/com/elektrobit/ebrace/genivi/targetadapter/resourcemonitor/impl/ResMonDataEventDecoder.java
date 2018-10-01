/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.utils.UnitConverter;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.WriteProcessRegistryIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ThreadInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ResMonDataEventDecoder
{
    private final static Logger LOG = Logger.getLogger( ResMonDataEventDecoder.class );

    private static final String LEVEL_SEPERATOR = ".";
    private static final String PROPERTY_SEPERATOR = ":";
    private static final String CPU = "cpu";
    private static final String MEM = "mem";
    private static final String PRIORITY = "prio";
    private static final String STATE = "state";
    private static final String PROCESS = "proc";
    private static final String THREAD = "t";

    private boolean channelsHaveBeenInitialized;

    private Map<Long, RuntimeEventChannel<Double>> cpuConsumptionChannels;
    private Map<Long, RuntimeEventChannel<Long>> memConsumptionChannels;
    private Map<String, RuntimeEventChannel<Double>> threadCpuConsumptionChannels;
    private Map<String, RuntimeEventChannel<Integer>> threadPriorityConsumptionChannels;
    private Map<String, RuntimeEventChannel<String>> threadStateConsumptionChannels;

    private RuntimeEventChannel<Double> percentualTotalCPUConsumptionChannel;
    private RuntimeEventChannel<Long> absoluteUsedSystemPrivateMemoryChannel;

    private int totalCpuMsInThisWindow = 0;
    private long msSinceLastMeasurement = 0;
    private long lastTimestampMs = 0;

    private double totalCpuPercentFromThreads = 0;
    private final WriteProcessRegistryIF processRegistry;
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private boolean printPIDAndTIDInHex = false;
    private final DataSourceContext dataSourceContext;
    private final Unit<Integer> priorityUnit = Unit.createCustomUnit( "Priority", Integer.class );

    public ResMonDataEventDecoder(WriteProcessRegistryIF processRegistry, StructureAcceptor structureAcceptor,
            RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.processRegistry = processRegistry;
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
        channelsHaveBeenInitialized = false;
    }

    public synchronized void initIfNot()
    {
        if (!channelsHaveBeenInitialized)
        {
            initRuntimeEventChannelsForResourceConsumption();
            channelsHaveBeenInitialized = true;
        }
    }

    private void initRuntimeEventChannelsForResourceConsumption()
    {
        percentualTotalCPUConsumptionChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, "cpu.system", Unit.PERCENT, "" );

        absoluteUsedSystemPrivateMemoryChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, "mem.system", Unit.KILOBYTE, "" );

        cpuConsumptionChannels = new HashMap<Long, RuntimeEventChannel<Double>>();
        memConsumptionChannels = new HashMap<Long, RuntimeEventChannel<Long>>();
        threadCpuConsumptionChannels = new HashMap<String, RuntimeEventChannel<Double>>();
        threadPriorityConsumptionChannels = new HashMap<String, RuntimeEventChannel<Integer>>();
        threadStateConsumptionChannels = new HashMap<String, RuntimeEventChannel<String>>();
    }

    public void newResMonApplicationMessageReceived(Timestamp timestamp, ResourceInfo resInfo)
    {
        processNewResourceInformation( resInfo, timestamp );
    }

    private void processThreadInformationForProcess(ProcessInfo process, Timestamp timestamp, int numberOfCores)
    {
        ProcessInfo previousProcessInfo = processRegistry.getProcessInfo( (int)process.getPid(), dataSourceContext );

        for (ThreadInfo threadInfo : process.getThreadsList())
        {
            if (threadInfo.hasState())
            {
                runtimeEventAcceptor.acceptEvent( timestamp.getTimeInMillis(),
                                                  createOrGetThreadStateConsumptionChannel( process, threadInfo ),
                                                  null,
                                                  threadInfo.getState() );
            }
            if (threadInfo.hasPriority())
            {
                runtimeEventAcceptor.acceptEvent( timestamp.getTimeInMillis(),
                                                  createOrGetThreadPriorityChannel( process, threadInfo ),
                                                  null,
                                                  threadInfo.getPriority() );
            }
            if (previousProcessInfo != null)
            {
                ThreadInfo previousThreadInfo = previousThreadInfo( previousProcessInfo, threadInfo );
                if (previousThreadInfo != null)
                {
                    if (process.hasTimeInKernelModeMs() && process.hasTimeInUserModeMs())
                    {
                        long currentSumMs = threadInfo.getTimeInKernelModeMs() + threadInfo.getTimeInUserModeMs();
                        long lastSumMs = previousThreadInfo.getTimeInKernelModeMs()
                                + previousThreadInfo.getTimeInUserModeMs();

                        acceptValidatedCpuValues( timestamp,
                                                  createOrGetThreadCpuConsumptionChannel( process, threadInfo ),
                                                  null,
                                                  calculateCPUConsumption( currentSumMs, lastSumMs ),
                                                  numberOfCores );
                    }
                    else
                    {
                        acceptValidatedCpuValues( timestamp,
                                                  createOrGetThreadCpuConsumptionChannel( process, threadInfo ),
                                                  null,
                                                  calculateCPUConsumptionFromThreadWithTimestamp( threadInfo,
                                                                                                  previousThreadInfo ),
                                                  numberOfCores );
                    }
                }
            }
        }
    }

    private void acceptValidatedCpuValues(Timestamp timestamp, RuntimeEventChannel<Double> channel, TreeNode node,
            Double cpuConsumption, int numberOfCores)
    {
        // This should not happen and we have to improve our Target-Agent,
        // that this never happens, but as long as we haven't done this,
        // we ignoring other values. (rage2903)
        if (cpuConsumption >= 0.0)
        {
            double normalizedCPUConsumption = cpuConsumption;

            if (normalizedCPUConsumption > 0.0)
            {
                normalizedCPUConsumption /= numberOfCores;
            }

            runtimeEventAcceptor.acceptEvent( timestamp.getTimeInMillis(), channel, node, normalizedCPUConsumption );
        }
        else
        {
            LOG.error( "Computation of CPU consumption value was out of range. Ignoring this value. Value was "
                    + cpuConsumption + ", Process was " + channel.getName() );
        }
    }

    private void processNewResourceInformation(ResourceInfo resourceInfo, Timestamp timestamp)
    {
        resetAccumulatedData();

        msSinceLastMeasurement = (timestamp.getAbsoluteTargetTimeInMillies() - lastTimestampMs);
        lastTimestampMs = timestamp.getAbsoluteTargetTimeInMillies();

        if (msSinceLastMeasurement < 0)
        {
            LOG.error( "New Resource message timestamp older as previous! ms since last message: "
                    + msSinceLastMeasurement );
        }

        if (msSinceLastMeasurement == lastTimestampMs)
        {
            processRegistry.setProcessInfoOfActiveProcesses( resourceInfo.getProcessList(), dataSourceContext );
            return; // first message, no timestamp span available
        }

        for (ProcessInfo process : resourceInfo.getProcessList())
        {
            checkIfPIDAndTIDShallBeInHex( process );
            collectResourceConsumptionDataForProcess( resourceInfo, process, timestamp );
            processThreadInformationForProcess( process, timestamp, resourceInfo.getSystem().getNumberOfCores() );
        }
        processRegistry.setProcessInfoOfActiveProcesses( resourceInfo.getProcessList(), dataSourceContext );
        propagateSystemCPUConsumption( resourceInfo, timestamp );
        propagateSystemMemoryConsumption( resourceInfo, timestamp );
    }

    private void checkIfPIDAndTIDShallBeInHex(ProcessInfo process)
    {
        // Only showing TID and PID in hex if thread info was received, because this is the only hint,
        // that the data come from the win ce resource monitor.
        if (!printPIDAndTIDInHex && process.getThreadsCount() > 0)
        {
            printPIDAndTIDInHex = true;
        }
    }

    private ThreadInfo previousThreadInfo(ProcessInfo previousProzess, ThreadInfo threadInfoToFind)
    {
        for (ThreadInfo threadInfo : previousProzess.getThreadsList())
        {
            if (threadInfo.getTid() == threadInfoToFind.getTid()
                    && threadInfo.getName().equals( threadInfoToFind.getName() ))
            {
                return threadInfo;
            }
        }
        return null;
    }

    private String createId(ProcessInfo process, ThreadInfo threadInfo)
    {
        StringBuilder channelName = new StringBuilder();
        channelName.append( PROCESS );
        channelName.append( LEVEL_SEPERATOR );
        channelName.append( process.getName() );
        channelName.append( PROPERTY_SEPERATOR );
        channelName.append( formatID( process.getPid() ) );

        if (threadInfo != null)
        {
            channelName.append( LEVEL_SEPERATOR );
            channelName.append( THREAD );
            channelName.append( PROPERTY_SEPERATOR );
            channelName.append( threadInfo.getName() );
            channelName.append( PROPERTY_SEPERATOR );
            channelName.append( formatID( threadInfo.getTid() ) );
        }
        return channelName.toString();
    }

    private String formatID(long id)
    {
        if (printPIDAndTIDInHex)
        {
            return ("" + Long.toHexString( id )).toUpperCase();
        }
        else
        {
            return "" + id;
        }
    }

    private RuntimeEventChannel<String> createOrGetThreadStateConsumptionChannel(ProcessInfo process, ThreadInfo thread)
    {
        String channelName = STATE + LEVEL_SEPERATOR + createId( process, thread );
        if (!threadStateConsumptionChannels.containsKey( channelName ))
        {
            RuntimeEventChannel<String> newChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext, channelName, Unit.TEXT, "" );
            threadStateConsumptionChannels.put( channelName, newChannel );
        }
        return threadStateConsumptionChannels.get( channelName );
    }

    private RuntimeEventChannel<Integer> createOrGetThreadPriorityChannel(ProcessInfo process, ThreadInfo thread)
    {
        String channelName = PRIORITY + LEVEL_SEPERATOR + createId( process, thread );
        if (!threadPriorityConsumptionChannels.containsKey( channelName ))
        {
            RuntimeEventChannel<Integer> newChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext, channelName, priorityUnit, "" );
            threadPriorityConsumptionChannels.put( channelName, newChannel );
        }
        return threadPriorityConsumptionChannels.get( channelName );
    }

    private RuntimeEventChannel<Double> createOrGetThreadCpuConsumptionChannel(ProcessInfo process, ThreadInfo thread)
    {
        String channelName = CPU + LEVEL_SEPERATOR + createId( process, thread );
        if (!threadCpuConsumptionChannels.containsKey( channelName ))
        {
            RuntimeEventChannel<Double> newChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext, channelName, Unit.PERCENT, "" );
            threadCpuConsumptionChannels.put( channelName, newChannel );
        }
        return threadCpuConsumptionChannels.get( channelName );
    }

    private void resetAccumulatedData()
    {
        totalCpuMsInThisWindow = 0;
        totalCpuPercentFromThreads = 0;
    }

    private void collectResourceConsumptionDataForProcess(ResourceInfo resourceInfo, ProcessInfo processInfo,
            Timestamp timestamp)
    {
        ProcessInfo previousProcessInfo = processRegistry.getProcessInfo( (int)processInfo.getPid(),
                                                                          dataSourceContext );
        if (previousProcessInfo == null)
        {
            return;
        }

        double cpuConsumption;
        if (processInfo.hasTimeInUserModeMs() && processInfo.hasTimeInKernelModeMs())
        {
            accumulateTotalProcessCpuTime( processInfo, previousProcessInfo );
            long currentSumMs = processInfo.getTimeInKernelModeMs() + processInfo.getTimeInUserModeMs();
            long lastSumMs = previousProcessInfo.getTimeInKernelModeMs() + previousProcessInfo.getTimeInUserModeMs();

            if (currentSumMs < lastSumMs)
            {
                LOG.error( "Resource consumption sum smaller as previous one! Process " + processInfo.getName() + ":"
                        + processInfo.getPid() + ", previous sum " + lastSumMs + "ms new sum " + currentSumMs + "ms" );
            }
            cpuConsumption = calculateCPUConsumption( currentSumMs, lastSumMs );
        }
        else
        {
            cpuConsumption = calculateProcessCPUConsumptionFromThreads( processInfo, previousProcessInfo );
            totalCpuPercentFromThreads += cpuConsumption;
        }

        acceptValidatedCpuValues( timestamp,
                                  getCPUChannelForProcess( processInfo ),
                                  null,
                                  cpuConsumption,
                                  resourceInfo.getSystem().getNumberOfCores() );

        if (processInfo.hasVmUsageBytes())
        {
            long memoryConsumption = processInfo.getVmUsageBytes();

            runtimeEventAcceptor.acceptEvent( timestamp.getTimeInMillis(),
                                              getMemoryChannelForProcess( processInfo ),
                                              null,
                                              UnitConverter.convertBytesToKB( memoryConsumption ) );
        }
    }

    private void propagateSystemCPUConsumption(ResourceInfo resourceInfo, Timestamp timestamp)
    {
        double totalCPUConsumption;
        if (resourceInfo.getProcess( 0 ).hasTimeInUserModeMs() && resourceInfo.getProcess( 0 ).hasTimeInKernelModeMs())
        {
            totalCPUConsumption = calculateTotalCPUConsumptionInPercent();
        }
        else
        {
            totalCPUConsumption = totalCpuPercentFromThreads;
        }

        acceptValidatedCpuValues( timestamp,
                                  percentualTotalCPUConsumptionChannel,
                                  null,
                                  formatTwoDecimals( totalCPUConsumption ),
                                  resourceInfo.getSystem().getNumberOfCores() );
    }

    private void propagateSystemMemoryConsumption(ResourceInfo resourceInfo, Timestamp timestamp)
    {
        Long totalMemoryConsumption = UnitConverter.convertBytesToKB( resourceInfo.getSystem().getUsedPmMemBytes() );
        runtimeEventAcceptor.acceptEvent( timestamp.getTimeInMillis(),
                                          absoluteUsedSystemPrivateMemoryChannel,
                                          null,
                                          totalMemoryConsumption );
    }

    private double formatTwoDecimals(double value)
    {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator( '.' );
        DecimalFormat df = new DecimalFormat( "#.##", symbols );

        return Double.parseDouble( df.format( value ) );
    }

    private double calculateTotalCPUConsumptionInPercent()
    {
        return 100.0 * totalCpuMsInThisWindow / msSinceLastMeasurement;
    }

    private void accumulateTotalProcessCpuTime(ProcessInfo processInfo, ProcessInfo previousProcessInfo)
    {
        long sumKernelMode = processInfo.getTimeInKernelModeMs() - previousProcessInfo.getTimeInKernelModeMs();
        long sumUserMode = processInfo.getTimeInUserModeMs() - previousProcessInfo.getTimeInUserModeMs();
        totalCpuMsInThisWindow += sumKernelMode + sumUserMode;
    }

    private double calculateCPUConsumption(long currentSumMs, long lastSumMs)
    {
        return formatTwoDecimals( 100.0 * (currentSumMs - lastSumMs) / msSinceLastMeasurement );
    }

    private double calculateCPUConsumptionFromThreadWithTimestamp(ThreadInfo threadInfo, ThreadInfo previousThreadInfo)
    {
        long sumKernelMode = threadInfo.getTimeInKernelModeMs() - previousThreadInfo.getTimeInKernelModeMs();
        long sumUserMode = threadInfo.getTimeInUserModeMs() - previousThreadInfo.getTimeInUserModeMs();
        long totalSum = sumKernelMode + sumUserMode;
        long msSinceLastThreadMeasurement = threadInfo.getTimestamp() - previousThreadInfo.getTimestamp();

        if (msSinceLastThreadMeasurement <= 0)
        {
            LOG.error( "Thread time stamp is the same or equal to previous one " + threadInfo.getName() + ":"
                    + threadInfo.getTid() + ", previous time stamp " + previousThreadInfo.getTimestamp()
                    + " current time stamp " + threadInfo.getTimestamp() + "ms" );
            return 0;
        }
        return formatTwoDecimals( 100.0 * totalSum / msSinceLastThreadMeasurement );
    }

    private double calculateProcessCPUConsumptionFromThreads(ProcessInfo processInfo, ProcessInfo previousProcessInfo)
    {
        double totalCPUConsumption = 0;
        for (ThreadInfo threadInfo : processInfo.getThreadsList())
        {
            ThreadInfo previousThreadInfo = previousThreadInfo( previousProcessInfo, threadInfo );
            if (previousThreadInfo != null)
            {
                totalCPUConsumption += calculateCPUConsumptionFromThreadWithTimestamp( threadInfo, previousThreadInfo );
            }
        }
        return formatTwoDecimals( totalCPUConsumption );
    }

    private RuntimeEventChannel<Long> getMemoryChannelForProcess(ProcessInfo process)
    {
        RuntimeEventChannel<Long> channel = memConsumptionChannels.get( process.getPid() );
        if (channel == null)
        {
            channel = createNewMemoryConsumptionChannelForProcess( process );
        }
        return channel;
    }

    private RuntimeEventChannel<Long> createNewMemoryConsumptionChannelForProcess(ProcessInfo process)
    {
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( dataSourceContext,
                                                                                                 createMemoryChannelName( process ),
                                                                                                 Unit.KILOBYTE,
                                                                                                 "" );
        memConsumptionChannels.put( process.getPid(), channel );
        return channel;
    }

    private String createMemoryChannelName(ProcessInfo process)
    {
        return MEM + LEVEL_SEPERATOR + createId( process, null );
    }

    private RuntimeEventChannel<Double> getCPUChannelForProcess(ProcessInfo process)
    {
        RuntimeEventChannel<Double> channel = cpuConsumptionChannels.get( process.getPid() );
        if (channel == null)
        {
            channel = createNewCPUConsumptionChannelForProcess( process );
        }
        return channel;
    }

    private RuntimeEventChannel<Double> createNewCPUConsumptionChannelForProcess(ProcessInfo process)
    {
        RuntimeEventChannel<Double> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, getCPUChannelName( process ), Unit.PERCENT, "" );
        cpuConsumptionChannels.put( process.getPid(), channel );
        return channel;
    }

    private String getCPUChannelName(ProcessInfo process)
    {
        return CPU + LEVEL_SEPERATOR + createId( process, null );
    }
}
