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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;

public class AnalysisTimespanNotifyUseCaseImpl implements AnalysisTimespanNotifyUseCase, AnalysisTimespanChangedListener
{

    private final AnalysisTimespanPreferences analysisTimespanPreferences;
    private AnalysisTimespanNotifyCallback callback;

    public AnalysisTimespanNotifyUseCaseImpl(AnalysisTimespanPreferences analysisTimespanPreferences,
            AnalysisTimespanNotifyCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "analysisTimespanPreferences", analysisTimespanPreferences );
        RangeCheckUtils.assertReferenceParameterNotNull( "AnalysisTimespanCallback", callback );
        this.analysisTimespanPreferences = analysisTimespanPreferences;
        this.callback = callback;

        analysisTimespanPreferences.addTimespanPreferencesChangedListener( this );
        postCurrentValues();
    }

    private void postCurrentValues()
    {
        long analysisTimespanEnd = analysisTimespanPreferences.getAnalysisTimespanEnd();
        long analysisTimespanStart = analysisTimespanPreferences.getAnalysisTimespanStart();
        long analysisTimespanLength = analysisTimespanPreferences.getAnalysisTimespanLength();
        long fullTimespanStart = analysisTimespanPreferences.getFullTimespanStart();
        long fullTimespanEnd = analysisTimespanPreferences.getFullTimespanEnd();

        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onAnalysisTimespanChanged( analysisTimespanStart, analysisTimespanEnd );
                callback.onAnalysisTimespanLengthChanged( analysisTimespanLength );
                callback.onFullTimespanChanged( fullTimespanStart, fullTimespanEnd );
            }
        } );
    }

    @Override
    public void unregister()
    {
        analysisTimespanPreferences.removeTimespanPreferencesChangedListener( this );
        callback = null;
    }

    @Override
    public void analysisTimespanLengthChanged(long timespanMicros)
    {
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onAnalysisTimespanLengthChanged( timespanMicros );
            }
        } );

        postAnalysisTime();
    }

    @Override
    public void fullTimespanEndTimeChanged(long timespanEndMicros)
    {
        long timespanStart = analysisTimespanPreferences.getFullTimespanStart();
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onFullTimespanChanged( timespanStart, timespanEndMicros );
            }
        } );
    }

    @Override
    public void onAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
        postAnalysisTime();
    }

    private void postAnalysisTime()
    {
        long analysisTimespanStart = analysisTimespanPreferences.getAnalysisTimespanStart();
        long analysisTimespanEnd = analysisTimespanPreferences.getAnalysisTimespanEnd();

        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onAnalysisTimespanChanged( analysisTimespanStart, analysisTimespanEnd );
            }
        } );
    }
}
