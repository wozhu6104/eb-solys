/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.line;

import java.util.Set;
import java.util.SortedSet;

import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseChartCanvas;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.ChartBuilder;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class LiveLineChartCanvas<T> extends BaseChartCanvas<T>
{
    public LiveLineChartCanvas(Set<RuntimeEventChannel<T>> channels, Composite parent, int style, ChartModel model)
    {
        super( channels, "Live Chart", parent, style, model );
    }

    @Override
    protected ChartBuilder<T> createChartBuilder(String chartTitle)
    {
        return new ChartBuilder<T>( modelToDisplay, RunMode.LIVE ).addChartTitle( chartTitle );
    }

    @Override
    protected RunMode getRunMode()
    {
        return RunMode.LIVE;
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
