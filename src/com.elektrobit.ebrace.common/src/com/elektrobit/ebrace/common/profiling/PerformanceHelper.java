/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.profiling;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

public class PerformanceHelper
{

    private final StopWatch stopWatch;
    private final double[] measureItems;
    private boolean running;
    private int nextMeasureItemIndex = 0;

    public PerformanceHelper(int numberOfRuns)
    {
        stopWatch = new StopWatch();
        measureItems = new double[numberOfRuns];
        running = false;
    }

    public void start()
    {
        if (!running)
        {
            running = true;
            stopWatch.reset();
            stopWatch.start();
        }
    }

    public void stop()
    {
        if (running)
        {
            stopWatch.stop();
            measureItems[nextMeasureItemIndex] = stopWatch.getTime();
            nextMeasureItemIndex++;
            running = false;
        }
    }

    public double min()
    {
        if (running)
            throw new IllegalArgumentException( "Stop measuring before calc values" );

        return new Statistics( measureItems ).min();
    }

    public double avg()
    {
        if (running)
            throw new IllegalArgumentException( "Stop measuring before calc values" );

        return new Statistics( measureItems ).getMean();
    }

    public double max()
    {
        if (running)
            throw new IllegalArgumentException( "Stop measuring before calc values" );

        return new Statistics( measureItems ).max();
    }

    public List<Double> all()
    {
        List<Double> list = new ArrayList<Double>();

        for (double nextVal : measureItems)
        {
            list.add( nextVal );
        }

        return list;
    }
}
