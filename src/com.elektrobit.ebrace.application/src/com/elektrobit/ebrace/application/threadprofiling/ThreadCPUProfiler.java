/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application.threadprofiling;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class ThreadCPUProfiler
{
    private static final Logger profLog = Logger.getLogger( "rprof" );

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();
    private final int NUM_CPU_CORES = Runtime.getRuntime().availableProcessors();
    private final Map<Long, CPUMeasureItem> threadId2LastCPUValue = new HashMap<Long, CPUMeasureItem>();
    private final long NANO_CONSTANT = 1000000;
    private Future<?> future;

    public void start()
    {
        future = executor.submit( new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                {
                    measure();
                    try
                    {
                        Thread.sleep( 100 );
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        } );
    }

    private void measure()
    {
        for (Long nextThreadID : THREAD_MX_BEAN.getAllThreadIds())
        {
            CPUMeasureItem cpuMeasureItem = getLastMeasureItem( nextThreadID );

            long currentTimestamp = System.nanoTime();
            long currentCpuTime = THREAD_MX_BEAN.getThreadCpuTime( nextThreadID );

            if (cpuMeasureItem.getTimestamp() > 0)
            {
                long lastTimestamp = cpuMeasureItem.getTimestamp();
                long lastCPUTime = cpuMeasureItem.getCpuTime();

                float elapsedTime = (currentTimestamp - lastTimestamp) / NANO_CONSTANT;
                float relativeCPUValue = (currentCpuTime - lastCPUTime) / NANO_CONSTANT;

                float cpuUsage = relativeCPUValue / (elapsedTime * NUM_CPU_CORES);

                float cpuUsageInPercent = cpuUsage * 100;

                profLog.debug( nextThreadID + "|" + cpuUsageInPercent + "|"
                        + THREAD_MX_BEAN.getThreadInfo( nextThreadID ).getThreadName() );
            }

            updateCPUMeasureItem( cpuMeasureItem, currentTimestamp, currentCpuTime );
        }
    }

    private CPUMeasureItem getLastMeasureItem(long threadId)
    {
        if (!threadId2LastCPUValue.containsKey( threadId ))
            threadId2LastCPUValue.put( threadId, new CPUMeasureItem() );

        return threadId2LastCPUValue.get( threadId );
    }

    private void updateCPUMeasureItem(CPUMeasureItem cpuMeasureItem, long nanoTime, long cpuTime)
    {
        cpuMeasureItem.setTimestamp( nanoTime );
        cpuMeasureItem.setCpuTime( cpuTime );
    }

    public void stop()
    {
        if (future != null)
            future.cancel( true );
    }
}
