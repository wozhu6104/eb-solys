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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public abstract class BaseLiveChartComposite<T>
{
    protected final Set<RuntimeEventChannel<T>> chartDataSeriesSuiteForLiveChart = new LinkedHashSet<RuntimeEventChannel<T>>();
    protected BaseChartCanvas<?> liveChartCanvas;
    protected final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();

    protected BaseChartComposite parent;

    protected final Class<?> assignableDataType;
    protected final ChartModel modelToDisplay;

    public BaseLiveChartComposite(final BaseChartComposite parent, int style, Class<?> assignableDataType)
    {
        this.parent = parent;
        this.modelToDisplay = parent.getModelToDisplay();
        this.assignableDataType = assignableDataType;
        createNewLiveChartCanvas( parent );
    }

    protected abstract void createNewLiveChartCanvas(Composite parent);

    @SuppressWarnings("unchecked")
    public void addRuntimeEventChannelToChart(RuntimeEventChannel<?> runtimeEventChannelToAdd)
    {
        if (assignableDataType.isAssignableFrom( runtimeEventChannelToAdd.getUnit().getDataType() ))
        {
            chartDataSeriesSuiteForLiveChart.add( (RuntimeEventChannel<T>)runtimeEventChannelToAdd );
            repaint();
        }
    }

    public void removeRuntimeEventChannelFromChart(RuntimeEventChannel<?> runtimeEventChannelToRemove)
    {
        chartDataSeriesSuiteForLiveChart.remove( runtimeEventChannelToRemove );
        repaint();
    }

    public void clearChannels()
    {
        chartDataSeriesSuiteForLiveChart.clear();
    }

    public BaseChartCanvas<?> getChartComposite()
    {
        return liveChartCanvas;
    }

    public void dispose()
    {

        if (liveChartCanvas != null)
        {
            liveChartCanvas.dispose();
        }
    }

    public void repaint()
    {
        dispose();
        createNewLiveChartCanvas( parent );
    }

    public BaseChartCanvas<?> getLiveChartCanvas()
    {
        return liveChartCanvas;
    }

}
