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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.viewer.chartengine.internal.TimebasedRuntimeEventFinder;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public abstract class BaseAnalysisChartComposite<T>
{
    protected final Set<RuntimeEventChannel<T>> analysisChartChannels = new LinkedHashSet<RuntimeEventChannel<T>>();
    protected final Set<RuntimeEventChannel<T>> fullChartChannels = new LinkedHashSet<RuntimeEventChannel<T>>();
    protected final List<RuntimeEventChannel<?>> runtimeEventChannels = new ArrayList<RuntimeEventChannel<?>>();

    protected BaseAnalysisChartCanvas<?> analysisChartCanvas;
    protected BaseFullChartCanvas<?> fullChartCanvas;

    protected final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();
    protected Composite mainAnalysisChartComposite;
    protected SashForm sash;
    protected Composite fullChartContainer;
    protected Composite analysisChartContainer;
    protected ChartModel modelToDisplay;

    public BaseAnalysisChartComposite(Composite parent, int style, ChartModel model)
    {
        this.modelToDisplay = model;
        addMainAnalysisChartComposite( parent, style );
    }

    protected void setNewMinMaxAndRepaintChart(long newMin, long newMax)
    {
        if (isAnalysisCanvasValid())
        {
            disposeAnalysisChart();
            createAnalysisChart( newMin, newMax );
            analysisChartContainer.layout();
            analysisChartCanvas.setMinMax( newMin, newMax );
            analysisChartCanvas.layout();
        }
    }

    private void addMainAnalysisChartComposite(Composite parent, int style)
    {
        mainAnalysisChartComposite = new Composite( parent, style );
        mainAnalysisChartComposite.setLayout( new GridLayout() );
        mainAnalysisChartComposite.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
        sash = new SashForm( mainAnalysisChartComposite, SWT.VERTICAL );
        sash.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
        createFullChartContainer();
        analysisChartContainer = new Composite( sash, SWT.NONE );
        analysisChartContainer.setLayout( new FillLayout() );
        sash.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        sash.setWeights( new int[]{1, 2} );
    }

    public void clearChannels()
    {
        runtimeEventChannels.clear();
        analysisChartChannels.clear();
        fullChartChannels.clear();
    }

    private void createFullChartContainer()
    {
        fullChartContainer = new Composite( sash, SWT.NONE );
        fullChartContainer.setLayout( new FillLayout() );
    }

    public BaseAnalysisChartCanvas<?> getAnalysisChartCanvas()
    {
        return analysisChartCanvas;
    }

    public Composite getChartComposite()
    {
        return mainAnalysisChartComposite;
    }

    public void recreateCharts()
    {
        disposeCharts();
        createCharts();
        repaintChartCanvas();
    }

    public void repaintAnalysisChartCanvas()
    {
        if (isAnalysisCanvasValid())
        {
            analysisChartCanvas.redraw();
        }
    }

    public void recreateAnlysisChart()
    {
        disposeAnalysisChart();
        long endAnalysis = analysisTimespanPreferences.getAnalysisTimespanEnd();
        long startAnalysis = analysisTimespanPreferences.getAnalysisTimespanStart();
        createAnalysisChart( startAnalysis, endAnalysis );
        repaintChartCanvas();
    }

    public void disposeCharts()
    {
        disposeFullChart();
        disposeAnalysisChart();
    }

    protected void disposeFullChart()
    {
        if (fullChartCanvas != null)
        {
            fullChartCanvas.dispose();
        }
    }

    protected void disposeAnalysisChart()
    {
        if (analysisChartCanvas != null)
        {
            analysisChartCanvas.dispose();
        }
    }

    protected void createCharts()
    {
        long analysisEnd = analysisTimespanPreferences.getAnalysisTimespanEnd();
        long analysisStart = analysisTimespanPreferences.getAnalysisTimespanStart();
        long fullTimespanEnd = analysisTimespanPreferences.getFullTimespanEnd();
        long fullTimespanStart = analysisTimespanPreferences.getFullTimespanStart();
        createFullChart( fullTimespanStart, fullTimespanEnd );
        createAnalysisChart( analysisStart, analysisEnd );
    }

    protected abstract void createFullChart(long timestampOfFirstRuntimeEventInMillis,
            long timestampOfLastRuntimeEventInMillis);

    protected abstract void createAnalysisChart(long startTimestampAnalysisChart, long endTimestampAnalysisChart);

    protected void repaintChartCanvas()
    {
        if (isFullChartCanvasValid())
        {
            fullChartCanvas.layout();
        }
        analysisChartCanvas.layout();
        mainAnalysisChartComposite.layout();
        analysisChartContainer.layout();
        fullChartContainer.layout();
    }

    protected RuntimeEvent<?> getNextRuntimeEventForTimeInMillis(long timeInMillis)
    {
        TimebasedRuntimeEventFinder timebaseRuntimeEventFinder = new TimebasedRuntimeEventFinder( timeInMillis,
                                                                                                  runtimeEventChannels );
        RuntimeEvent<?> foundRuntimeEvent = timebaseRuntimeEventFinder.getNextRuntimeEvent();
        return foundRuntimeEvent;
    }

    protected RuntimeEvent<?> getLastRuntimeEventForTimeInMillis(long timeInMillis)
    {
        TimebasedRuntimeEventFinder timebaseRuntimeEventFinder = new TimebasedRuntimeEventFinder( timeInMillis,
                                                                                                  runtimeEventChannels );
        RuntimeEvent<?> foundRuntimeEvent = timebaseRuntimeEventFinder.getLastRuntimeEvent();
        return foundRuntimeEvent;
    }

    @SuppressWarnings("unchecked")
    public void addRuntimeEventChannelToChart(RuntimeEventChannel<?> runtimeEventChannelToAdd)
    {
        analysisChartChannels.add( (RuntimeEventChannel<T>)runtimeEventChannelToAdd );
        fullChartChannels.add( (RuntimeEventChannel<T>)runtimeEventChannelToAdd );
        runtimeEventChannels.add( runtimeEventChannelToAdd );
    }

    public void removeRuntimeEventChannelFromChart(RuntimeEventChannel<?> runtimeEventChannelToRemove)
    {
        analysisChartChannels.remove( runtimeEventChannelToRemove );
        fullChartChannels.remove( runtimeEventChannelToRemove );
        runtimeEventChannels.remove( runtimeEventChannelToRemove );
    }

    protected boolean isAnalysisCanvasValid()
    {
        return analysisChartCanvas != null && !analysisChartCanvas.isDisposed();
    }

    protected boolean isFullChartCanvasValid()
    {
        return fullChartCanvas != null && !fullChartCanvas.isDisposed();
    }

    public BaseFullChartCanvas<?> getFullChartCanvas()
    {
        return fullChartCanvas;
    }
}
