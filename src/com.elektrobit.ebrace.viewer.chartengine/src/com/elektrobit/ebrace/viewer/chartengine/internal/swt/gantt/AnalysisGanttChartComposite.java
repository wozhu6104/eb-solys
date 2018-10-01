/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.gantt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseAnalysisChartComposite;

public class AnalysisGanttChartComposite<T> extends BaseAnalysisChartComposite<T>
{
    public AnalysisGanttChartComposite(Composite parent, int style, ChartModel model)
    {
        super( parent, style, model );
    }

    @Override
    protected void createFullChart(long timestampOfFirstRuntimeEventInMillis, long timestampOfLastRuntimeEventInMillis)
    {
        fullChartCanvas = new FullGanttChartCanvas<T>( fullChartChannels,
                                                       timestampOfFirstRuntimeEventInMillis,
                                                       timestampOfLastRuntimeEventInMillis,
                                                       true,
                                                       fullChartContainer,
                                                       SWT.DOUBLE_BUFFERED,
                                                       modelToDisplay );
    }

    @Override
    protected void createAnalysisChart(long startTimestampAnalysisChart, long endTimestampAnalysisChart)
    {
        analysisChartCanvas = new AnalysisGanttChartCanvas<T>( analysisChartChannels,
                                                               analysisChartContainer,
                                                               SWT.DOUBLE_BUFFERED,
                                                               modelToDisplay );
        analysisChartCanvas.setMinMax( startTimestampAnalysisChart, endTimestampAnalysisChart );

    }
}
