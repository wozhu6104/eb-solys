/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.timeline;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.ITimeGraphColorListener;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.ITimeGraphPresentationProvider2;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.StateItem;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.TimeGraphViewer;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.ITmfTimeGraphDrawingHelper;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebrace.viewer.chartengine.internal.Activator;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.rendering.ChartTimeMarkerRenderer;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimelineViewPresentationProvider
        implements
            ITimeGraphPresentationProvider2,
            DisposeListener,
            TimeMarkersNotifyCallback
{
    private static final int DRAGGED_TIMEMARKER_ALPHA = 125;
    private Color labelTextColor;
    private Integer fAverageCharWidth;
    private final GenericListenerCaller<ITimeGraphColorListener> colorListeners = new GenericListenerCaller<ITimeGraphColorListener>();
    private ITmfTimeGraphDrawingHelper drawingHelper;
    private StateItem[] stateTable;
    private Map<SColor, Integer> colorToStateTableIndex;
    private final ResourceManager resManager;
    private final TimeMarkersNotifyUseCase timeMarkersNotifyUseCase;
    private List<TimeMarker> timeMarkers = Collections.emptyList();
    private TimeMarker selectedTimeMarker = null;
    private final Composite composite;
    private final int graphTopPaddingForTimemarkers;
    private TimeMarker draggedTimeMarker;
    private final int nameSpaceWidth;
    private final TimeGraphViewer timeGraphViewer;

    public TimelineViewPresentationProvider(TimeGraphViewer timeGraphViewer, Composite composite,
            int graphTopPaddingForTimemarkers, int nameSpaceWidth)
    {
        this.timeGraphViewer = timeGraphViewer;
        this.composite = composite;
        this.graphTopPaddingForTimemarkers = graphTopPaddingForTimemarkers;
        this.timeGraphViewer.getTimeGraphControl().setGraphTopPadding( graphTopPaddingForTimemarkers );
        this.nameSpaceWidth = nameSpaceWidth;
        resManager = new LocalResourceManager( JFaceResources.getResources(), composite );
        composite.addDisposeListener( this );
        timeMarkersNotifyUseCase = UseCaseFactoryInstance.get().makeTimeMarkersNotifyUseCase( this );
    }

    @Override
    public boolean displayTimesInTooltip()
    {
        return true;
    }

    @Override
    public Map<String, String> getEventHoverToolTipInfo(ITimeEvent timeEvent)
    {
        throw new UnsupportedOperationException( "function not implemented" );
    }

    @Override
    public Map<String, String> getEventHoverToolTipInfo(ITimeEvent event, long arg1)
    {
        SolysTimeEvent solysEvent = (SolysTimeEvent)event;
        String label = solysEvent.getTimeSegment().getLabel();
        Map<String, String> retMap = new LinkedHashMap<>();
        retMap.put( "Label", label );
        return retMap;
    }

    @Override
    public String getEventName(ITimeEvent event)
    {
        return null;
    }

    @Override
    public int getItemHeight(ITimeGraphEntry arg0)
    {
        return 20;
    }

    @Override
    public Image getItemImage(ITimeGraphEntry arg0)
    {
        return Activator.getDefault().getImage( "channel", "png" );
    }

    @Override
    public StateItem[] getStateTable()
    {
        return stateTable;
    }

    @Override
    public int getStateTableIndex(ITimeEvent event)
    {
        SolysTimeEvent solysEvent = (SolysTimeEvent)event;
        SColor color = solysEvent.getTimeSegment().getColor();
        Integer stateTableIndex = colorToStateTableIndex.get( color );
        if (stateTableIndex == null)
        {
            return TRANSPARENT;
        }
        return stateTableIndex;
    }

    @Override
    public String getStateTypeName()
    {
        return null;
    }

    @Override
    public String getStateTypeName(ITimeGraphEntry arg0)
    {
        return "Channel Name"; // this is used as a label before the channel name
    }

    @Override
    public void postDrawControl(Rectangle bounds, GC gc)
    {
        gc.setAntialias( SWT.ON );
        drawTimeMarkers( bounds, gc );
    }

    private void drawTimeMarkers(Rectangle bounds, GC gc)
    {
        Rectangle boundsWithoutNameSpace = new Rectangle( bounds.x + nameSpaceWidth,
                                                          bounds.y,
                                                          bounds.width - nameSpaceWidth,
                                                          bounds.height );
        gc.setClipping( boundsWithoutNameSpace );

        for (TimeMarker timeMarker : timeMarkers)
        {
            if (!timeMarker.isEnabled())
            {
                continue;
            }
            drawTimeMarker( timeMarker, bounds, gc );
        }
        drawDraggedTimeMarker( bounds, gc );
        gc.setClipping( (Rectangle)null );
    }

    private void drawTimeMarker(TimeMarker timeMarker, Rectangle bounds, GC gc)
    {
        int xForTime = getDrawingHelper()
                .getXForTime( TimelineDataConverter.microsToNanos( timeMarker.getTimestamp() ) );

        Point topPoint = new Point( xForTime, bounds.y + graphTopPaddingForTimemarkers );
        Point bottomPoint = new Point( xForTime, bounds.y + bounds.height );

        boolean selected = timeMarker.equals( selectedTimeMarker );
        ChartTimeMarkerRenderer.drawTimeMarkerTiltedText( gc, topPoint, bottomPoint, timeMarker, selected, resManager );
    }

    private void drawDraggedTimeMarker(Rectangle bounds, GC gc)
    {
        if (draggedTimeMarker != null)
        {
            int origAlpha = gc.getAlpha();
            gc.setAlpha( DRAGGED_TIMEMARKER_ALPHA );
            drawTimeMarker( draggedTimeMarker, bounds, gc );
            gc.setAlpha( origAlpha );
        }
    }

    @Override
    public void postDrawEntry(ITimeGraphEntry arg0, Rectangle arg1, GC arg2)
    {
    }

    @Override
    public void postDrawEvent(ITimeEvent event, Rectangle bounds, GC gc)
    {
        if (labelTextColor == null)
        {
            labelTextColor = gc.getDevice().getSystemColor( SWT.COLOR_WHITE );
        }
        if (fAverageCharWidth == null)
        {
            fAverageCharWidth = gc.getFontMetrics().getAverageCharWidth();
        }

        ITmfTimeGraphDrawingHelper drawingHelper = getDrawingHelper();
        if (bounds.width <= fAverageCharWidth)
        {
            return;
        }

        if (!(event instanceof TimeEvent))
        {
            return;
        }

        SolysTimeEvent solysTimeEvent = (SolysTimeEvent)event;
        STimeSegment timeSegment = solysTimeEvent.getTimeSegment();
        drawLabelOnEvent( timeSegment.getLabel(), event, bounds, gc, drawingHelper );
    }

    private void drawLabelOnEvent(String label, ITimeEvent event, Rectangle bounds, GC gc,
            ITmfTimeGraphDrawingHelper drawingHelper)
    {
        long eventStartTime = event.getTime();
        int x = Math.max( drawingHelper.getXForTime( eventStartTime ), bounds.x );
        if (x >= bounds.x + bounds.width)
        {
            return;
        }
        long endTime = event.getTime() + event.getDuration();
        int xForEndTime = drawingHelper.getXForTime( endTime );
        int width = Math.min( xForEndTime, bounds.x + bounds.width ) - x - 1;

        gc.setForeground( labelTextColor );
        Utils.drawText( gc, label, x + 1, bounds.y, width, bounds.height, true, true );
    }

    @Override
    public ITmfTimeGraphDrawingHelper getDrawingHelper()
    {
        return drawingHelper;
    }

    @Override
    public void addColorListener(ITimeGraphColorListener listener)
    {
        colorListeners.add( listener );
    };

    @Override
    public void removeColorListener(ITimeGraphColorListener listener)
    {
        colorListeners.remove( listener );
    }

    private void notifyColorSettingsChanged()
    {
        colorListeners.notifyListeners( listener -> listener.colorSettingsChanged( getStateTable() ) );
    }

    @Override
    public void setDrawingHelper(ITmfTimeGraphDrawingHelper drawingHelper)
    {
        this.drawingHelper = drawingHelper;
    }

    public void setPossibleColors(Set<SColor> allColors)
    {
        updateStateTable( allColors );
    }

    private void updateStateTable(Set<SColor> allColors)
    {
        stateTable = new StateItem[allColors.size()];
        colorToStateTableIndex = new HashMap<>();
        int index = 0;
        for (SColor color : allColors)
        {
            colorToStateTableIndex.put( color, index );
            RGB colorRGB = new RGB( color.getRed(), color.getGreen(), color.getBlue() );
            stateTable[index] = new StateItem( colorRGB );
            index++;
        }
        notifyColorSettingsChanged();
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        timeMarkersNotifyUseCase.unregister();
    }

    @Override
    public void onTimeMarkersChanged(List<TimeMarker> timeMarkers)
    {
        this.timeMarkers = timeMarkers;
        redraw();
    }

    @Override
    public void onTimeMarkerSelected(TimeMarker selectedTimeMarker)
    {
        this.selectedTimeMarker = selectedTimeMarker;
        moveChartToTimeMarker( selectedTimeMarker );
        redraw();
    }

    private void moveChartToTimeMarker(TimeMarker timeMarker)
    {
        if (timeMarker != null)
        {
            long timestampNanos = TimelineDataConverter.microsToNanos( timeMarker.getTimestamp() );
            timeGraphViewer.setSelectedTime( timestampNanos, true );
        }
    }

    private void redraw()
    {
        composite.redraw();
    }

    public void setDraggedTimeMarker(TimeMarker draggedTimeMarker)
    {
        this.draggedTimeMarker = draggedTimeMarker;

    }
}
