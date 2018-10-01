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

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseFullChartCanvas;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.ChartBuilder;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class FullLineChartCanvas<T> extends BaseFullChartCanvas<T>
{

    public FullLineChartCanvas(Set<RuntimeEventChannel<T>> channels, long startTimestampOfFullChartInMillis,
            long endTimestampOfFullChartInMillis, boolean aggregate, Composite parent, int style, ChartModel model)
    {
        super( channels,
                startTimestampOfFullChartInMillis,
                endTimestampOfFullChartInMillis,
                aggregate,
                parent,
                style,
                model );

        addControlResizedListener();
    }

    private void addControlResizedListener()
    {
        addControlListener( new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                width = getSize().x;
                chartDataUseCase.notifyChartWidthChanged();
            };
        } );
    }

    @Override
    protected ChartBuilder<T> createChartBuilder(String chartTitle)
    {
        return new ChartBuilder<T>( modelToDisplay, RunMode.FULL ).addChartTitle( chartTitle );
    }

    @Override
    protected RunMode getRunMode()
    {
        return RunMode.FULL;
    }
}
