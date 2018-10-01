/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.api;

import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;

public interface AnalysisTimespanPreferences
{
    public enum ANALYSIS_TIMESPAN_CHANGE_REASON {
        TIME_MARKER_SELECTED, USER_SHIFT
    }

    public long getFullTimespanStart();

    public long getFullTimespanEnd();

    public long getAnalysisTimespanStart();

    public long getAnalysisTimespanEnd();

    public long getAnalysisTimespanLength();

    public void addTimespanPreferencesChangedListener(
            AnalysisTimespanChangedListener analysisTimespanChangedListenerToAdd);

    public void removeTimespanPreferencesChangedListener(
            AnalysisTimespanChangedListener analysisTimespanChangedListenerToRemove);

    public void setAnalysisTimespanEnd(long newValue, ANALYSIS_TIMESPAN_CHANGE_REASON reason);

    public void setAnalysisTimespanLength(long analysisTimespanInMillis);

}
