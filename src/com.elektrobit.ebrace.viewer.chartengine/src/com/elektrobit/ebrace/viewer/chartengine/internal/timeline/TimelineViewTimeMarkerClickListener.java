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
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.ITmfTimeGraphDrawingHelper;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.TimeGraphControl;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimelineViewTimeMarkerClickListener
        implements
            MouseListener,
            DisposeListener,
            TimeMarkersNotifyCallback,
            MouseMoveListener
{
    private static final int MOUSE_CLICK_MAX_DISTANCE_PX = 5;
    private final ITmfTimeGraphDrawingHelper drawingHelper;
    private final TimeMarkersNotifyUseCase timeMarkersNotifyUseCase;
    private List<TimeMarker> timeMarkers = Collections.emptyList();
    private TimeMarker selectedTimeMarker;
    private final TimeMarkersInteractionUseCase timeMarkersInteractionUseCase;
    private TimeMarker originalDraggedTimeMarker;
    private int dragStartX;
    private final TimelineViewPresentationProvider timelineViewPresentationProvider;
    private DraggedTimeMarker temporaryDraggedTimeMarker;

    public TimelineViewTimeMarkerClickListener(TimeGraphControl timeGraphControl)
    {
        timeGraphControl.addDisposeListener( this );
        timeGraphControl.addMouseListener( this );
        timeGraphControl.addMouseMoveListener( this );
        timelineViewPresentationProvider = (TimelineViewPresentationProvider)timeGraphControl.getTimeGraphProvider();
        drawingHelper = timelineViewPresentationProvider.getDrawingHelper();
        timeMarkersNotifyUseCase = UseCaseFactoryInstance.get().makeTimeMarkersNotifyUseCase( this );
        timeMarkersInteractionUseCase = UseCaseFactoryInstance.get().makeTimeMarkersInteractionUseCase();
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
        // TODO bottom scroll bar not visible after custom changes in chart engine
        TimeMarker doubleClickedTimeMarker = getTimeMarkerForClickedPosition( e.x );
        if (doubleClickedTimeMarker != null)
        {
            timeMarkersInteractionUseCase.removeTimeMarker( doubleClickedTimeMarker );
        }
        else
        {
            long clickTimeNanos = drawingHelper.getTimeAtX( e.x );
            long clickedTimeMicros = TimelineDataConverter.nanosToMicros( clickTimeNanos );
            timeMarkersInteractionUseCase.createTimeMarker( clickedTimeMicros );
        }

    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        long timeInNanos = drawingHelper.getTimeAtX( e.x );
        TimelineDataConverter.nanosToMicros( timeInNanos );
        TimeMarker timeMarkerUnderMouse = getTimeMarkerForClickedPosition( e.x );

        dragStartX = e.x;
        originalDraggedTimeMarker = timeMarkerUnderMouse;
        if (timeMarkerUnderMouse != null)
        {
            temporaryDraggedTimeMarker = new DraggedTimeMarker( timeMarkerUnderMouse.getTimestamp(),
                                                                timeMarkerUnderMouse.getName(),
                                                                timeMarkerUnderMouse.getColor() );
            timelineViewPresentationProvider.setDraggedTimeMarker( temporaryDraggedTimeMarker );
        }
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
        TimeMarker clickedTimeMarker = getTimeMarkerForClickedPosition( e.x );
        if (clickedTimeMarker != null && !clickedTimeMarker.equals( selectedTimeMarker ))
        {
            timeMarkersInteractionUseCase.selectTimeMarker( clickedTimeMarker );
        }

        int dragDiffPx = Math.abs( dragStartX - e.x );
        if (dragDiffPx < MOUSE_CLICK_MAX_DISTANCE_PX)
        {
            originalDraggedTimeMarker = null;
            temporaryDraggedTimeMarker = null;
            timelineViewPresentationProvider.setDraggedTimeMarker( null );
        }

        if (originalDraggedTimeMarker != null)
        {
            long timeNanos = drawingHelper.getTimeAtX( e.x );
            long timeMicros = TimelineDataConverter.nanosToMicros( timeNanos );
            originalDraggedTimeMarker.setTimestamp( timeMicros );
            timeMarkersInteractionUseCase.selectTimeMarker( originalDraggedTimeMarker );
            originalDraggedTimeMarker = null;
            temporaryDraggedTimeMarker = null;
            timelineViewPresentationProvider.setDraggedTimeMarker( null );
        }
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        timeMarkersNotifyUseCase.unregister();
        timeMarkersInteractionUseCase.unregister();
    }

    @Override
    public void onTimeMarkersChanged(List<TimeMarker> timeMarkers)
    {
        this.timeMarkers = timeMarkers;
    }

    @Override
    public void onTimeMarkerSelected(TimeMarker selectedTimeMarker)
    {
        this.selectedTimeMarker = selectedTimeMarker;
    }

    private TimeMarker getTimeMarkerForClickedPosition(int xPosOfMouse)
    {
        long lowerBoundNanos = drawingHelper.getTimeAtX( xPosOfMouse - MOUSE_CLICK_MAX_DISTANCE_PX );
        long upperBoundNanos = drawingHelper.getTimeAtX( xPosOfMouse + MOUSE_CLICK_MAX_DISTANCE_PX );

        long lowerBoundMicros = TimelineDataConverter.nanosToMicros( lowerBoundNanos );
        long upperBoundMicros = TimelineDataConverter.nanosToMicros( upperBoundNanos );
        for (TimeMarker marker : timeMarkers)
        {
            long timemarkerTimeMicros = marker.getTimestamp();
            if (lowerBoundMicros < timemarkerTimeMicros && upperBoundMicros > timemarkerTimeMicros)
            {
                if (marker.isEnabled())
                {
                    return marker;
                }
            }
        }
        return null;
    }

    @Override
    public void mouseMove(MouseEvent e)
    {
        if (temporaryDraggedTimeMarker != null)
        {
            long timeNanos = drawingHelper.getTimeAtX( e.x );
            long timeMicros = TimelineDataConverter.nanosToMicros( timeNanos );
            temporaryDraggedTimeMarker.setTimestamp( timeMicros );
        }

    }
}
