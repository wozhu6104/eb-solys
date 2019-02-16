/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.liveviewer;

import java.util.Set;
import java.util.stream.Collectors;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.liveviewer.LiveViewerHandlerInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.liveviewer.LiveViewerHandlerInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRepeatedTask;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class LiveViewerHandlerInteractionUseCaseImpl
        implements
            LiveViewerHandlerInteractionUseCase,
            AnalysisTimespanChangedListener,
            UserInteractionPreferencesListener
{

    private LiveViewerHandlerInteractionCallback callback;
    private final AnalysisTimespanPreferences analysisTimespanPreferences;
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final UserInteractionPreferences userInteractionPreferences;
    private UseCaseRepeatedTask runningTask;
    private final TimeMarkerManager timeMarkerManager;

    public LiveViewerHandlerInteractionUseCaseImpl(LiveViewerHandlerInteractionCallback callback,
            AnalysisTimespanPreferences analysisTimespanPreferences, RuntimeEventAcceptor runtimeEventAcceptor,
            UserInteractionPreferences userInteractionPreferences, TimeMarkerManager timeMarkerManager)
    {
        this.callback = callback;
        this.analysisTimespanPreferences = analysisTimespanPreferences;
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.userInteractionPreferences = userInteractionPreferences;
        this.timeMarkerManager = timeMarkerManager;

        analysisTimespanPreferences.addTimespanPreferencesChangedListener( this );
        userInteractionPreferences.addUserInteractionPreferencesListener( this );

    }

    @Override
    public void activateLiveViewer()
    {
        rescheduleEventsRemovingTask();
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onLiveViewerActivated();
            }
        } );
    }

    private void rescheduleEventsRemovingTask()
    {
        stopRemoveOutdatedEvents();

        long analysisTimespanLength = analysisTimespanPreferences.getAnalysisTimespanLength();
        runningTask = UseCaseExecutor.scheduleRepeated( new UseCaseRepeatedTask()
        {

            @Override
            public void execute()
            {
                removeOutdatedEvents();
                removeOutdatedTimeMarkers();
            }

        }, analysisTimespanLength / 1000 / 2 );
    }

    @Override
    public void unregister()
    {
        stopRemoveOutdatedEvents();
        analysisTimespanPreferences.removeTimespanPreferencesChangedListener( this );
        userInteractionPreferences.removeUserInteractionPreferencesListener( this );
        callback = null;
    }

    private void stopRemoveOutdatedEvents()
    {
        if (runningTask != null)
        {
            runningTask.cancel();
            runningTask = null;
        }
    }

    @Override
    public void analysisTimespanLengthChanged(long timespanMicros)
    {
        rescheduleEventsRemovingTask();
    }

    @Override
    public void fullTimespanEndTimeChanged(long fullTimespanEndMicros)
    {

    }

    @Override
    public void onAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {

    }

    private void removeOutdatedEvents()
    {
        runtimeEventAcceptor.removeEventsFromTo( 0, analysisTimespanPreferences.getAnalysisTimespanStart() );
    }

    private void removeOutdatedTimeMarkers()
    {
        Set<TimeMarker> outdatedTimeMarkers = timeMarkerManager.getAllTimeMarkers().stream()
                .filter( marker -> marker.getTimestamp() < analysisTimespanPreferences.getAnalysisTimespanStart() )
                .collect( Collectors.toSet() );
        if (!outdatedTimeMarkers.isEmpty())
        {
            timeMarkerManager.removeTimeMarkers( outdatedTimeMarkers );
        }

    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {

        if (!isLiveMode)
        {
            stopRemoveOutdatedEvents();
            UseCaseExecutor.schedule( new UseCaseRunnable( "LiveViewerRemoveEvents", () -> {
                removeOutdatedEvents();
                removeOutdatedTimeMarkers();
            } ) );
        }
        else
        {
            rescheduleEventsRemovingTask();
        }
    }

}
