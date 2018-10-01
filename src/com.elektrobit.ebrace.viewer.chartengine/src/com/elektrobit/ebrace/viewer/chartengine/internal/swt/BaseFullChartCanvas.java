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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.viewer.common.timemarker.listener.TimeMarkerChangeTimeSpanMouseListener;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.ibm.icu.util.Calendar;

public abstract class BaseFullChartCanvas<T> extends BaseChartCanvas<T>

{
    protected boolean changeTimespanLineBounds;
    private final TimeMarkerChangeTimeSpanMouseListener handleTimlinesListener;
    private ResourceManager resManager = null;

    private final Calendar timeAxisMin = Calendar.getInstance();
    private final Calendar timeAxisMax = Calendar.getInstance();
    private final long startTimestampOfFullChartInMillis;
    private final long endTimestampOfFullChartInMillis;

    public BaseFullChartCanvas(Set<RuntimeEventChannel<T>> channels, long startTimestampOfFullChartInMillis,
            long endTimestampOfFullChartInMillis, boolean aggregate, Composite parent, int style, ChartModel model)
    {
        super( channels, "Overview Chart", parent, style, model );
        this.startTimestampOfFullChartInMillis = startTimestampOfFullChartInMillis;
        this.endTimestampOfFullChartInMillis = endTimestampOfFullChartInMillis;
        timeAxisMin.setTimeInMillis( startTimestampOfFullChartInMillis );
        timeAxisMax.setTimeInMillis( endTimestampOfFullChartInMillis );

        this.handleTimlinesListener = new TimeMarkerChangeTimeSpanMouseListener( this, SWT.HORIZONTAL, false );
        addMouseListener( this.handleTimlinesListener );
        addMouseTrackListener( this.handleTimlinesListener );
        addMouseMoveListener( this.handleTimlinesListener );
        createContextMenu();
        resManager = new LocalResourceManager( JFaceResources.getResources(), parent );
    }

    protected void setTimelineRuler()
    {
        updateTimelineRulerListenerValues();
    }

    private void paintTimespanAreaHorizontal(GC gc)
    {
        long fullTimespanLength = endTimestampOfFullChartInMillis - startTimestampOfFullChartInMillis;
        long analysisTimespanStart = analysisTimespanPreferences.getAnalysisTimespanStart();
        long analysisTimespanEnd = analysisTimespanPreferences.getAnalysisTimespanEnd();

        double startAreaPos = TimestampPositionInChartConverter
                .calculatePositionForTimestamp( chartState.getComputations().getPlotBounds().getLeft(),
                                                chartState.getComputations().getPlotBounds().getWidth(),
                                                analysisTimespanStart,
                                                this.startTimestampOfFullChartInMillis,
                                                fullTimespanLength );

        double endAreaPos = TimestampPositionInChartConverter
                .calculatePositionForTimestamp( chartState.getComputations().getPlotBounds().getLeft(),
                                                chartState.getComputations().getPlotBounds().getWidth(),
                                                analysisTimespanEnd,
                                                this.startTimestampOfFullChartInMillis,
                                                fullTimespanLength );
        validateAndDrawTimespanAreaHorizontal( startAreaPos, endAreaPos, gc );
    }

    @Override
    protected SortedSet<TimeMarker> getTimeMarkersToDraw()
    {
        return timeMarkerManager.getAllTimeMarkers();
    }

    @Override
    protected TimeMarkerLabelStyle getTimeMarkerLabelStyle()
    {
        return TimeMarkerLabelStyle.HORIZONTAL;
    }

    private void validateAndDrawTimespanAreaHorizontal(double startAreaPos, double endAreaPos, GC gc)
    {
        int endPos = (int)(endAreaPos - startAreaPos);
        gc.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );
        gc.setAlpha( ColorPreferences.ANALYSIS_TIMESPAN_FILL_ALPHA );
        gc.fillRectangle( (int)startAreaPos,
                          (int)chartState.getComputations().getPlotBounds().getTop(),
                          endPos,
                          (int)chartState.getComputations().getPlotBounds().getHeight() );

        int lineWidth = 1;
        gc.setLineWidth( lineWidth );
        gc.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );
        gc.setAlpha( 255 );
        gc.drawRectangle( (int)startAreaPos,
                          (int)chartState.getComputations().getPlotBounds().getTop(),
                          endPos,
                          (int)chartState.getComputations().getPlotBounds().getHeight() - lineWidth );
    }

    private void updateTimelineRulerListenerValues()
    {
        if (handleTimlinesListener != null && chartState != null)
        {
            double xOffset = chartState.getComputations().getPlotBounds().getLeft();
            double yOffset = chartState.getComputations().getPlotBounds().getTop();
            double plotWidth = chartState.getComputations().getPlotBounds().getWidth();
            double plotHeight = chartState.getComputations().getPlotBounds().getHeight();

            long minTimeAxis = startTimestampOfFullChartInMillis;
            long maxTimeAxis = endTimestampOfFullChartInMillis;
            handleTimlinesListener.updateValues( xOffset, yOffset, plotWidth, plotHeight, minTimeAxis, maxTimeAxis );
        }
    }

    @Override
    public void dispose()
    {
        if (resManager != null)
        {
            resManager.dispose();
        }
        super.dispose();
    }

    @Override
    protected void paintControlFinished(PaintEvent pe)
    {
        pe.gc.setAntialias( SWT.ON );
        updateTimelineRulerListenerValues();
        super.paintControlFinished( pe );
        paintTimespanAreaHorizontal( pe.gc );
    }

    @Override
    protected long getChartStartTime()
    {
        return startTimestampOfFullChartInMillis;
    }

    @Override
    protected long getChartEndTime()
    {
        return endTimestampOfFullChartInMillis;
    }
}
