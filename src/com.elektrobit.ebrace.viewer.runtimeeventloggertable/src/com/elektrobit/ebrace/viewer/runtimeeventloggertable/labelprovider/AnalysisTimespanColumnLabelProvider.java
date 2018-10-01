/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class AnalysisTimespanColumnLabelProvider extends ColumnLabelProvider
{

    private final AnalysisTimespanPreferences analysisTimespanPreferences;
    private final UserInteractionPreferences userInteractionPreferences;

    public AnalysisTimespanColumnLabelProvider(AnalysisTimespanPreferences analysisTimespanPreferences,
            UserInteractionPreferences userInteractionPreferences)
    {
        this.analysisTimespanPreferences = analysisTimespanPreferences;
        this.userInteractionPreferences = userInteractionPreferences;
    }

    @Override
    public Color getBackground(Object element)
    {
        Color result = null;
        if (!userInteractionPreferences.isLiveMode())
        {
            if (element instanceof TimebasedObject)
            {
                TimebasedObject timestamp = (TimebasedObject)element;
                if (isCellInAnalysisTimespanRange( timestamp ))
                {
                    result = ColorPreferences.ANALYSIS_TIMESPAN_COLOR_RULER_WITHOUT_ALPHA;
                }
            }
        }

        return result;
    }

    private boolean isCellInAnalysisTimespanRange(TimebasedObject timestamp)
    {
        long start = analysisTimespanPreferences.getAnalysisTimespanStart();
        long end = analysisTimespanPreferences.getAnalysisTimespanEnd();
        if (start <= timestamp.getTimestamp() && end >= timestamp.getTimestamp())
        {
            return true;
        }
        return false;
    }

    @Override
    public String getText(Object element)
    {
        return null;
    }

}
