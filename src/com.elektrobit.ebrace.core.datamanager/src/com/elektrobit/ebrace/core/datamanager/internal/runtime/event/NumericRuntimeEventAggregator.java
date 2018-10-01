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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import lombok.extern.log4j.Log4j;

@Log4j
public class NumericRuntimeEventAggregator<T extends Number>
{
    private final Long aggregationTime;
    private final boolean aggregateForStackedMode;
    private List<Long> middleTimestampsOfAggregationIntervals;

    /**
     * Keeps min and max value in each aggregation period and removes rest of the events.
     * 
     * @param aggregationTime
     * @param aggregateForStackedMode
     *            If true, only maximum in each interval is taken, and it is assigned the first timestamp from interval,
     *            to keep all aggregated timestamps aligned. (When showing chart data as stacked, each channel should
     *            have value in each timestamp provided, otherwise chart is showing "bumps")
     */
    public NumericRuntimeEventAggregator(Long aggregationTime, boolean aggregateForStackedMode)
    {
        this.aggregationTime = aggregationTime;
        this.aggregateForStackedMode = aggregateForStackedMode;
    }

    public List<RuntimeEvent<T>> aggregateRuntimeEvents(List<RuntimeEvent<T>> runtimeEventsToAggregate)
    {
        return mergeTimeSlicesToOneTimeSlice( extractMinMaxOfTimeSlices( partRuntimeEventsInTimeSlices( runtimeEventsToAggregate ) ) );
    }

    private List<List<RuntimeEvent<T>>> partRuntimeEventsInTimeSlices(final List<RuntimeEvent<T>> inputRuntimeEventList)
    {
        List<List<RuntimeEvent<T>>> partedRuntimeEvents = new ArrayList<List<RuntimeEvent<T>>>();

        if (inputRuntimeEventList.size() > 0)
        {
            List<RuntimeEvent<T>> currentRuntimeEventTimeSlice = new ArrayList<RuntimeEvent<T>>();
            long timestampOfStartRuntimeEvent = inputRuntimeEventList.get( 0 ).getTimestamp();
            for (RuntimeEvent<T> nextRuntimeEvent : inputRuntimeEventList)
            {
                long timeDiffStartAndCurrentRuntimeEvent = nextRuntimeEvent.getTimestamp()
                        - timestampOfStartRuntimeEvent;
                if (timeDiffStartAndCurrentRuntimeEvent >= aggregationTime)
                {
                    if (currentRuntimeEventTimeSlice.size() > 0)
                    {
                        partedRuntimeEvents.add( currentRuntimeEventTimeSlice );
                        currentRuntimeEventTimeSlice = new ArrayList<RuntimeEvent<T>>();
                        timestampOfStartRuntimeEvent = nextRuntimeEvent.getTimestamp();
                    }
                }
                currentRuntimeEventTimeSlice.add( nextRuntimeEvent );
            }
            if (currentRuntimeEventTimeSlice.size() > 0)
            {
                partedRuntimeEvents.add( currentRuntimeEventTimeSlice );
            }
        }

        log.debug( "" );
        log.debug( "Parted Events" );
        int j = 0;
        for (List<RuntimeEvent<T>> list : partedRuntimeEvents)
        {
            log.debug( "Part " + j );
            for (RuntimeEvent<T> runtimeEvent : list)
            {
                log.debug( runtimeEvent.getValue() + " [" + runtimeEvent.getTimestamp() + ","
                        + runtimeEvent.getRuntimeEventChannel().getName() + "], " );
            }
            log.debug( ";" );
        }
        return partedRuntimeEvents;
    }

    private List<Map<RuntimeEventChannel<?>, MinMax<T>>> extractMinMaxOfTimeSlices(
            final List<List<RuntimeEvent<T>>> partedRuntimeEvents)
    {
        List<Map<RuntimeEventChannel<?>, MinMax<T>>> extractedMinMaxTimeSlices = new ArrayList<Map<RuntimeEventChannel<?>, MinMax<T>>>();
        middleTimestampsOfAggregationIntervals = new ArrayList<Long>();

        for (List<RuntimeEvent<T>> nextTimeSlice : partedRuntimeEvents)
        {
            Map<RuntimeEventChannel<?>, MinMax<T>> currentSliceChannelsMinMax = new HashMap<RuntimeEventChannel<?>, MinMax<T>>();

            long middleOfIntervalTimestamp = findMiddleOfIntervalTimestamp( nextTimeSlice );
            middleTimestampsOfAggregationIntervals.add( middleOfIntervalTimestamp );

            if (aggregateForStackedMode)// TODO compare performance to non-stacked
            {
                findMaxStackedValueAndSetEachChannelValueToMap( nextTimeSlice, currentSliceChannelsMinMax );
            }
            else
            {
                for (int i = 0; i < nextTimeSlice.size(); i++)
                {
                    RuntimeEvent<T> currentEventEvent = nextTimeSlice.get( i );
                    processValuesForMinMaxInChannel( currentSliceChannelsMinMax, currentEventEvent );
                }
            }
            extractedMinMaxTimeSlices.add( currentSliceChannelsMinMax );
        }
        return extractedMinMaxTimeSlices;
    }

