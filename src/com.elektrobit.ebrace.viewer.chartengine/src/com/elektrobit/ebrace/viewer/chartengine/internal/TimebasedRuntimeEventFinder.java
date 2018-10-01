/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;

public class TimebasedRuntimeEventFinder
{
    private RuntimeEventProvider runtimeEventProvider = new GenericOSGIServiceTracker<RuntimeEventProvider>( RuntimeEventProvider.class )
            .getService();
    private static final int ONE_SECOND_IN_MILLIS = 1000;
    private List<RuntimeEvent<?>> resultsForNextRuntimeEvent = new ArrayList<RuntimeEvent<?>>();
    private List<RuntimeEvent<?>> resultsForLastRuntimeEvent = new ArrayList<RuntimeEvent<?>>();
    private RuntimeEvent<?> nextRuntimeEvent;
    private RuntimeEvent<?> lastRuntimeEvent;

    public TimebasedRuntimeEventFinder(long startTimestamp, List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        matchRuntimeEvents( startTimestamp, runtimeEventChannels );
    }

    TimebasedRuntimeEventFinder(long startTimestamp, List<RuntimeEventChannel<?>> runtimeEventChannels,
            RuntimeEventProvider runtimeEventProvider)
    {
        this.runtimeEventProvider = runtimeEventProvider;
        matchRuntimeEvents( startTimestamp, runtimeEventChannels );
    }

    private void matchRuntimeEvents(long startTimestamp, List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        matchNextRuntimeEvent( startTimestamp, ONE_SECOND_IN_MILLIS, runtimeEventChannels );
        matchLastRuntimeEvent( startTimestamp, ONE_SECOND_IN_MILLIS, runtimeEventChannels );
    }

    public boolean hasNextRuntimeEvent()
    {
        return nextRuntimeEvent != null;
    }

    public RuntimeEvent<?> getNextRuntimeEvent()
    {
        return nextRuntimeEvent;
    }

    private void matchNextRuntimeEvent(long startTimestamp, long searchInterval,
            List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        if (!isRuntimeEventProviderInvalid() && isTimestampBetweenFirstAndLastTimestamp( startTimestamp ))
        {
            List<RuntimeEvent<?>> runtimeEventsOfOneSecondAfterTimestamp = runtimeEventProvider
                    .getRuntimeEventsOfTimespan( startTimestamp, startTimestamp + searchInterval );
            resultsForNextRuntimeEvent = extractRuntimeEventOfChannel( runtimeEventsOfOneSecondAfterTimestamp,
                                                                       runtimeEventChannels );

            if (resultsForNextRuntimeEvent.isEmpty())
            {
                matchNextRuntimeEvent( startTimestamp + searchInterval, searchInterval, runtimeEventChannels );
            }
        }
        setNextRuntimeEvent();
    }

    private void setNextRuntimeEvent()
    {
        if (resultsForNextRuntimeEvent.size() > 0)
        {
            nextRuntimeEvent = resultsForNextRuntimeEvent.get( 0 );
        }
    }

    private boolean isTimestampBetweenFirstAndLastTimestamp(long startTimestamp)
    {
        long lastTimestamp = runtimeEventProvider.getLatestRuntimeEvent().getTimestamp();

        if (isTimestampBeforeFirstRuntimeEventsTimestamp( startTimestamp ))
        {
            resultsForNextRuntimeEvent.add( runtimeEventProvider.getFirstRuntimeEvent() );
            return false;
        }
        else if (isTimestampAfterLastRuntimeEventsTimestamp( startTimestamp, lastTimestamp ))
        {
            resultsForLastRuntimeEvent.add( runtimeEventProvider.getLatestRuntimeEvent() );
            return false;
        }

        return true;
    }

    private boolean isTimestampBeforeFirstRuntimeEventsTimestamp(long timestamp)
    {
        long timestampOfFirstRuntimeEvent = runtimeEventProvider.getFirstRuntimeEvent().getTimestamp();
        return timestamp < timestampOfFirstRuntimeEvent;
    }

    private boolean isTimestampAfterLastRuntimeEventsTimestamp(long timestamp, long lastTimestamp)
    {
        return timestamp > lastTimestamp;
    }

    private List<RuntimeEvent<?>> extractRuntimeEventOfChannel(List<RuntimeEvent<?>> runtimeEvents,
            List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        List<RuntimeEvent<?>> runtimeEventsOfRuntimeEventChannel = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> nextRuntimeEvent : runtimeEvents)
        {
            if (runtimeEventChannels.contains( nextRuntimeEvent.getRuntimeEventChannel() ))
            {
                runtimeEventsOfRuntimeEventChannel.add( nextRuntimeEvent );
            }
        }
        return runtimeEventsOfRuntimeEventChannel;
    }

    private boolean isRuntimeEventProviderInvalid()
    {
        return runtimeEventProvider == null;
    }

    public boolean hasLastRuntimeEvent()
    {
        return lastRuntimeEvent != null;
    }

    public RuntimeEvent<?> getLastRuntimeEvent()
    {
        return lastRuntimeEvent;
    }

    private void matchLastRuntimeEvent(long startTimestamp, long searchInterval,
            List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        if (!isRuntimeEventProviderInvalid() && isTimestampBetweenFirstAndLastTimestamp( startTimestamp ))
        {
            List<RuntimeEvent<?>> runtimeEventsOfOneSecondBeforeTimestamp = runtimeEventProvider
                    .getRuntimeEventsOfTimespan( startTimestamp - searchInterval, startTimestamp );
            resultsForLastRuntimeEvent = extractRuntimeEventOfChannel( runtimeEventsOfOneSecondBeforeTimestamp,
                                                                       runtimeEventChannels );

            if (resultsForLastRuntimeEvent.isEmpty())
            {
                matchLastRuntimeEvent( startTimestamp - searchInterval, searchInterval, runtimeEventChannels );
            }
        }
        setLastRuntimeEvent();
    }

    private void setLastRuntimeEvent()
    {
        if (resultsForLastRuntimeEvent.size() > 0)
        {
            int indexOfLastRuntimeEventInList = resultsForLastRuntimeEvent.size() - 1;
            lastRuntimeEvent = resultsForLastRuntimeEvent.get( indexOfLastRuntimeEventInList );
        }
    }
}
