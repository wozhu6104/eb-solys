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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.profiling.PerformanceUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class EventTimestampPositionInListConverter
{
    private static final Logger LOG = Logger.getLogger( EventTimestampPositionInListConverter.class );

    private final List<TimebasedObject> allItems;
    private final List<TimebasedObject> itemsToBeMarked;
    private final int widgetDimension;

    private final Map<TimebasedObject, Integer> itemToRulerPositionMap = new HashMap<TimebasedObject, Integer>();

    public EventTimestampPositionInListConverter(List<TimebasedObject> allItems,
            Collection<TimebasedObject> itemsToBeMarked, int widgetDimension)
    {
        this.allItems = allItems;
        this.itemsToBeMarked = new ArrayList<TimebasedObject>( itemsToBeMarked );
        Collections.sort( this.itemsToBeMarked );
        this.widgetDimension = widgetDimension;
    }

    public Map<TimebasedObject, Integer> getTimestampPositions()
    {
        if (allItems != null && !allItems.isEmpty() && !itemsToBeMarked.isEmpty())
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
        return itemToRulerPositionMap;
    }

    private void computePositionsFromEvents()
    {
        handleTimestampsBeforeFirstEvent();

        if (itemsToBeMarked.isEmpty())
            return;

        TimebasedObject currentRuntimeEvent = null;
        TimebasedObject previousRuntimeEvent = null;

        for (int i = 0; i < allItems.size(); i++)
        {
            previousRuntimeEvent = currentRuntimeEvent;
            currentRuntimeEvent = allItems.get( i );
            if (previousRuntimeEvent == null)
                continue;

            long currentEventTimestamp = currentRuntimeEvent.getTimestamp();
            long previousEventTimestamp = previousRuntimeEvent.getTimestamp();

            processTimestampsForEvent( i, currentEventTimestamp, previousEventTimestamp );
            if (itemsToBeMarked.isEmpty())
                break;
        }
        handleRemainingTimestamps();
    }

    private void handleTimestampsBeforeFirstEvent()
    {
        long firstEventTime = allItems.get( 0 ).getTimestamp();
        TimebasedObject firstItem = itemsToBeMarked.get( 0 );

        while (firstItem.getTimestamp() <= firstEventTime)
        {
            itemToRulerPositionMap.put( firstItem, 0 );
            itemsToBeMarked.remove( 0 );
            if (!itemsToBeMarked.isEmpty())
                firstItem = itemsToBeMarked.get( 0 );
            else
                break;
        }
    }

    private TimebasedObject processTimestampsForEvent(int currentEventIndex, long currentEventTimestamp,
            long previousEventTimestamp)
    {
        TimebasedObject firstItem = itemsToBeMarked.get( 0 );
        long firstItemTimestamp = firstItem.getTimestamp();
        if (firstItemTimestamp < 0)
            LOG.error( "Timestamp has negative value" );

        while (firstItemTimestamp >= previousEventTimestamp && firstItemTimestamp < currentEventTimestamp)
        {
            computeTimestampsPosition( firstItem, currentEventIndex );
            itemsToBeMarked.remove( firstItem );
            if (itemsToBeMarked.isEmpty())
                break;
            firstItem = itemsToBeMarked.get( 0 );
            firstItemTimestamp = firstItem.getTimestamp();
        }
        return firstItem;
    }

    private void handleRemainingTimestamps()
    {
        addValueForTimestamps( itemsToBeMarked, widgetDimension );
    }

    private void addValueForTimestamps(List<TimebasedObject> timestamps, int value)
    {
        for (TimebasedObject timestamp : timestamps)
            itemToRulerPositionMap.put( timestamp, value );
    }

    private void computeTimestampsPosition(TimebasedObject item, int currentEventPosition)
    {
        int allEventsSize = allItems.size();
        double relativePosition = ((double)currentEventPosition) / ((double)allEventsSize);
        double positionInWidgetDouble = widgetDimension * relativePosition;
        int positionInWidgetInPixels = (int)Math.round( positionInWidgetDouble );
        itemToRulerPositionMap.put( item, positionInWidgetInPixels );
    }

    private void addDummyPositions()
    {
        addValueForTimestamps( itemsToBeMarked, 0 );
    }
}