    private long findMiddleOfIntervalTimestamp(List<RuntimeEvent<T>> nextTimeSlice)
    {
        RuntimeEvent<T> firstEvent = nextTimeSlice.get( 0 );
        RuntimeEvent<T> lastEvent = nextTimeSlice.get( nextTimeSlice.size() - 1 );

        long middleOfIntervalTimestamp = (firstEvent.getTimestamp() + lastEvent.getTimestamp()) / 2;
        return middleOfIntervalTimestamp;
    }

    private void findMaxStackedValueAndSetEachChannelValueToMap(List<RuntimeEvent<T>> timeSlice,
            Map<RuntimeEventChannel<?>, MinMax<T>> currentSliceChannelsMinMax)
    {
        Map<Long, List<RuntimeEvent<T>>> eventsForTimestamp = groupEventsWithSameTimestamp( timeSlice );
        Map<Long, Double> aggregatedMaximumsForEachTimestamp = aggregateEventsForEachTimestamp( eventsForTimestamp );

        Entry<Long, Double> entryWithHighestValue = findEntryWithHighestValue( aggregatedMaximumsForEachTimestamp );

        List<RuntimeEvent<T>> seriesWithHighestAggregatedMaximum = eventsForTimestamp
                .get( entryWithHighestValue.getKey() );

        seriesWithHighestAggregatedMaximum.forEach( event -> currentSliceChannelsMinMax
                .put( event.getRuntimeEventChannel(), new MinMax<T>( null, event.getValue() ) ) );
    }

    private Map<Long, List<RuntimeEvent<T>>> groupEventsWithSameTimestamp(List<RuntimeEvent<T>> timeSlice)
    {
        Map<Long, List<RuntimeEvent<T>>> eventsForTimestamp = new HashMap<>();

        for (int i = 0; i < timeSlice.size(); i++)
        {
            RuntimeEvent<T> currentEvent = timeSlice.get( i );
            List<RuntimeEvent<T>> eventsWithSameTimestamp = new ArrayList<>();
            long currentTimestamp = currentEvent.getTimestamp();

            int lastProcessedIndex = findAllEventsWithTimestamp( currentTimestamp,
                                                                 i,
                                                                 timeSlice,
                                                                 eventsWithSameTimestamp );
            i = lastProcessedIndex;
            eventsForTimestamp.put( currentTimestamp, eventsWithSameTimestamp );
        }
        return eventsForTimestamp;
    }

    private Map<Long, Double> aggregateEventsForEachTimestamp(Map<Long, List<RuntimeEvent<T>>> eventsForTimestamp)
    {
        Map<Long, Double> aggregatedMaximumsForEachTimestamp = new HashMap<>();
        for (Entry<Long, List<RuntimeEvent<T>>> entry : eventsForTimestamp.entrySet())
        {
            Long timestamp = entry.getKey();
            List<RuntimeEvent<T>> eventsForThisTimestamp = entry.getValue();
            double sum = eventsForThisTimestamp.stream().mapToDouble( (e) -> e.getValue().doubleValue() ).sum();

            aggregatedMaximumsForEachTimestamp.put( timestamp, sum );
        }
        return aggregatedMaximumsForEachTimestamp;
    }

    private Entry<Long, Double> findEntryWithHighestValue(Map<Long, Double> aggregatedMaximumsForEachTimestamp)
    {
        Entry<Long, Double> highesStackedValueEntry = null;

        for (Entry<Long, Double> entry : aggregatedMaximumsForEachTimestamp.entrySet())
        {
            if (highesStackedValueEntry == null)
            {
                highesStackedValueEntry = entry;
            }
            else
            {
                highesStackedValueEntry = entry.getValue() > highesStackedValueEntry.getValue()
                        ? entry
                        : highesStackedValueEntry;
            }
        }

        return highesStackedValueEntry;
    }

    private int findAllEventsWithTimestamp(long currentTimestamp, int startIndex, List<RuntimeEvent<T>> timeSlice,
            List<RuntimeEvent<T>> eventsForThisTimestamp)
    {
        int i = startIndex;

        do
        {
            RuntimeEvent<T> currentEvent = timeSlice.get( i );
            if (currentEvent.getTimestamp() == currentTimestamp)
            {
                eventsForThisTimestamp.add( currentEvent );
                i++;
            }
            else
            {
                break;
            }
        }
        while (i < timeSlice.size());

        int lastProcessedIndex = i - 1;
        return lastProcessedIndex;
    }

