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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.EventTimestampPositionInListConverter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.TableRuler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class TableRulerSearchResultsPainter implements PaintListener
{
    private final TableRuler ruler;
    private Map<TimebasedObject, Integer> timestampPositions = new HashMap<TimebasedObject, Integer>();
    private static final int FILTER_OFFSET = 7;
    private TableData filterResultData;
    int rulerHeigh = -1;
    private final CommonFilteredTable table;
    private TableRulerSearchResultsMouseListener mouseListener;

    public TableRulerSearchResultsPainter(TableRuler ruler, CommonFilteredTable table)
    {
        this.ruler = ruler;
        this.table = table;
        createAndAddMouseListener();
    }

    private void createAndAddMouseListener()
    {
        mouseListener = new TableRulerSearchResultsMouseListener( table );
        ruler.addMouseListener( mouseListener );
    }

    @Override
    public void paintControl(PaintEvent e)
    {
        if (table.isSearchModeOn())
        {
            if (rulerHeigh != e.height)
            {
                rulerHeigh = e.height;
                calculateTimestampPositions( filterResultData );
            }
            paintFilters( e.gc );
        }
    }

    private void paintFilters(GC gc)
    {
        setGCLineParams( gc );
        for (int timestampPosition : timestampPositions.values())
        {
            paintTimestampPositionVertical( gc, timestampPosition );
        }
    }

    private void setGCLineParams(GC gc)
    {
        gc.setForeground( ColorPreferences.SEARCH_CELL_HIGHLIGHTED_BG_COLOR_RULER );
        gc.setLineWidth( 2 );
        gc.setAlpha( 80 );
    }

    private void paintTimestampPositionVertical(GC gc, int linePositionInArea)
    {
        gc.drawLine( FILTER_OFFSET, linePositionInArea, ruler.getBounds().width, linePositionInArea );
    }

    public void onNewData(TableData filterResultData)
    {
        this.filterResultData = filterResultData;
        calculateTimestampPositions( filterResultData );
    }

    private void calculateTimestampPositions(TableData filterResultData)
    {
        if (filterResultData != null)
        {
            @SuppressWarnings("unchecked")
            List<TimebasedObject> itemsToBeDisplayed = (List<TimebasedObject>)filterResultData.getItemsToBeDisplayed();
            EventTimestampPositionInListConverter timestampInListToPositionConverter = new EventTimestampPositionInListConverter( itemsToBeDisplayed,
                                                                                                                                  filterResultData
                                                                                                                                          .getSearchMatchingItems(),
                                                                                                                                  ruler.getBounds().height
                                                                                                                                          - 5 );
            timestampPositions = timestampInListToPositionConverter.getTimestampPositions();
            updateMouseListener( timestampPositions );
        }
    }

    private void updateMouseListener(Map<TimebasedObject, Integer> timestampPositions)
    {
        mouseListener.setTimestampPositions( timestampPositions );
    }

}
