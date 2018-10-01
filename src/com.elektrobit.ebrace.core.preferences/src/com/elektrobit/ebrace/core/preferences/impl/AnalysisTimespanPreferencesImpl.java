/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.impl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;

@Component
public class AnalysisTimespanPreferencesImpl implements AnalysisTimespanPreferences, UserInteractionPreferencesListener
{
    private RuntimeEventProvider runtimeEventProvider;
    private final List<AnalysisTimespanChangedListener> timespanChangedListeners = new CopyOnWriteArrayList<AnalysisTimespanChangedListener>();
    private long analysisTimespanLength;
    private long fullTimespanEnd;
    private long analysisTimespanEnd;
    private final Timer timespanUpdateTimer = new Timer();
    private TimerTask timerTask = null;
    private long fullTimespanStart;
    private boolean isLiveMode;
    private PreferencesService preferencesService;

    @Reference
    public void setRuntimeEventProvider(final RuntimeEventProvider runtimeEventProvider)
    {
        this.runtimeEventProvider = runtimeEventProvider;
    }

    public void unsetRuntimeEventProvider(final RuntimeEventProvider runtimeEventProvider)
    {
        this.runtimeEventProvider = null;
    }

    @Reference
    public void setPreferencesService(final PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
    }

    public void unsetPreferencesService(final PreferencesService preferencesService)
    {
        this.preferencesService = null;
    }

    @Activate
    protected void activate(ComponentContext componentContext)
    {
        analysisTimespanLength = preferencesService.getAnalysisTimespanLength();
        resetIntervals();
        startStopTimespanUpdateTime( true );
    }

    @Deactivate
    protected void deactivate()
    {
        cancelTimespanUpdateTimer();
        saveAnalysisTimespanLengthValueToPreferences();
    }

    @Override
    public long getAnalysisTimespanLength()
    {
        return analysisTimespanLength;
    }

    @Override
    public void setAnalysisTimespanLength(long analysisTimespanLength)
    {
        this.analysisTimespanLength = analysisTimespanLength;
        recomputeAnalysisBorders();
        notifyAnalysisTimespanLengthChanged( analysisTimespanLength );
    }

    private void recomputeAnalysisBorders()
    {
        setAnalysisTimespanEnd( analysisTimespanEnd, ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
    }

    private void notifyAnalysisTimespanLengthChanged(long newAnalysisTimespanLength)
    {
        for (AnalysisTimespanChangedListener nextAnalysisTimespanChangedListener : timespanChangedListeners)
        {
            nextAnalysisTimespanChangedListener.analysisTimespanLengthChanged( newAnalysisTimespanLength );
        }
    }

    private void notifyFullTimespanEndChanged(long newTimespanEndTime)
    {
        for (AnalysisTimespanChangedListener nextAnalysisTimespanChangedListener : timespanChangedListeners)
        {
            nextAnalysisTimespanChangedListener.fullTimespanEndTimeChanged( newTimespanEndTime );
        }
    }

    private void notifyAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
        for (AnalysisTimespanChangedListener nextAnalysisTimespanChangedListener : timespanChangedListeners)
        {
            nextAnalysisTimespanChangedListener.onAnalysisTimespanChanged( reason );
        }
    }

    @Override
    public long getFullTimespanStart()
    {
        updateFullTimespanInterval();
        return fullTimespanStart;
    }

    private void updateFullTimespanInterval()
    {
        RuntimeEvent<?> latestRuntimeEvent = runtimeEventProvider.getLatestRuntimeEvent();
        RuntimeEvent<?> firstRuntimeEvent = runtimeEventProvider.getFirstRuntimeEvent();
        if (firstRuntimeEvent == null || latestRuntimeEvent == null)
        {
            resetIntervals();
        }
        else
        {
            fullTimespanStart = firstRuntimeEvent.getTimestamp();
            if (isLiveMode || fullTimespanEnd < fullTimespanStart)
            {
                fullTimespanEnd = latestRuntimeEvent.getTimestamp();
            }
        }
    }

    private void resetIntervals()
    {
        fullTimespanEnd = analysisTimespanLength;
        analysisTimespanEnd = analysisTimespanLength;
        fullTimespanStart = 0;
    }

    @Override
    public long getFullTimespanEnd()
    {
        updateFullTimespanInterval();
        return fullTimespanEnd;
    }

    @Override
    public long getAnalysisTimespanEnd()
    {
        return analysisTimespanEnd;
    }

    @Override
    public void setAnalysisTimespanEnd(long newEnd, ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
        long newStart = newEnd - analysisTimespanLength;

        if (newStart < fullTimespanStart)
        {
            newStart = fullTimespanStart;
        }

        newEnd = newStart + analysisTimespanLength;

        if (newEnd > fullTimespanEnd)
        {
            newEnd = fullTimespanEnd;
        }

        analysisTimespanEnd = newEnd;
        notifyAnalysisTimespanChanged( reason );
    }

    @Override
    public void addTimespanPreferencesChangedListener(AnalysisTimespanChangedListener listener)
    {
        timespanChangedListeners.add( listener );
    }

    @Override
    public void removeTimespanPreferencesChangedListener(
            AnalysisTimespanChangedListener analysisTimespanChangedListenerToRemove)
    {
        timespanChangedListeners.remove( analysisTimespanChangedListenerToRemove );
    }

    @Override
    public long getAnalysisTimespanStart()
    {
        long fullStart = getFullTimespanStart();
        long analysisStart = getAnalysisTimespanEnd() - getAnalysisTimespanLength();
        if (analysisStart < fullStart)
        {
            return fullStart;
        }
        else
        {
            return analysisStart;
        }
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        this.isLiveMode = isLiveMode;
        startStopTimespanUpdateTime( isLiveMode );
        updateAnalysisTimespan();
    }

    private void startStopTimespanUpdateTime(boolean start)
    {
        if (start)
        {
            startTimespanUpdateTimer();
        }
        else
        {
            cancelTimespanUpdateTimer();
        }
    }

    private void startTimespanUpdateTimer()
    {
        if (timerTask != null)
        {
            return;
        }
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                updateAnalysisTimespan();
            }
        };
        timespanUpdateTimer.schedule( timerTask, 1, 3000 );
    }

    private void cancelTimespanUpdateTimer()
    {
        if (timerTask != null)
        {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private void updateAnalysisTimespan()
    {
        RuntimeEvent<?> latestRuntimeEvent = runtimeEventProvider.getLatestRuntimeEvent();
        if (latestRuntimeEvent != null)
        {
            long ts = latestRuntimeEvent.getTimestamp();
            setFullTimespanEnd( ts );
            setAnalysisTimespanEnd( ts, ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
        }
        else
        {
            resetIntervals();
            notifyAnalysisTimespanChanged( ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
        }
    }

    private void setFullTimespanEnd(long fullTimespanEnd)
    {
        this.fullTimespanEnd = fullTimespanEnd;
        notifyFullTimespanEndChanged( fullTimespanEnd );
    }

    private void saveAnalysisTimespanLengthValueToPreferences()
    {
        preferencesService.setAnalysisTimespanLength( analysisTimespanLength );
    }
}
