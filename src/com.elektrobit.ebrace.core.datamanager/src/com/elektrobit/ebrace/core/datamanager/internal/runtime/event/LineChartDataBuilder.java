/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.util.RuntimeEventTimestampComparator;

public class LineChartDataBuilder
{
    private final static Logger LOG = Logger.getLogger( LineChartDataBuilder.class );

    private Double minValue = null;
    private Double maxValue = null;

    private Double maxValueStacked = null;

    private final List<RuntimeEventChannel<?>> channels;
    private final long startTimestamp;
    private final long endTimestamp;
    private List<RuntimeEvent<?>> allRuntimeEvents;
    private Map<RuntimeEventChannel<?>, List<RuntimeEvent<Number>>> seriesForChannels = null;
    private Map<RuntimeEventChannel<?>, RuntimeEvent<Number>> firstEventsBeforeTimeSpan = new HashMap<RuntimeEventChannel<?>, RuntimeEvent<Number>>();
    private Set<RuntimeEventChannel<?>> nullOnlyChannels;
    private final List<Long> timestamps = new ArrayList<Long>();
    private final boolean dataAsBars;

    private final Long aggregationTime;

    private final boolean aggregateForStackedMode;

    public LineChartDataBuilder(List<RuntimeEventChannel<?>> channels, long startTimestamp, long endTimestamp,
            List<RuntimeEvent<?>> allRuntimeEvents, boolean dataAsBars, Long aggregationTime,
            boolean aggregateForStackedMode)
    {
        this.channels = channels;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.allRuntimeEvents = allRuntimeEvents;
        this.dataAsBars = dataAsBars;
        this.aggregationTime = aggregationTime;
        this.aggregateForStackedMode = aggregateForStackedMode;
    }

    public LineChartData build()
    {
        filterEventsFromOtherChannels();
        aggregateRuntimeEvents();

        seriesForChannels = createEmptyListsForChannels( channels );
        nullOnlyChannels = new HashSet<RuntimeEventChannel<?>>( channels );

        RuntimeEvent<?> currentEvent = null;
        for (int i = 0; i < allRuntimeEvents.size(); i++)
        {
            currentEvent = allRuntimeEvents.get( i );
            rememberFirstEventBeforeStart( currentEvent );
            if (currentEvent.getTimestamp() < startTimestamp)
            {
                continue;
            }

            if (currentEvent.getTimestamp() > endTimestamp)
            {
                if (firstEventsBeforeTimeSpan != null)
                {
                    processFirstEventsBeforeTimeSpan();
                }
                processFirstEventsAfterTimespan( i );
                break;
            }

            if (firstEventsBeforeTimeSpan != null)
            {
                processFirstEventsBeforeTimeSpan();
            }
            int lastProcessedIndex = processEventsWithSameTimestamp( i, allRuntimeEvents );
            i = lastProcessedIndex;
        }

        LineChartData result = buildResult();
        return result;
    }

