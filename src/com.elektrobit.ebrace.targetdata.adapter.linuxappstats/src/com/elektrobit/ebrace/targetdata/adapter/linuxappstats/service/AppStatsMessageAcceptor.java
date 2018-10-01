/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.linuxappstats.service;

import java.util.HashMap;
import java.util.Map;

import com.elektrobit.ebrace.common.utils.UnitConverter;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.protobuf.LinuxAppStatsContentTAProto.MeasureMessage;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import lombok.extern.log4j.Log4j;

@Log4j
public final class AppStatsMessageAcceptor
{
    private final Map<Integer, MeasureMessage> processMessageCache = new HashMap<Integer, MeasureMessage>();
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final CacheHelper<Double> latestCpuValueCache = new CacheHelper<Double>();
    private final CacheHelper<Long> latestMemPeakValueCache = new CacheHelper<Long>();
    private final CacheHelper<Long> latestMemCountValueCache = new CacheHelper<Long>();
    private final DataSourceContext dataSourceContext;

    public AppStatsMessageAcceptor(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
    }

    public void acceptMessage(MeasureMessage measureMessage, TimestampCreator timestampCreator)
            throws IllegalArgumentException
    {
        int tickCounts = measureMessage.getCC();

        if (isProcessMessage( measureMessage ))
        {
            extractAndPostProcessMessage( measureMessage, timestampCreator, tickCounts );
        }
        else if (isThreadMessage( measureMessage ))
        {
            extractAndPostThreadMessages( measureMessage, timestampCreator, tickCounts );
        }
        else
        {
            throw new IllegalArgumentException( "Cannot parse message, it is neither a process measure message, nor a thread measure message. Message was "
                    + measureMessage );
        }
    }

    private boolean isProcessMessage(MeasureMessage processMeasureMessage)
    {
        return processMeasureMessage.hasPI() && processMeasureMessage.hasPN() && processMeasureMessage.hasPT()
                && !processMeasureMessage.hasTI();
    }

    private void extractAndPostProcessMessage(MeasureMessage measureMessage, TimestampCreator timestampCreator,
            int tickCounts)
    {
        int pid = measureMessage.getPI();
        String pName = measureMessage.getPN();
        double cpuValue = measureMessage.getPT();

        processMessageCache.put( pid, measureMessage );

        String channelName = "cpu.prof.p:" + pName + ":" + pid;
        RuntimeEventChannel<Double> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                 channelName,
                                                 Unit.PERCENT,
                                                 "Percentage of time a processor are executing the process." );
        runtimeEventAcceptor.acceptEvent( timestampCreator.create( tickCounts ).getTimeInMillis(),
                                          channel,
                                          null,
                                          cpuValue );
    }

    private boolean isThreadMessage(MeasureMessage processMeasureMessage)
    {
        return processMeasureMessage.hasPI() && processMeasureMessage.hasTI() && processMeasureMessage.hasTN()
                && processMeasureMessage.hasTT();
    }

    private void extractAndPostThreadMessages(MeasureMessage measureMessage, TimestampCreator timestampCreator,
            int tickCounts)
    {
        int pid = measureMessage.getPI();

        String pName = getNameOfProcess( pid );

        if (pName != null)
        {
            int tid = measureMessage.getTI();
            String tName = measureMessage.getTN();
            double cpuValue = measureMessage.getTT();
            int memPeakValue = measureMessage.getTP();
            int memCountValue = measureMessage.getTC();

            String cpuChannelName = "cpu.prof.p:" + pName + ":" + pid + ".t:" + tName + ":" + tid;
            String memPeakChannelName = "mem.prof.p:" + pName + ":" + pid + ".t:" + tName + ":" + tid;
            String memCountChannelName = "mem.count.p:" + pName + ":" + pid + ".t:" + tName + ":" + tid;

            postCPUEvent( timestampCreator,
                          tickCounts,
                          cpuValue,
                          cpuChannelName,
                          "Percentage of time a processor are executing the thread." );
            postMemPeakEvent( timestampCreator,
                              tickCounts,
                              memPeakValue,
                              memPeakChannelName,
                              "Memory peaks of the thread." );
            postMemCountEvent( timestampCreator,
                               tickCounts,
                               memCountValue,
                               memCountChannelName,
                               "Number of memory allocations of the thread." );
        }
        else
        {
            log.info( "Waiting for process message before accepting thread values of this process. PID was " + pid );
        }
    }

    private void postCPUEvent(TimestampCreator timestampCreator, int tickCounts, double value, String channelName,
            String description)
    {
        if (latestCpuValueCache.needsUpdate( channelName, value ))
        {
            RuntimeEventChannel<Double> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext, channelName, Unit.PERCENT, description );
            runtimeEventAcceptor.acceptEvent( timestampCreator.create( tickCounts ).getTimeInMillis(),
                                              channel,
                                              null,
                                              value );
        }
    }

    private void postMemPeakEvent(TimestampCreator timestampCreator, int tickCounts, long value, String channelName,
            String description)
    {
        if (latestMemPeakValueCache.needsUpdate( channelName, value ))
        {
            RuntimeEventChannel<Long> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext, channelName, Unit.KILOBYTE, description );
            runtimeEventAcceptor.acceptEvent( timestampCreator.create( tickCounts ).getTimeInMillis(),
                                              channel,
                                              null,
                                              UnitConverter.convertBytesToKB( value ) );
        }
    }

    private void postMemCountEvent(TimestampCreator timestampCreator, int tickCounts, long count, String channelName,
            String description)
    {
        if (latestMemCountValueCache.needsUpdate( channelName, count ))
        {
            RuntimeEventChannel<Long> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext, channelName, Unit.COUNT, description );
            runtimeEventAcceptor.acceptEvent( timestampCreator.create( tickCounts ).getTimeInMillis(),
                                              channel,
                                              null,
                                              count );
        }
    }

    private String getNameOfProcess(int pid)
    {
        MeasureMessage processMeasureMessage = processMessageCache.get( pid );

        String pName = null;
        if (processMeasureMessage != null)
        {
            pName = processMeasureMessage.getPN();
        }

        return pName;
    }
}
