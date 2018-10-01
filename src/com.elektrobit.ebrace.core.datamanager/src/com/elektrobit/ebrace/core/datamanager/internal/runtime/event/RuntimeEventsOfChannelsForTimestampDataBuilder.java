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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventsOfChannelsForTimestampDataBuilder
{
    private final List<RuntimeEventChannel<?>> runtimeEventChannels;
    private final long timestamp;
    private final List<RuntimeEvent<?>> sortedEvents;

    public RuntimeEventsOfChannelsForTimestampDataBuilder(List<RuntimeEventChannel<?>> runtimeEventChannels,
            long timestamp, List<RuntimeEvent<?>> sortedEvents)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "runtimeEventChannels", runtimeEventChannels );
        RangeCheckUtils.assertReferenceParameterNotNull( "timestamp", timestamp );
        RangeCheckUtils.assertReferenceParameterNotNull( "sortedEvents", sortedEvents );

        this.runtimeEventChannels = runtimeEventChannels;
        this.timestamp = timestamp;
        this.sortedEvents = sortedEvents;
    }

    public Map<RuntimeEventChannel<?>, RuntimeEvent<?>> build()
    {
        List<RuntimeEventChannel<?>> numericChannels = getNumericChannels( runtimeEventChannels );
        List<RuntimeEventChannel<?>> otherChannels = new ArrayList<>( runtimeEventChannels );
        otherChannels.removeAll( numericChannels );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> numericChannelsResult = getRuntimeEventsOfNumericChannelsForTimestamp( numericChannels,
                                                                                                                            timestamp,
                                                                                                                            sortedEvents );
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> otherChannelsResult = getRuntimeEventsOfOtherChannelsForTimestamp( otherChannels,
                                                                                                                        timestamp,
                                                                                                                        sortedEvents );

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = new HashMap<>();
        result.putAll( numericChannelsResult );
        result.putAll( otherChannelsResult );
        return result;
    }

    private List<RuntimeEventChannel<?>> getNumericChannels(List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        List<RuntimeEventChannel<?>> numericChannels = new ArrayList<>( runtimeEventChannels );
        numericChannels.removeIf( (channel) -> !Number.class.isAssignableFrom( channel.getUnit().getDataType() ) );
        return numericChannels;
    }

    private Map<RuntimeEventChannel<?>, RuntimeEvent<?>> getRuntimeEventsOfNumericChannelsForTimestamp(
            List<RuntimeEventChannel<?>> runtimeEventChannels, long timestamp, List<RuntimeEvent<?>> sortedEvents)
    {
        Set<RuntimeEventChannel<?>> channelsInSet = new HashSet<>( runtimeEventChannels );
        Set<RuntimeEventChannel<?>> channelsAfterTheirFirstValue = new HashSet<RuntimeEventChannel<?>>();
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> lastEventsOfChannels = new HashMap<RuntimeEventChannel<?>, RuntimeEvent<?>>();
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = new HashMap<RuntimeEventChannel<?>, RuntimeEvent<?>>();

        for (RuntimeEvent<?> event : sortedEvents)
        {
            RuntimeEventChannel<?> currentChannel = event.getRuntimeEventChannel();

            if (channelsInSet.contains( currentChannel ))
            {
                if (event.getTimestamp() >= timestamp)
                {
                    if (channelsAfterTheirFirstValue.contains( currentChannel )
                            && !result.containsKey( currentChannel ))
                    {
                        result.put( currentChannel, event );
                    }
                    lastEventsOfChannels.put( currentChannel, event );
                }

                if (!channelsAfterTheirFirstValue.contains( currentChannel ) && event.getTimestamp() > timestamp)
                {
                    result.put( currentChannel, null );
                }

                channelsAfterTheirFirstValue.add( currentChannel );
            }
        }

        result = clearResultsAfterLastTimestamp( result, lastEventsOfChannels, timestamp );
        addNullForMissingResults( runtimeEventChannels, result );

        return result;
    }

    private Map<RuntimeEventChannel<?>, RuntimeEvent<?>> clearResultsAfterLastTimestamp(
            Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result,
            Map<RuntimeEventChannel<?>, RuntimeEvent<?>> lastEventsOfChannels, long timestamp)
    {
        for (RuntimeEventChannel<?> channel : result.keySet())
        {
            RuntimeEvent<?> lastEvent = lastEventsOfChannels.get( channel );

            if (timestamp > lastEvent.getTimestamp())
            {
                result.put( channel, null );
            }
        }
        return result;
    }

    private void addNullForMissingResults(List<RuntimeEventChannel<?>> runtimeEventChannels,
            Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result)
    {
        for (RuntimeEventChannel<?> runtimeEventChannel : runtimeEventChannels)
        {
            if (!result.containsKey( runtimeEventChannel ))
            {
                result.put( runtimeEventChannel, null );
            }
        }
    }

    private Map<RuntimeEventChannel<?>, RuntimeEvent<?>> getRuntimeEventsOfOtherChannelsForTimestamp(
            List<RuntimeEventChannel<?>> runtimeEventChannels, long timestamp, List<RuntimeEvent<?>> sortedEvents)
    {
        Set<RuntimeEventChannel<?>> channelsInSet = new HashSet<>( runtimeEventChannels );
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = new HashMap<RuntimeEventChannel<?>, RuntimeEvent<?>>();

        for (RuntimeEvent<?> event : sortedEvents)
        {
            RuntimeEventChannel<?> currentChannel = event.getRuntimeEventChannel();

            if (channelsInSet.contains( currentChannel ))
            {
                if (event.getTimestamp() <= timestamp)
                {
                    result.put( currentChannel, event );
                }
            }

            if (event.getTimestamp() > timestamp)
            {
                break;
            }
        }

        addNullForMissingResults( runtimeEventChannels, result );

        return result;
    }
}