    private void filterEventsFromOtherChannels()
    {
        Set<RuntimeEventChannel<?>> channelsInSet = new HashSet<>( channels );
        List<RuntimeEvent<?>> filteredList = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> runtimeEvent : allRuntimeEvents)
        {
            RuntimeEventChannel<?> currentChannel = runtimeEvent.getRuntimeEventChannel();
            if (channelsInSet.contains( currentChannel ))
            {
                filteredList.add( runtimeEvent );
            }
        }
        allRuntimeEvents = filteredList;
    }

    private void processFirstEventsAfterTimespan(int nextIndexToProcess)
    {
        List<RuntimeEvent<?>> nextEventsForChannel = getOneNextEventForEachChannel( nextIndexToProcess );
        for (int i = 0; i < nextEventsForChannel.size(); i++)
        {
            int lastProcessedIndex = processEventsWithSameTimestamp( i, nextEventsForChannel );
            i = lastProcessedIndex;
        }
    }

    private List<RuntimeEvent<?>> getOneNextEventForEachChannel(int nextIndexToProcess)
    {
        List<RuntimeEventChannel<?>> remainingChannelsToProcess = new ArrayList<RuntimeEventChannel<?>>();
        remainingChannelsToProcess.addAll( channels );
        List<RuntimeEvent<?>> nextEventsForEachChannel = new ArrayList<RuntimeEvent<?>>();

        for (int i = nextIndexToProcess; i < allRuntimeEvents.size(); i++)
        {
            if (remainingChannelsToProcess.isEmpty())
            {
                break;
            }

            RuntimeEvent<?> currentEvent = allRuntimeEvents.get( i );
            RuntimeEventChannel<?> currentChannel = currentEvent.getRuntimeEventChannel();

            if (remainingChannelsToProcess.contains( currentChannel ))
            {
                remainingChannelsToProcess.remove( currentChannel );
                nextEventsForEachChannel.add( currentEvent );
            }
        }
        return nextEventsForEachChannel;
    }

    private void processFirstEventsBeforeTimeSpan()
    {
        List<RuntimeEvent<?>> eventsToProcess = new ArrayList<RuntimeEvent<?>>();
        eventsToProcess.addAll( firstEventsBeforeTimeSpan.values() );
        firstEventsBeforeTimeSpan = null;
        Collections.sort( eventsToProcess, new RuntimeEventTimestampComparator() );

        for (int i = 0; i < eventsToProcess.size(); i++)
        {
            int lastProcessedIndex = processEventsWithSameTimestamp( i, eventsToProcess );
            i = lastProcessedIndex;
        }
    }

    private void aggregateRuntimeEvents()
    {
        if (aggregationTime == null)
        {
            return;
        }
        allRuntimeEvents = aggregateEvents( allRuntimeEvents );
        Collections.sort( allRuntimeEvents, new RuntimeEventTimestampComparator() );
        removeZeroOnlyChannels();
    }

    private void removeZeroOnlyChannels()
    {
        List<RuntimeEventChannel<?>> zeroChannels = findZeroOnlyChannels();
        removeEventsOfChannels( allRuntimeEvents, zeroChannels );
        channels.removeAll( zeroChannels );
    }

    private List<RuntimeEventChannel<?>> findZeroOnlyChannels()
    {
        Set<RuntimeEventChannel<?>> nonZeroChannels = new HashSet<RuntimeEventChannel<?>>();
        for (RuntimeEvent<?> runtimeEvent : allRuntimeEvents)
        {
            Object value = runtimeEvent.getValue();
            if (!value.equals( 0 ) && !value.equals( 0.0 ))
            {
                nonZeroChannels.add( runtimeEvent.getRuntimeEventChannel() );
            }
        }

        List<RuntimeEventChannel<?>> zeroChannels = new ArrayList<RuntimeEventChannel<?>>( channels );
        zeroChannels.removeAll( nonZeroChannels );
        return zeroChannels;
    }

    private void removeEventsOfChannels(List<RuntimeEvent<?>> events, List<RuntimeEventChannel<?>> channels)
    {
        events.removeIf( event -> channels.contains( event.getRuntimeEventChannel() ) );
    }

    @SuppressWarnings("unchecked")
    private List<RuntimeEvent<?>> aggregateEvents(List<RuntimeEvent<?>> allRuntimeEvents)
    {
        List<RuntimeEvent<Number>> typedEvents = new ArrayList<RuntimeEvent<Number>>( allRuntimeEvents.size() );
        typedEvents.addAll( (Collection<? extends RuntimeEvent<Number>>)allRuntimeEvents );

        List<RuntimeEvent<Number>> aggregatedList = new NumericRuntimeEventAggregator<Number>( aggregationTime,
                                                                                               aggregateForStackedMode )
                                                                                                       .aggregateRuntimeEvents( typedEvents );

        List<RuntimeEvent<?>> notTypedEvents = new ArrayList<RuntimeEvent<?>>( allRuntimeEvents.size() );
        notTypedEvents.addAll( aggregatedList );
        return notTypedEvents;
    }

    @SuppressWarnings("unchecked")
    private void rememberFirstEventBeforeStart(RuntimeEvent<?> currentEvent)
    {
        if (firstEventsBeforeTimeSpan != null && currentEvent.getTimestamp() < startTimestamp)
        {
            firstEventsBeforeTimeSpan.put( currentEvent.getRuntimeEventChannel(), (RuntimeEvent<Number>)currentEvent );
        }
    }

    private LineChartData buildResult()
    {
        if (timestamps.isEmpty())
        {
            return buildDummyResult();
        }
        else
        {
            return buildResultDirectly();
        }
    }

    private LineChartData buildResultDirectly()
    {
        Map<RuntimeEventChannel<?>, List<Number>> numberSeriesForChannels = new HashMap<RuntimeEventChannel<?>, List<Number>>();

        for (Entry<RuntimeEventChannel<?>, List<RuntimeEvent<Number>>> entry : seriesForChannels.entrySet())
        {
            List<RuntimeEvent<Number>> eventsList = entry.getValue();
            List<Number> numbersList = new ArrayList<Number>();

            for (RuntimeEvent<Number> runtimeEvent : eventsList)
            {
                if (runtimeEvent != null)
                {
                    numbersList.add( runtimeEvent.getValue() );
                }
                else
                {
                    numbersList.add( null );
                }
            }

            numberSeriesForChannels.put( entry.getKey(), numbersList );
        }
        return new LineChartDataImpl( numberSeriesForChannels, timestamps, minValue, maxValue, maxValueStacked );
    }

    private LineChartData buildDummyResult()
    {
        double resultMinValue = minValue == null ? 0.0 : minValue;
        double resultMaxValue = maxValue == null ? 0.0 : maxValue;
        double resultMaxValueStacked = maxValueStacked == null ? 0.0 : maxValueStacked;
        timestamps.add( startTimestamp );
        Map<RuntimeEventChannel<?>, List<Number>> numberSeriesForChannels = new HashMap<RuntimeEventChannel<?>, List<Number>>();

        for (RuntimeEventChannel<?> channel : seriesForChannels.keySet())
        {
            List<Number> resultDummyList = new ArrayList<Number>();
            resultDummyList.add( 0.0 );
            numberSeriesForChannels.put( channel, resultDummyList );
        }

        return new LineChartDataImpl( numberSeriesForChannels,
                                      timestamps,
                                      resultMinValue,
                                      resultMaxValue,
                                      resultMaxValueStacked );
    }

    @SuppressWarnings("unchecked")
    private int processEventsWithSameTimestamp(int startIndex, List<RuntimeEvent<?>> eventsToProcess)
    {
        long timestampToProcess = eventsToProcess.get( startIndex ).getTimestamp();
        int allEventsSize = eventsToProcess.size();
        int i = startIndex;
        double sum = 0;
        RuntimeEvent<Number> currentRuntimeEvent = null;
        if (dataAsBars)
        {
            addLastTimestampAgain();
        }
        while (i < allEventsSize && eventsToProcess.get( i ).getTimestamp() == timestampToProcess)
        {
            currentRuntimeEvent = (RuntimeEvent<Number>)eventsToProcess.get( i );
            RuntimeEventChannel<Number> channel = currentRuntimeEvent.getRuntimeEventChannel();
            List<RuntimeEvent<Number>> foundSeriesForChannel = seriesForChannels.get( channel );
            if (foundSeriesForChannel != null)
            {
                if (dataAsBars)
                {
                    if (!foundSeriesForChannel.isEmpty() && !nullOnlyChannels.contains( channel ))
                    {
                        foundSeriesForChannel.add( currentRuntimeEvent );
                        foundSeriesForChannel.add( currentRuntimeEvent );
                    }
                    else
                    {
                        /*
                         * When presenting values as bars, and this is the first value (all previous values are null),
                         * add the value only once, in oder not to show the horizontal line before first value
                         */
                        foundSeriesForChannel.add( null );
                        foundSeriesForChannel.add( currentRuntimeEvent );
                    }
                    nullOnlyChannels.remove( channel );
                }
                else
                {
                    foundSeriesForChannel.add( currentRuntimeEvent );
                }
                setMinMax( currentRuntimeEvent.getValue() );
                sum = sum + currentRuntimeEvent.getValue().doubleValue();
            }
            i++;
            setMinMaxStacked( sum );
        }
        int longestSeriesSize = getLongestListSize( seriesForChannels.values() );
        addNullsToFitSize( seriesForChannels.values(), longestSeriesSize );
        addTimestampMultipleTimes( timestamps, timestampToProcess, longestSeriesSize - timestamps.size() );
        int lastProcessedIndex = --i;
        return lastProcessedIndex;
    }

    private void setMinMaxStacked(double value)
    {
        Number number = value;
        double numberDouble = number.doubleValue();
        if (maxValueStacked == null || numberDouble > maxValueStacked)
        {
            maxValueStacked = numberDouble;
        }
    }

    private void addLastTimestampAgain()
    {
        if (!timestamps.isEmpty())
        {
            long lastTimestamp = timestamps.get( timestamps.size() - 1 );
            timestamps.add( lastTimestamp );
        }
    }

    private void setMinMax(Object value)
    {
        if (value instanceof Number)
        {
            Number number = (Number)value;
            double numberDouble = number.doubleValue();
            if (minValue == null || numberDouble < minValue)
            {
                minValue = numberDouble;
            }
            if (maxValue == null || numberDouble > maxValue)
            {
                maxValue = numberDouble;
            }
        }
    }

    private int getLongestListSize(Collection<List<RuntimeEvent<Number>>> lists)
    {
        int maxSize = 0;
        for (List<?> list : lists)
        {
            if (list.size() > maxSize)
            {
                maxSize = list.size();
            }
        }

        return maxSize;
    }

    private void addNullsToFitSize(Collection<List<RuntimeEvent<Number>>> allLists, int desiredSize)
    {
        for (List<RuntimeEvent<Number>> list : allLists)
        {
            if (list.size() > desiredSize)
            {
                LOG.error( "List too long, size " + list.size() + " desired size " + desiredSize );
                continue;
            }

            while (list.size() < desiredSize)
            {
                list.add( null );
            }
        }
    }

    private void addTimestampMultipleTimes(List<Long> timestampsList, long timestampToAdd, int times)
    {
        for (int i = 0; i < times; i++)
        {
            timestampsList.add( timestampToAdd );
        }
    }

    private Map<RuntimeEventChannel<?>, List<RuntimeEvent<Number>>> createEmptyListsForChannels(
            List<RuntimeEventChannel<?>> channels)
    {
        Map<RuntimeEventChannel<?>, List<RuntimeEvent<Number>>> seriesForChannels = new HashMap<RuntimeEventChannel<?>, List<RuntimeEvent<Number>>>();
        for (RuntimeEventChannel<?> runtimeEventChannel : channels)
        {
            seriesForChannels.put( runtimeEventChannel, new ArrayList<RuntimeEvent<Number>>() );
        }
        return seriesForChannels;
    }
}