    @SuppressWarnings("unchecked")
    private void processValuesForMinMaxInChannel(Map<RuntimeEventChannel<?>, MinMax<T>> currentSliceChannelsMinMax,
            RuntimeEvent<T> currentEventEvent)
    {
        Comparable<Comparable<?>> currentValue = null;
        currentValue = (Comparable<Comparable<?>>)currentEventEvent.getValue();

        if (!(currentValue instanceof Comparable))
        {
            log.error( "Aggregation not possible, because RuntimeEvent data type is not comparable." );
            return;
        }

        RuntimeEventChannel<T> channel = currentEventEvent.getRuntimeEventChannel();
        MinMax<T> minMax = createOrGetChannelMinMax( currentSliceChannelsMinMax, channel );

        if (minMax.getMin() == null && minMax.getMax() == null)// TODO extract to methods?
        {
            minMax.setMax( currentEventEvent.getValue() );
            return;
        }

        if (minMax.getMax() != null)
        {
            Comparable<Comparable<?>> maxValue = (Comparable<Comparable<?>>)minMax.getMax();
            if (currentValue.compareTo( maxValue ) > 0)
            {
                minMax.setMax( currentEventEvent.getValue() );
                return;
            }
        }
        if (minMax.getMax() == null && !minMax.contains( currentEventEvent.getValue() ))
        {
            minMax.setMax( currentEventEvent.getValue() );
            return;
        }

        if (aggregateForStackedMode)// in stacked mode we are looking only for maximums
        {
            return;
        }

        if (minMax.getMin() != null)
        {
            Comparable<Comparable<?>> minValue = (Comparable<Comparable<?>>)minMax.getMin();
            if (currentValue.compareTo( minValue ) < 0)
            {
                minMax.setMin( currentEventEvent.getValue() );
                return;
            }
        }

        boolean containsMin = !minMax.contains( currentEventEvent.getValue() );
        if (minMax.getMin() == null && containsMin)
        {
            minMax.setMin( currentEventEvent.getValue() );
        }

    }

    private MinMax<T> createOrGetChannelMinMax(Map<RuntimeEventChannel<?>, MinMax<T>> currentSliceChannelsMinMax,
            RuntimeEventChannel<T> channel)
    {
        MinMax<T> minMax = currentSliceChannelsMinMax.get( channel );
        if (minMax == null)
        {
            minMax = new MinMax<T>();
            currentSliceChannelsMinMax.put( channel, minMax );
        }
        return minMax;
    }

    private List<RuntimeEvent<T>> mergeTimeSlicesToOneTimeSlice(
            final List<Map<RuntimeEventChannel<?>, MinMax<T>>> extractedMinMaxTimeSlices)
    {
        List<RuntimeEvent<T>> mergedRuntimeEvents = new ArrayList<RuntimeEvent<T>>();

        for (int i = 0; i < extractedMinMaxTimeSlices.size(); i++)
        {
            Map<RuntimeEventChannel<?>, MinMax<T>> currentIntervalMap = extractedMinMaxTimeSlices.get( i );
            Long middleOfCurrentInterval = middleTimestampsOfAggregationIntervals.get( i );
            Set<Entry<RuntimeEventChannel<?>, MinMax<T>>> mapEntrySet = currentIntervalMap.entrySet();

            for (Entry<RuntimeEventChannel<?>, MinMax<T>> entry : mapEntrySet)
            {
                @SuppressWarnings("unchecked")
                RuntimeEventChannel<T> channel = (RuntimeEventChannel<T>)entry.getKey();
                MinMax<T> minMax = entry.getValue();

                if (minMax.getMin() != null)
                {
                    RuntimeEvent<T> newEvent = new RuntimeEventObjectImpl<T>( middleOfCurrentInterval,
                                                                              channel,
                                                                              0L,
                                                                              minMax.getMin(),
                                                                              null,
                                                                              null );
                    mergedRuntimeEvents.add( newEvent );
                }

                if (minMax.getMax() != null)
                {
                    RuntimeEvent<T> newEvent = new RuntimeEventObjectImpl<T>( middleOfCurrentInterval,
                                                                              channel,
                                                                              0L,
                                                                              minMax.getMax(),
                                                                              null,
                                                                              null );
                    mergedRuntimeEvents.add( newEvent );
                }
            }

        }

        log.debug( "" );
        log.debug( "Aggregated Events" );
        for (RuntimeEvent<T> runtimeEvent : mergedRuntimeEvents)
        {
            log.debug( runtimeEvent.getValue() + " [" + runtimeEvent.getTimestamp() + "]["
                    + runtimeEvent.getRuntimeEventChannel().getName() + "], " );
        }
        log.debug( ";" );
        return mergedRuntimeEvents;
    }
}
