/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimeMarkerMouseListener implements MouseListener, MouseTrackListener, DisposeListener
{

    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();
    private TimeMarker draggedTimeMarker;

    protected final int parentControlOrientation;

    protected double xOffset;
    protected double yOffset;
    protected double plotWidth;
    protected double plotHeight;

    protected long minTimeAxis;
    protected long maxTimeAxis;

    protected Control parent;

    private final Cursor handCursor = new Cursor( Display.getCurrent(), SWT.CURSOR_HAND );
    private final Cursor arrowCursor = new Cursor( Display.getCurrent(), SWT.CURSOR_ARROW );

    private final TimeFormatter mouseHoverTimeFormatter = new TimeFormatter( "dd/MM/yyyy HH:mm:ss:SSSSSS" );

    public TimeMarkerMouseListener(Control parent, int isHorizontal)
    {
        this.parent = parent;
        this.parentControlOrientation = isHorizontal;
        parent.addDisposeListener( this );
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
        int mousePos = getCoordXOrYMousePositionForOrientation( e );
        boolean removed = timeMarkerManager
                .removeTimeMarkerForClickedPosition( xOffset, plotWidth, minTimeAxis, maxTimeAxis, mousePos );
        if (!removed && isClickInsideChartArea( e ))
        {
            double newValue = TimestampPositionInChartConverter
                    .calculateTimestampForClick( xOffset,
                                                 plotWidth,
                                                 mousePos,
                                                 this.minTimeAxis,
                                                 this.maxTimeAxis - this.minTimeAxis );
            timeMarkerManager.createNewTimeMarker( (long)newValue );
        }
    }

    protected boolean isClickInsideChartArea(MouseEvent e)
    {
        if (e.x < xOffset || e.x > (xOffset + plotWidth))
        {
            return false;
        }
        if (e.y < yOffset || e.y > (yOffset + plotHeight))
        {
            return false;
        }
        return true;
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        if (e.button == 1)
        {
            this.draggedTimeMarker = timeMarkerManager
                    .getTimeMarkerForClickedPosition( xOffset,
                                                      plotWidth,
                                                      minTimeAxis,
                                                      maxTimeAxis,
                                                      getCoordXOrYMousePositionForOrientation( e ) );
        }
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
        int mousePos = getCoordXOrYMousePositionForOrientation( e );
        TimeMarker clickedTimemarker = timeMarkerManager
                .getTimeMarkerForClickedPosition( xOffset, plotWidth, minTimeAxis, maxTimeAxis, mousePos );
        if (clickedTimemarker != null)
        {
            timeMarkerManager.setCurrentSelectedTimeMarker( clickedTimemarker, false );
        }
        else if (draggedTimeMarker != null)
        {
            calculateAndSetnewTimestampToExistentTimeline( draggedTimeMarker, mousePos );
            timeMarkerManager.setCurrentSelectedTimeMarker( draggedTimeMarker, false );
            draggedTimeMarker = null;
        }
        else
        {
            timeMarkerManager.setCurrentSelectedTimeMarker( null, false );
        }
    }

    @Override
    public void mouseHover(MouseEvent e)
    {

        TimeMarker line = timeMarkerManager
                .getTimeMarkerForClickedPosition( xOffset,
                                                  plotWidth,
                                                  minTimeAxis,
                                                  maxTimeAxis,
                                                  getCoordXOrYMousePositionForOrientation( e ) );

        if (line != null)
        {
            this.parent.setCursor( handCursor );
            this.parent.setToolTipText( "Name: " + line.getName() + System.getProperty( "line.separator" )
                    + "Timestamp: " + mouseHoverTimeFormatter.formatMicros( line.getTimestamp() ) );
        }
    }

    protected int getCoordXOrYMousePositionForOrientation(MouseEvent mousePosition)
    {
        int mousePos = mousePosition.x;
        switch (parentControlOrientation)
        {
            case SWT.VERTICAL :
                mousePos = mousePosition.y;
            default :
                return mousePos;
        }
    }

    protected void calculateAndSetnewTimestampToExistentTimeline(TimeMarker timeline, int pos)
    {
        long xPositionForTimeLine = (long)TimestampPositionInChartConverter
                .calculateTimestampForClick( xOffset, plotWidth, pos, minTimeAxis, maxTimeAxis - minTimeAxis );
        timeline.setTimestamp( xPositionForTimeLine );
    }

    public void updateValues(double xOffset, double yOffset, double plotWidth, double plotHeight, long minTimeAxis,
            long maxTimeAxis)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.plotWidth = plotWidth;
        this.plotHeight = plotHeight;
        this.minTimeAxis = minTimeAxis;
        this.maxTimeAxis = maxTimeAxis;
    }

    @Override
    public void mouseEnter(MouseEvent e)
    {
        resetCursorAndToolTip();
    }

    @Override
    public void mouseExit(MouseEvent e)
    {
        resetCursorAndToolTip();
    }

    private void resetCursorAndToolTip()
    {
        this.parent.setCursor( arrowCursor );
        this.parent.setToolTipText( null );
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        handCursor.dispose();
        arrowCursor.dispose();
    }
}
