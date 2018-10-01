/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.util;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;

public class TimespanUtil
{
    public static void changeTimespanIfNeeded(long timestamp, ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
        AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
                .getService();

        long timespan = analysisTimespanPreferences.getAnalysisTimespanLength();
        long analysisTimestampFullStart = analysisTimespanPreferences.getFullTimespanStart();
        long analysisTimestampFullEnd = analysisTimespanPreferences.getFullTimespanEnd();

        long newMin = (timestamp - timespan / 2);
        long newMax = (timestamp + timespan / 2);

        if (newMin < analysisTimestampFullStart)
        {
            newMin = analysisTimestampFullStart;
            newMax = newMin + timespan;
        }
        if (newMax > analysisTimestampFullEnd)
        {
            newMax = analysisTimestampFullEnd;
            newMin = newMax - timespan;
        }
        analysisTimespanPreferences.setAnalysisTimespanEnd( newMax, reason );
    }
}
