/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.profiling.PerformanceUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class TimestampPositionInListConverter
{
    private static final Logger LOG = Logger.getLogger( TimestampPositionInListConverter.class );

    private final List<TimebasedObject> events;
    private final List<Long> sortedTimestamps;
    private final int widgetDimension;

    private final Map<Long, Integer> timeMarkerToPositionMap = new HashMap<Long, Integer>();

    public TimestampPositionInListConverter(List<TimebasedObject> events, List<Long> timestamps, int widgetDimension)
    {
        this.events = events;
        Collections.sort( timestamps );
        this.sortedTimestamps = timestamps;
        this.widgetDimension = widgetDimension;
    }

    public Map<Long, Integer> getTimestampPositions()
    {
        if (events != null && !events.isEmpty() && !sortedTimestamps.isEmpty())
        {
            String name = "TS" + System.currentTimeMillis();// TODO REMOVE
            PerformanceUtils.startMeasure( name );
            computePositionsFromEvents();
            PerformanceUtils.stopMeasure( name );
            PerformanceUtils.printTimingResult( name );
            PerformanceUtils.clearTimingResult( name );
        }
        else
            addDummyPositions();
        return timeMarkerToPositionMap;
    }

    private void computePositionsFromEvents()
    {
        handleTimestampsBeforeFirstEvent();

        if (sortedTimestamps.isEmpty())
            return;

        TimebasedObject currentRuntimeEvent = null;
        TimebasedObject previousRuntimeEvent = null;

        for (int i = 0; i < events.size(); i++)
        {
            previousRuntimeEvent = currentRuntimeEvent;
            currentRuntimeEvent = events.get( i );
            if (previousRuntimeEvent == null)
                continue;

            long currentEventTimestamp = currentRuntimeEvent.getTimestamp();
            long previousEventTimestamp = previousRuntimeEvent.getTimestamp();

            processTimestampsForEvent( i, currentEventTimestamp, previousEventTimestamp );
            if (sortedTimestamps.isEmpty())
                break;
        }
        handleRemainingTimestamps();
    }

    private void handleTimestampsBeforeFirstEvent()
    {
        long firstEventTime = events.get( 0 ).getTimestamp();
        Long firstTimestamp = sortedTimestamps.get( 0 );

        while (firstTimestamp <= firstEventTime)
        {
            timeMarkerToPositionMap.put( firstTimestamp, 0 );
            sortedTimestamps.remove( 0 );
            if (!sortedTimestamps.isEmpty())
                firstTimestamp = sortedTimestamps.get( 0 );
            else
                break;
        }
    }

    private Long processTimestampsForEvent(int currentEventIndex, long currentEventTimestamp,
            long previousEventTimestamp)
    {
        Long firstTimestamp = sortedTimestamps.get( 0 );
        if (firstTimestamp < 0)
            LOG.error( "Timestamp has negative value" );

        while (firstTimestamp >= previousEventTimestamp && firstTimestamp < currentEventTimestamp)
        {
            computeTimestampsPosition( firstTimestamp, currentEventIndex );
            sortedTimestamps.remove( firstTimestamp );
            if (sortedTimestamps.isEmpty())
                break;
            firstTimestamp = sortedTimestamps.get( 0 );
        }
        return firstTimestamp;
    }

    private void handleRemainingTimestamps()
    {
        addValueForTimestamps( sortedTimestamps, widgetDimension );
    }

    private void addValueForTimestamps(List<Long> timestamps, int value)
    {
        for (Long timestamp : timestamps)
            timeMarkerToPositionMap.put( timestamp, value );
    }

    private void computeTimestampsPosition(long timeMarkerTimestamp, int currentEventPosition)
    {
        int allEventsSize = events.size();
        double relativePosition = ((double)currentEventPosition) / ((double)allEventsSize);
        double positionInWidgetDouble = widgetDimension * relativePosition;
        int positionInWidgetInPixels = (int)Math.round( positionInWidgetDouble );
        timeMarkerToPositionMap.put( timeMarkerTimestamp, positionInWidgetInPixels );
    }

    private void addDummyPositions()
    {
        addValueForTimestamps( sortedTimestamps, 0 );
    }
}
