/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt;

import java.util.Set;
import java.util.SortedSet;

import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.viewer.common.timemarker.listener.TimeMarkerMouseListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public abstract class BaseAnalysisChartCanvas<T> extends BaseChartCanvas<T>
{
    protected long minTimeAxisInAnalysisMode;
    protected long maxTimeAxisInAnalysisMode;
    protected TimeMarkerMouseListener handleTimelineMouseListener;

    public BaseAnalysisChartCanvas(Set<RuntimeEventChannel<T>> channels, Composite parent, int style, ChartModel model)
    {
        super( channels, "Analysis Chart", parent, style, model );
        createContextMenu();
    }

    @Override
    public void dispose()
    {
        super.dispose();
    };

    public void setMinMax(long min, long max)
    {
        minTimeAxisInAnalysisMode = min;
        maxTimeAxisInAnalysisMode = max;
    }

    @Override
    protected SortedSet<TimeMarker> getTimeMarkersToDraw()
    {
        return timeMarkerManager
                .getAllTimeMarkersBetweenTimestamp( analysisTimespanPreferences.getAnalysisTimespanStart(),
                                                    analysisTimespanPreferences.getAnalysisTimespanEnd() );
    }

    @Override
    protected TimeMarkerLabelStyle getTimeMarkerLabelStyle()
    {
        return TimeMarkerLabelStyle.TILTED;
    }
}
