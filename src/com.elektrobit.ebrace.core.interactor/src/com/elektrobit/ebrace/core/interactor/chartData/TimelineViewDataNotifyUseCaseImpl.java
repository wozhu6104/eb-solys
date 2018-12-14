/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.chartData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.resources.api.manager.ResourceChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class TimelineViewDataNotifyUseCaseImpl
        implements
            TimelineViewDataNotifyUseCase,
            ChannelsContentChangedListener,
            ResourceChangedListener
{

    private final TimeSegmentAcceptorService timeSegmentAcceptor;
    private TimelineViewDataNotifyCallback callback;
    private TimelineViewModel timelineViewModel;
    private final ResourcesModelManager resourcesModelManager;

    public TimelineViewDataNotifyUseCaseImpl(TimeSegmentAcceptorService timeSegmentAcceptor,
            ResourcesModelManager resourcesModelManager, TimelineViewDataNotifyCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "timeSegmentAcceptor", timeSegmentAcceptor );
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );

        this.timeSegmentAcceptor = timeSegmentAcceptor;
        this.resourcesModelManager = resourcesModelManager;
        this.callback = callback;
    }

    @Override
    public void register(TimelineViewModel timelineViewModel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "timelineViewModel", timelineViewModel );
        this.timelineViewModel = timelineViewModel;
        List<RuntimeEventChannel<?>> channels = timelineViewModel.getChannels();
        timeSegmentAcceptor.registerListener( this, channels );
        resourcesModelManager.registerResourceListener( this );

        triggerDataCollection();
    }

    private void triggerDataCollection()
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "TimelineViewDataNotifyUseCase.triggerDataCollection",
                                                       () -> collectAndPostData() ) );
    }

    @SuppressWarnings("unchecked")
    private void collectAndPostData()
    {
        List<RuntimeEventChannel<?>> channelsUntyped = timelineViewModel.getChannels();
        List<RuntimeEventChannel<STimeSegment>> channelsTyped = new ArrayList<RuntimeEventChannel<STimeSegment>>();
        channelsTyped.addAll( (List<? extends RuntimeEventChannel<STimeSegment>>)channelsUntyped );
        Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> result = timeSegmentAcceptor
                .getTimeSegmentsForChannel( channelsTyped );

        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onNewTimelineData( result );
            }
        } );
    }

    @Override
    public void unregister()
    {
        timeSegmentAcceptor.unregisterListener( this );
        resourcesModelManager.unregisterResourceListener( this );
        callback = null;
    }

    @Override
    public void onChannelsContentChanged()
    {
        triggerDataCollection();
    }

    @Override
    public void onResourceModelChannelsChanged(ResourceModel resourceModel)
    {
        if (resourceModel.equals( timelineViewModel ))
        {
            timeSegmentAcceptor.registerListener( this, timelineViewModel.getChannels() );
            triggerDataCollection();
        }
    }

    @Override
    public void onResourceModelSelectedChannelsChanged(ResourceModel resourceModel)
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        if (channel.getUnit().equals( Unit.TIMESEGMENT ))
        {
            timeSegmentAcceptor.removeSegmentsForChannel( (RuntimeEventChannel<STimeSegment>)channel );
            removeChannel( channel );
        }
    }

    private void removeChannel(RuntimeEventChannel<?> channel)
    {
        Iterator<RuntimeEventChannel<?>> iterator = timelineViewModel.getChannels().iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().equals( channel ))
            {
                iterator.remove();
            }
        }
        timelineViewModel.setChannels( timelineViewModel.getChannels() );
    }
}
