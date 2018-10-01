/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.timesegmentmanager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimeSegmentImpl;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

@Component
public class TimeSegmentAcceptorServiceImpl implements TimeSegmentAcceptorService, ClearChunkDataListener
{
    private final Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> timeSegmentsByChannel = new ConcurrentHashMap<>();
    private RuntimeEventChannelManager runtimeEventChannelManager;
    private ChannelListenerNotifier channelListenerNotifier;
    private ChannelColorProviderService colorService;
    private final List<List<STimeSegment>> listsWithBrokenSort = new ArrayList<>();

    @Reference
    public void bindRuntimeEventChannelManager(RuntimeEventChannelManager runtimeEventChannelManager)
    {
        this.runtimeEventChannelManager = runtimeEventChannelManager;
    }

    public void unbindRuntimeEventChannelManager(RuntimeEventChannelManager runtimeEventChannelManager)
    {
        this.runtimeEventChannelManager = null;
    }

    @Reference
    public void bindChannelListenerNotifier(ChannelListenerNotifier channelListenerNotifier)
    {
        this.channelListenerNotifier = channelListenerNotifier;
    }

    public void unbindChannelListenerNotifier(ChannelListenerNotifier channelListenerNotifier)
    {
        this.channelListenerNotifier = null;
    }

    @Reference
    public void bindChannelColorProviderService(ChannelColorProviderService colorService)
    {
        this.colorService = colorService;
    }

    public void unbindChannelColorProviderService(ChannelColorProviderService channelListenerNotifier)
    {
        this.colorService = null;
    }

    @Override
    public RuntimeEventChannel<STimeSegment> createTimeSegmentChannel(String name, String description)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "TimeSegmentChannelName", name );
        RangeCheckUtils.assertReferenceParameterNotNull( "TimeSegmentChannelDescription", description );

        RuntimeEventChannel<STimeSegment> channel = runtimeEventChannelManager
                .createRuntimeEventChannel( name, Unit.TIMESEGMENT, description );

        return channel;
    }

    @Override
    public synchronized STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel,
            RuntimeEvent<?> startEvent, RuntimeEvent<?> endEvent, String label)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "timeSegmentChannel", timeSegmentChannel );
        RangeCheckUtils.assertReferenceParameterNotNull( "startEvent", startEvent );
        RangeCheckUtils.assertReferenceParameterNotNull( "endEvent", endEvent );
        RangeCheckUtils
                .assertCorrectIntervalBoundaries( "Time segment", startEvent.getTimestamp(), endEvent.getTimestamp() );

        SColor color = colorService.createAndGetColorForChannel( timeSegmentChannel );
        STimeSegment timeSegment = new TimeSegmentImpl( startEvent, endEvent, label, color, timeSegmentChannel );
        List<STimeSegment> list = createOrGetListForChannel( timeSegmentChannel );
        noteIfSortBroken( timeSegment, list );
        list.add( timeSegment );

        channelListenerNotifier.notifyChannelChanged( timeSegmentChannel );
        return timeSegment;
    }

    private void noteIfSortBroken(STimeSegment timeSegment, List<STimeSegment> list)
    {
        if (list.isEmpty())
        {
            return;
        }

        STimeSegment lastSegmentInList = list.get( list.size() - 1 );

        if (timeSegment.getStartTime() < lastSegmentInList.getStartTime())
        {
            listsWithBrokenSort.add( list );
        }
    }

    private List<STimeSegment> createOrGetListForChannel(RuntimeEventChannel<STimeSegment> timeSegmentChannel)
    {
        List<STimeSegment> list = timeSegmentsByChannel.get( timeSegmentChannel );
        if (list == null)
        {
            list = new ArrayList<>();
            timeSegmentsByChannel.put( timeSegmentChannel, list );
        }
        return list;
    }

    @Override
    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent)
    {
        return add( timeSegmentChannel, startEvent, endEvent, "" );
    }

    @Override
    public synchronized List<STimeSegment> getTimeSegments(RuntimeEventChannel<STimeSegment> timeSegmentChannel)
    {
        sortIfNeeded();
        return new ArrayList<>( createOrGetListForChannel( timeSegmentChannel ) );
    }

    @Override
    public RuntimeEventChannel<STimeSegment> createOrGetTimeSegmentChannel(String name, String description)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "TimeSegmentChannelName", name );
        RangeCheckUtils.assertReferenceParameterNotNull( "TimeSegmentChannelDescription", description );

        RuntimeEventChannel<STimeSegment> channel = runtimeEventChannelManager
                .createOrGetRuntimeEventChannel( name, Unit.TIMESEGMENT, description );

        return channel;
    }

    // TODO check coverage

    @Override
    public synchronized void clear()
    {
        listsWithBrokenSort.clear();
        timeSegmentsByChannel.clear();
        channelListenerNotifier.notifyAllChannelsChanged();
    }

    @Override
    public synchronized Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> getTimeSegmentsForChannel(
            List<RuntimeEventChannel<STimeSegment>> channels)
    {
        sortIfNeeded();

        Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> result = new LinkedHashMap<>();
        for (RuntimeEventChannel<STimeSegment> channel : channels)
        {
            List<STimeSegment> segmentsForChannel = createOrGetListForChannel( channel );
            result.put( channel, segmentsForChannel );
        }
        return result;
    }

    private void sortIfNeeded()
    {
        if (listsWithBrokenSort.isEmpty())
        {
            return;
        }
        else
        {
            STimeSegmentComparator comparator = new STimeSegmentComparator();
            for (List<STimeSegment> listToSort : listsWithBrokenSort)
            {
                Collections.sort( listToSort, comparator );
            }
        }
        listsWithBrokenSort.clear();
    }

    public void removeTimeSegmentsWithChannel(RuntimeEventChannel<STimeSegment> channel)
    {
        if (timeSegmentsByChannel.containsKey( channel ))
        {
            timeSegmentsByChannel.remove( channel );
        }
    }

    @Override
    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channels)
    {
        channelListenerNotifier.registerListener( listener, channels );
    }

    @Override
    public void unregisterListener(ChannelsContentChangedListener listener)
    {
        channelListenerNotifier.unregisterListener( listener );
    }

    @Override
    public void onClearChunkData()
    {
        clear();
    }

    @Override
    public void setColor(STimeSegment segment, SColor color)
    {
        TimeSegmentImpl segmentImpl = (TimeSegmentImpl)segment;
        segmentImpl.setColor( color );
        channelListenerNotifier.notifyChannelChanged( segment.getChannel() );
    }

    @Override
    public void removeSegmentsForChannel(RuntimeEventChannel<STimeSegment> channel)
    {
        removeTimeSegmentsWithChannel( channel );
    }
}
