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

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartEntry;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;

public class GanttChartDataBuilder
{
    private final List<RuntimeEventChannel<?>> channels;
    private final long startTimestamp;
    private final long endTimestamp;
    private final RuntimeEventProvider runtimeEventProvider;
    private final Long aggregationTime;

    public GanttChartDataBuilder(List<RuntimeEventChannel<?>> channels, long startTimestamp, long endTimestamp,
            RuntimeEventProvider runtimeEventProvider, Long aggregationTime)
    {
        this.channels = channels;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.runtimeEventProvider = runtimeEventProvider;
        this.aggregationTime = aggregationTime;
    }

    public GanttChartData build()
    {
        List<GanttChartEntry[]> ganttChartDataSet = new ArrayList<GanttChartEntry[]>();

        Map<RuntimeEventChannel<?>, List<GanttChartEntry>> channelToGantEntryMap = findGanttEntriesForChannels( channels );
        int maxNumberOfGanttEntries = 0;
        for (RuntimeEventChannel<?> nextRuntimeEventChannel : channelToGantEntryMap.keySet())
        {
            if (channelToGantEntryMap.get( nextRuntimeEventChannel ).size() > maxNumberOfGanttEntries)
            {
                maxNumberOfGanttEntries = channelToGantEntryMap.get( nextRuntimeEventChannel ).size();
            }
        }

        for (int i = 0; i < maxNumberOfGanttEntries; i++)
        {
            GanttChartEntry[] entries = new GanttChartEntry[channels.size()];
            for (RuntimeEventChannel<?> nextRuntimeEventChannel : channelToGantEntryMap.keySet())
            {
                if (i < channelToGantEntryMap.get( nextRuntimeEventChannel ).size())
                {
                    int seriesIndex = channels.indexOf( nextRuntimeEventChannel );
                    if (seriesIndex > -1)
                        entries[seriesIndex] = channelToGantEntryMap.get( nextRuntimeEventChannel ).get( i );
                }
            }
            ganttChartDataSet.add( entries );
        }
        return new GanttChartDataImpl( channels, ganttChartDataSet );
    }

    private Map<RuntimeEventChannel<?>, List<GanttChartEntry>> findGanttEntriesForChannels(
            List<RuntimeEventChannel<?>> channels)
    {
        Map<RuntimeEventChannel<?>, List<GanttChartEntry>> map = new HashMap<RuntimeEventChannel<?>, List<GanttChartEntry>>();
        for (RuntimeEventChannel<?> nextRuntimeEventChannel : channels)
        {
            List<GanttChartEntry> ganttEntriesOfRuntimeEventChannel = findGanttEntriesForChannel( nextRuntimeEventChannel );
            if (!ganttEntriesOfRuntimeEventChannel.isEmpty())
                map.put( nextRuntimeEventChannel, ganttEntriesOfRuntimeEventChannel );
        }
        return map;
    }

    private List<GanttChartEntry> findGanttEntriesForChannel(RuntimeEventChannel<?> nextRuntimeEventChannel)
    {
        List<GanttChartEntry> ganttEntriesOfRuntimeEventChannel = new ArrayList<GanttChartEntry>();
        RuntimeEvent<?> startRuntimeEvent = null;
        RuntimeEvent<?> endRuntimeEvent = null;
        RuntimeEvent<?> currentRuntimeEvent = null;
        List<RuntimeEventChannel<?>> channelList = new ArrayList<RuntimeEventChannel<?>>();
        channelList.add( nextRuntimeEventChannel );
        List<RuntimeEvent<?>> events = runtimeEventProvider.getRuntimeEventsOfRuntimeEventChannels( channelList );
        for (RuntimeEvent<?> nextRuntimeEvent : events)
        {
            if (currentRuntimeEvent != null)
            {
                if (startRuntimeEvent == null)
                    startRuntimeEvent = findStartEvent( currentRuntimeEvent );

                if (startRuntimeEvent != null)
                    endRuntimeEvent = findEndEvent( currentRuntimeEvent );
                if (startRuntimeEvent != null && endRuntimeEvent != null)
                {
                    GanttChartEntry foundGanttEntry = new GanttChartEntryImpl( startRuntimeEvent.getTimestamp(),
                                                                               endRuntimeEvent.getTimestamp() );
                    if (entryVisibleInInterval( foundGanttEntry ))
                        ganttEntriesOfRuntimeEventChannel.add( foundGanttEntry );

                    startRuntimeEvent = null;
                    endRuntimeEvent = null;
                }
            }

            currentRuntimeEvent = nextRuntimeEvent;
            if (nextRuntimeEvent.getTimestamp() > endTimestamp)
                break;
        }

        if (startRuntimeEvent != null)
        {
            GanttChartEntry foundGanttEntry = new GanttChartEntryImpl( startRuntimeEvent.getTimestamp(),
                                                                       currentRuntimeEvent.getTimestamp() );
            ganttEntriesOfRuntimeEventChannel.add( foundGanttEntry );
        }
        ganttEntriesOfRuntimeEventChannel = aggregateGanttEntries( ganttEntriesOfRuntimeEventChannel );
        return ganttEntriesOfRuntimeEventChannel;
    }

