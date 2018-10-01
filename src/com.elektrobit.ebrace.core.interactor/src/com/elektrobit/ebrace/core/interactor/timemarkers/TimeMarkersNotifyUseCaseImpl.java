/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.timemarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class TimeMarkersNotifyUseCaseImpl implements TimeMarkersNotifyUseCase, TimeMarkersChangedListener
{
    private final TimeMarkerManager timeMarkerManager;
    private TimeMarkersNotifyCallback callback;

    public TimeMarkersNotifyUseCaseImpl(TimeMarkersNotifyCallback callback, TimeMarkerManager timeMarkerManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "timeMarkerManager", timeMarkerManager );
        this.callback = callback;
        this.timeMarkerManager = timeMarkerManager;
        registerListeners();
        collectAndPostData();
        postCurrentSelectedTimeMarker( timeMarkerManager.getCurrentSelectedTimeMarker() );
    }

    private void collectAndPostData()
    {
        SortedSet<TimeMarker> allTimeMarkers = timeMarkerManager.getAllTimeMarkers();
        final List<TimeMarker> timeMarkers = new ArrayList<TimeMarker>( allTimeMarkers );

        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onTimeMarkersChanged( timeMarkers );
                }
            }
        } );
    }

    private void registerListeners()
    {
        timeMarkerManager.registerListener( this );
    }

    @Override
    public void unregister()
    {
        timeMarkerManager.unregisterListener( this );
        callback = null;
    }

    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
        collectAndPostData();
    }

    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
        collectAndPostData();
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
        collectAndPostData();
    }

    @Override
    public void timeMarkerSelected(TimeMarker timeMarker)
    {
        postCurrentSelectedTimeMarker( timeMarker );
    }

    private void postCurrentSelectedTimeMarker(TimeMarker timeMarker)
    {
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onTimeMarkerSelected( timeMarker );
            }
        } );

    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
        collectAndPostData();
    }

    @Override
    public void timeMarkerNameChanged(TimeMarker timeMarker)
    {
        collectAndPostData();
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
        collectAndPostData();
    }

    @Override
    public void allTimeMarkersRemoved()
    {
        collectAndPostData();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
        collectAndPostData();
    }
}
