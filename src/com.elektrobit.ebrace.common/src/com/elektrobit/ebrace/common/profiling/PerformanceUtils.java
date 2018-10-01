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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

public final class PerformanceUtils
{
    private static final Logger _LOG = Logger.getLogger( PerformanceUtils.class );

    private static final long _MILLI_SEC_FACTOR = 1000 * 1000;

    private static final ConcurrentHashMap<String, List<TimingEntry>> _timingMap = new ConcurrentHashMap<String, List<TimingEntry>>();

    private static final Object startStopProtection = new Object();

    public static void startMeasure(final String name)
    {
        synchronized (startStopProtection)
        {
            _timingMap.putIfAbsent( name, new CopyOnWriteArrayList<TimingEntry>() );
            _timingMap.get( name ).add( new TimingEntry( System.nanoTime() ) );
        }
    }

    public static void stopMeasure(final String name)
    {
        synchronized (startStopProtection)
        {
            final TimingEntry timingEntry = getLastTimingEntry( name );
            timingEntry.setStopTime( System.nanoTime() );
        }
    }

    public static long getMeasuredTimeMs(String name)
    {
        if (!_timingMap.containsKey( name ))
        {
            throw new IllegalArgumentException( "No data for '" + name + "'." );
        }

        return computeTimingDuration( _timingMap.get( name ) ) / _MILLI_SEC_FACTOR;
    }

    private static TimingEntry getLastTimingEntry(final String name)
    {
        return getTimingEntries( name ).get( getTimingEntries( name ).size() - 1 );
    }

    private static List<TimingEntry> getTimingEntries(String name)
    {
        if (!_timingMap.containsKey( name ))
        {
            throw new IllegalArgumentException( "No data for '" + name + "'." );
        }

        return _timingMap.get( name );
    }

    public static void printTimingResult(final String name)
    {
        printTimingResult( name, getTimingEntries( name ) );
    }

    public static void clearTimingResult(final String name)
    {
        if (_timingMap.containsKey( name ))
        {
            _timingMap.remove( name );
        }
    }

    private static void printTimingResult(String name, List<TimingEntry> timingEntries)
    {
        long completeDuration = computeTimingDuration( timingEntries );

        _LOG.info( name + " took " + completeDuration / _MILLI_SEC_FACTOR + " ms." );
    }

    private static long computeTimingDuration(List<TimingEntry> timingEntries)
    {
        long completeDuration = 0;
        for (TimingEntry nextTimingEntry : timingEntries)
        {
            final long nextDuration = nextTimingEntry.getStoptime() - nextTimingEntry.getStartTime();
            if (nextDuration < 0)
            {
                _LOG.warn( "StartTime greater than stopTime." );
            }
            else
            {
                completeDuration += nextDuration;
            }
        }
        return completeDuration;
    }

    public static void printTimingResultWithAverage(String name, long count)
    {
        final double avg = ((double)(computeTimingDuration( getTimingEntries( name ) ))) / _MILLI_SEC_FACTOR / count;
        _LOG.info( String.format( name + " avg %f ms", avg ) );
        clearTimingResult( name );
    }
}
