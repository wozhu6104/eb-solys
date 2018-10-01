/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor.impl;

import java.util.List;

import com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor.protobuf.TargetAgentProtocolTopResourceMonitor.TopResourceInfo;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class TopResMonDataEventDecoder
{
    private static final String TOTAL_CPU_CONSUMPTION_CHANNEL = "cpu.system";
    private static final String TOTAL_MEMORY_CONSUMPTION_CHANNEL = "mem.system";

    private static final String LEVEL_SEPERATOR = ".";
    private static final String PROPERTY_SEPERATOR = ":";
    private static final String CPU = "cpu";
    private static final String MEM = "mem";
    private static final String PROCESS = "proc";

    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final DataSourceContext dataSourceContext;
    private final PageParser pageParser;
    private long timestampInMicro;

    public TopResMonDataEventDecoder(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
        pageParser = new PageParser();
    }

    public void newResMonApplicationMessageReceived(Timestamp timestamp, TopResourceInfo topRessourceInfo)
    {
        this.timestampInMicro = timestamp.getTimeInMillis() * 1000;

        pageParser.OnNewContentReceived( topRessourceInfo.getTopTunneledOutput().toByteArray(),
                                         topRessourceInfo.getAndroidSDKVersion() );
        if (pageParser.isParsingSuccess())
        {
            processResourceInformation();
        }
    }

    private void processResourceInformation()
    {
        processCPUInformations();
        processMemoryInformations();
    }

    private void processCPUInformations()
    {
        processTotalCpuConsumption();
        processCPUConsumptionPerProc();
    }

    private void processTotalCpuConsumption()
    {
        double totalCpuConsumption;
        RuntimeEventChannel<Double> percentageTotalCPUConsumptionChannel;

        totalCpuConsumption = pageParser.getTotalCpuConsumption();

        percentageTotalCPUConsumptionChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, TOTAL_CPU_CONSUMPTION_CHANNEL, Unit.PERCENT, "" );

        runtimeEventAcceptor.acceptEventMicros( timestampInMicro,
                                                percentageTotalCPUConsumptionChannel,
                                                ModelElement.NULL_MODEL_ELEMENT,
                                                totalCpuConsumption );
    }

    private void processCPUConsumptionPerProc()
    {
        List<ProcResourceConsumptionParser> procResourceConsumptionParser = pageParser.getProcessInfoList();

        for (ProcResourceConsumptionParser topProcessInfo : procResourceConsumptionParser)
        {
            if (!topProcessInfo.getName().matches( "top" ))
            {
                String channelId = CPU + LEVEL_SEPERATOR + createChannelId( topProcessInfo );
                sendProcCpuConsumption( channelId, topProcessInfo );
            }
        }
    }

    private String createChannelId(ProcResourceConsumptionParser topProcessInfo)
    {
        StringBuilder channelName = new StringBuilder();
        channelName.append( PROCESS );
        channelName.append( LEVEL_SEPERATOR );
        channelName.append( topProcessInfo.getName() );
        channelName.append( PROPERTY_SEPERATOR );
        channelName.append( topProcessInfo.getPID() );

        return channelName.toString();
    }

    private void sendProcCpuConsumption(String channelId, ProcResourceConsumptionParser topProcessInfo)
    {

        RuntimeEventChannel<Double> procCpuConsumptionChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, channelId, Unit.PERCENT, "" );

        runtimeEventAcceptor.acceptEventMicros( timestampInMicro,
                                                procCpuConsumptionChannel,
                                                ModelElement.NULL_MODEL_ELEMENT,
                                                topProcessInfo.getCpuUsage() );
    }

    private void processMemoryInformations()
    {
        processTotalMemoryConsumption();
        processMemoryConsumptionPerPid();
    }

    private void processTotalMemoryConsumption()
    {
        long totalMemoryConsumption;
        RuntimeEventChannel<Long> percentageTotalMemoryConsumptionChannel;

        totalMemoryConsumption = pageParser.getTotalMemoryConsumption();

        percentageTotalMemoryConsumptionChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                 TOTAL_MEMORY_CONSUMPTION_CHANNEL,
                                                 Unit.KILOBYTE,
                                                 "" );

        runtimeEventAcceptor.acceptEventMicros( timestampInMicro,
                                                percentageTotalMemoryConsumptionChannel,
                                                ModelElement.NULL_MODEL_ELEMENT,
                                                totalMemoryConsumption );

    }

    private void processMemoryConsumptionPerPid()
    {
        List<ProcResourceConsumptionParser> topProcessInfoList = pageParser.getProcessInfoList();

        for (ProcResourceConsumptionParser topProcessInfo : topProcessInfoList)
        {
            if (!topProcessInfo.getName().matches( "top" ))
            {
                String channelId = MEM + LEVEL_SEPERATOR + createChannelId( topProcessInfo );
                sendProcMemoryConsumption( channelId, topProcessInfo );
            }
        }

    }

    private void sendProcMemoryConsumption(String channelId,
            ProcResourceConsumptionParser procResourceConsumptionParser)
    {

        RuntimeEventChannel<Long> procMemoryConsumptionChannel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext, channelId, Unit.KILOBYTE, "" );

        runtimeEventAcceptor.acceptEventMicros( timestampInMicro,
                                                procMemoryConsumptionChannel,
                                                ModelElement.NULL_MODEL_ELEMENT,
                                                procResourceConsumptionParser.getMemoryUsage() );
    }
}
