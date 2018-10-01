/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.listener;

import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;

public interface AnalysisTimespanChangedListener
{
    /**
     * Triggers when the analysis time span is changed up or down
     * 
     * @param timespanMicros
     *            long The new analysis time span
     */
    public void analysisTimespanLengthChanged(long timespanMicros);

    /**
     * Triggers when the analysis time span end changed
     * 
     * @param timespanEndMicros
     *            long The new analysis time end
     */
    public void fullTimespanEndTimeChanged(long fullTimespanEndMicros);

    /**
     * Triggers when the time span to analyze changed
     * 
     * @param reason
     * 
     */
    public void onAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason);
}
