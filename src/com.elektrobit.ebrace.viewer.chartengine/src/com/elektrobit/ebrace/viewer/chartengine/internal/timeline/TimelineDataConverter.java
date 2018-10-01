/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.timeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import lombok.Getter;

public class TimelineDataConverter
{
    private final Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> input;
    @Getter
    private final Set<SColor> allColors = new HashSet<>();

    public TimelineDataConverter(Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> input)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "input", input );
        this.input = input;
    }

    public List<TimeGraphEntry> buildInput()
    {
        Set<Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>>> entrySet = input.entrySet();
        List<TimeGraphEntry> allChannelEntries = new ArrayList<TimeGraphEntry>();
        for (Entry<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> entry : entrySet)
        {
            RuntimeEventChannel<STimeSegment> channel = entry.getKey();
            List<STimeSegment> timeSegments = entry.getValue();
            SolysTimeGraphEntry channelEntry = createChannelEntry( channel, timeSegments );
            createTimeEvents( channelEntry, timeSegments );
            allChannelEntries.add( channelEntry );
        }
        return allChannelEntries;
    }

    private SolysTimeGraphEntry createChannelEntry(RuntimeEventChannel<STimeSegment> channel,
            List<STimeSegment> timeSegments)
    {
        long startTimestamp = getStartTimestamp( timeSegments );
        long endTimestamp = getEndTimestamp( timeSegments );
        long startTimestampNanos = microsToNanos( startTimestamp );
        long endTimestampNanos = microsToNanos( endTimestamp );
        SolysTimeGraphEntry channelEntry = new SolysTimeGraphEntry( channel,
                                                                    channel.getName(),
                                                                    startTimestampNanos,
                                                                    endTimestampNanos );

        return channelEntry;
    }

    private long getStartTimestamp(List<STimeSegment> timeSegments)
    {
        if (timeSegments.isEmpty())
        {
            return 0;
        }
        else
        {
            return timeSegments.get( 0 ).getStartTime();
        }
    }

    private long getEndTimestamp(List<STimeSegment> timeSegments)
    {
        if (timeSegments.isEmpty())
        {
            return 0;
        }
        else
        {
            return timeSegments.get( timeSegments.size() - 1 ).getEndTime();
        }
    }

    private void createTimeEvents(SolysTimeGraphEntry channelEntry, List<STimeSegment> timeSegments)
    {
        List<ITimeEvent> eventList = new ArrayList<ITimeEvent>();
        for (STimeSegment timeSegment : timeSegments)
        {
            allColors.add( timeSegment.getColor() );
            long startTime = timeSegment.getStartTime();
            long endTime = timeSegment.getEndTime();
            long duration = endTime - startTime;
            SolysTimeEvent timeEvent = new SolysTimeEvent( timeSegment,
                                                           channelEntry,
                                                           microsToNanos( startTime ),
                                                           microsToNanos( duration ) );
            eventList.add( timeEvent );
        }
        channelEntry.setEventList( eventList );
    }

    public static long microsToNanos(long micros)
    {
        return micros * 1000;
    }

    public static long nanosToMicros(long micros)
    {
        return Math.round( micros / 1000.0 );
    }
}