    private List<GanttChartEntry> aggregateGanttEntries(List<GanttChartEntry> entries)
    {
        if (aggregationTime == null)
            return entries;
        List<GanttChartEntry> aggregatedEntries = new ArrayList<GanttChartEntry>();
        GanttChartEntry previousEntry = null;
        for (GanttChartEntry entry : entries)
        {
            previousEntry = aggregatedEntries.isEmpty() ? null : aggregatedEntries.get( aggregatedEntries.size() - 1 );
            if (previousEntry == null)
            {
                previousEntry = entry;
                aggregatedEntries.add( entry );
                continue;
            }
            long timeBetweenEntries = entry.getStartTimeStamp() - previousEntry.getEndTimeStamp();
            if (timeBetweenEntries < aggregationTime && previousEntry.getLength() < aggregationTime)
            {
                GanttChartEntry joinedEntry = new GanttChartEntryImpl( previousEntry.getStartTimeStamp(),
                                                                       entry.getEndTimeStamp() );
                aggregatedEntries.remove( aggregatedEntries.size() - 1 );
                aggregatedEntries.add( joinedEntry );
            }
            else
                aggregatedEntries.add( entry );
        }
        return aggregatedEntries;
    }

    private RuntimeEvent<?> findEndEvent(RuntimeEvent<?> currentRuntimeEvent)
    {
        RuntimeEvent<?> endRuntimeEvent = null;
        Boolean currentValue = getBooleanValue( currentRuntimeEvent );
        if (!currentValue)
            endRuntimeEvent = currentRuntimeEvent;
        return endRuntimeEvent;
    }

    private RuntimeEvent<?> findStartEvent(RuntimeEvent<?> currentRuntimeEvent)
    {
        Boolean currentValue = getBooleanValue( currentRuntimeEvent );
        if (currentValue)
        {
            return currentRuntimeEvent;
        }
        return null;
    }

    private boolean entryVisibleInInterval(GanttChartEntry foundGanttEntry)
    {
        long entryStart = foundGanttEntry.getStartTimeStamp();
        if (entryStart > startTimestamp && entryStart < endTimestamp)
            return true;

        long entryEnd = foundGanttEntry.getEndTimeStamp();
        if (entryEnd > startTimestamp && entryEnd < endTimestamp)
            return true;

        if (entryStart < startTimestamp && entryEnd > endTimestamp)
            return true;

        return false;
    }

    private Boolean getBooleanValue(RuntimeEvent<?> nextRuntimeEvent)
    {
        Boolean result = null;
        if (nextRuntimeEvent.getValue() instanceof Integer)
        {
            int intValue = (Integer)nextRuntimeEvent.getValue();
            if (intValue == 0)
                result = Boolean.FALSE;
            else if (intValue == 1)
                result = Boolean.TRUE;
        }
        if (nextRuntimeEvent.getValue() instanceof Boolean)
        {
            result = (Boolean)nextRuntimeEvent.getValue();
        }
        return result;
    }
}
