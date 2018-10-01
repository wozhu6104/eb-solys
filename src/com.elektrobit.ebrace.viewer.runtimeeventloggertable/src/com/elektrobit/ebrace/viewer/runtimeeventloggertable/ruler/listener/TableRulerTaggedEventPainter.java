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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.EventTimestampPositionInListConverter;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.TableRuler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class TableRulerTaggedEventPainter implements PaintListener
{
    private static final int PADDING = 5;

    private final TableRuler ruler;
    private TableRulerTaggedEventMouseListener mouseListener;
    private final CommonFilteredTable table;

    public TableRulerTaggedEventPainter(TableRuler ruler, CommonFilteredTable table)
    {
        this.ruler = ruler;
        this.table = table;
        createAndAddMouseListener();
    }

    @Override
    public void paintControl(PaintEvent e)
    {
        List<TimebasedObject> eventsAsTimestamps = new ArrayList<TimebasedObject>( ruler.getTaggedEvents() );
        EventTimestampPositionInListConverter timestampInListToPositionConverter = new EventTimestampPositionInListConverter( ruler
                .getTableData(), eventsAsTimestamps, ruler.getBounds().height - PADDING );
        Map<TimebasedObject, Integer> timestampPositions = timestampInListToPositionConverter.getTimestampPositions();
        updateMouseListener( timestampPositions );
        paintTaggedEvents( e.gc, timestampPositions );
    }

    private void paintTaggedEvents(GC gc, Map<TimebasedObject, Integer> timestampPositions)
    {
        setGCLineParams( gc );
        Iterator<RuntimeEvent<?>> iterator = ruler.getTaggedEvents().iterator();
        while (iterator.hasNext())
        {
            RuntimeEvent<?> event = iterator.next();
            Integer eventPositionInWidget = timestampPositions.get( event );
            if (eventPositionInWidget != null)
            {
                paintTimeLinesVertical( gc, eventPositionInWidget );
            }
        }
    }

    private void paintTimeLinesVertical(GC gc, int timeLinePosInArea)
    {
        gc.drawLine( 10, timeLinePosInArea, ruler.getBounds().width, timeLinePosInArea );
    }

    private void setGCLineParams(GC gc)
    {
        gc.setForeground( ColorPreferences.TAGGED_EVENT_COLOR );
        gc.setLineWidth( 2 );
        gc.setAlpha( 255 );
    }

    private void createAndAddMouseListener()
    {
        mouseListener = new TableRulerTaggedEventMouseListener( table );
        ruler.addMouseListener( mouseListener );
    }

    private void updateMouseListener(Map<TimebasedObject, Integer> timestampPositions)
    {
        mouseListener.setTimestampPositions( timestampPositions );
    }
}
