/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.EventTimestampPositionInListConverter;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.TableRuler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TableRulerTimemarkerPainter implements PaintListener
{
    private static final int PADDING = 5;

    private final TableRuler ruler;
    private TableRulerTimemarkerMouseListener mouseListener;
    private List<TimeMarker> timeMarkers = Collections.emptyList();

    public TableRulerTimemarkerPainter(TableRuler ruler)
    {
        this.ruler = ruler;
        createAndAddMouseListener();
    }

    public void setTimeMarkers(List<TimeMarker> timeMarkers)
    {
        this.timeMarkers = timeMarkers;
    }

    @Override
    public void paintControl(PaintEvent e)
    {
        List<TimebasedObject> timeMarkersAsTimestamps = new ArrayList<TimebasedObject>( timeMarkers );
        EventTimestampPositionInListConverter timestampInListToPositionConverter = new EventTimestampPositionInListConverter( ruler
                .getTableData(), timeMarkersAsTimestamps, ruler.getBounds().height - PADDING );
        Map<TimebasedObject, Integer> timestampPositions = timestampInListToPositionConverter.getTimestampPositions();
        updateMouseListener( timestampPositions );
        paintTimeMarkers( e.gc, timeMarkers, timestampPositions );
    }

    private void paintTimeMarkers(GC gc, List<TimeMarker> timeMarkers, Map<TimebasedObject, Integer> timestampPositions)
    {
        setGCLineParams( gc );
        Iterator<TimeMarker> iterator = timeMarkers.iterator();
        while (iterator.hasNext())
        {
            TimeMarker timeMarker = iterator.next();
            if (timeMarker.isEnabled())
            {
                Integer timeMarkerPositionInWidget = timestampPositions.get( timeMarker );
                if (timeMarkerPositionInWidget != null)
                {
                    paintTimeLinesVertical( gc, timeMarkerPositionInWidget );
                }
            }
        }
    }

    private void paintTimeLinesVertical(GC gc, int timeLinePosInArea)
    {
        gc.drawLine( 10, timeLinePosInArea, ruler.getBounds().width, timeLinePosInArea );
    }

    private void setGCLineParams(GC gc)
    {
        gc.setForeground( ColorPreferences.TIMEMARKER_COLOR );
        gc.setLineWidth( 2 );
        gc.setAlpha( 255 );
    }

    private void createAndAddMouseListener()
    {
        mouseListener = new TableRulerTimemarkerMouseListener( ruler );
        ruler.addMouseListener( mouseListener );
        ruler.addMouseTrackListener( mouseListener );
    }

    private void updateMouseListener(Map<TimebasedObject, Integer> timestampPositions)
    {
        mouseListener.setTimestampPositions( timestampPositions );
    }

}
