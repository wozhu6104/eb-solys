/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.memory;

import java.util.HashSet;
import java.util.Set;

import com.elektrobit.ebrace.common.profiling.Statistics;

public class CyclicMemoryChecker
{
    private final static int NUMBER_OF_RUNS = 50;
    private final boolean debugOn;
    private final long[] heapSizes = new long[NUMBER_OF_RUNS];
    private final MemorySnapshotHelper m = new MemorySnapshotHelper( "mem.hprof" );

    public CyclicMemoryChecker()
    {
        this.debugOn = false;
    }

    public CyclicMemoryChecker(boolean debugOn)
    {
        this.debugOn = debugOn;
    }

    public boolean isHeapSizeStable(Runnable testCode) throws Exception
    {
        runTestCode50Times( testCode );

        Set<Long> set = new HashSet<Long>();

        for (Long nextHeapSize : heapSizes)
        {
            set.add( nextHeapSize );
        }

        return set.size() < heapSizes.length;
    }

    public boolean heapSizeStdDevInPercentSmallerThen(Runnable testCode, double maxStdDev) throws Exception
    {
        runTestCode50Times( testCode );

        Statistics statistics = new Statistics( heapSizes );

        return statistics.getMeanDevInPercent() < maxStdDev;
    }

    public double heapSizeStdDevInPercent(Runnable testCode) throws Exception
    {
        runTestCode50Times( testCode );

        Statistics statistics = new Statistics( heapSizes );

        if (debugOn)
            System.out.println( "HeapSizeDevInPercent: " + statistics.getMeanDevInPercent() );

        return statistics.getMeanDevInPercent();
    }

    private void runTestCode50Times(Runnable testCode) throws Exception
    {
        // This method has side effects on the heap
        // We call this method one time before
        // to get a more precise result
        computeHeapSize();

        for (int i = 0; i < NUMBER_OF_RUNS; i++)
        {
            testCode.run();
            cleanupMemory();
            heapSizes[i] = computeHeapSize();
        }
    }

    public void cleanupMemory()
    {
        System.gc();
        try
        {
            Thread.sleep( 250 );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private long computeHeapSize() throws Exception
    {
        m.makeMemorySnapshot();
        long heapSize = m.getHeapSize();
        if (debugOn)
            System.out.println( "HeapSize: " + heapSize );
        m.closeAndDeleteSnapshot();
        return heapSize;
    }

    public Statistics getStatistics()
    {
        return new Statistics( heapSizes );
    }

}
