/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.elektrobit.ebrace.common.utils.JsonHelper;
import com.elektrobit.ebrace.common.utils.SimpleJsonPath;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.viewer.common.swt.SearchNextPreviousProvider;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebrace.viewer.common.util.SearchHighlighterUtil;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

// referenced by https://infohub.automotive.elektrobit.com/display/EBRACEINTEB/Custom+table+columns+through+JSON+event+value

public class ValueColumnLabelProvider extends StyledCellLabelProvider implements RowFormatter
{
    protected final TimeMarkerManager timeMarkerManager;

    private final Font timeMarkerFont = new Font( null, new FontData( "Arial", 9, SWT.BOLD | SWT.ITALIC ) );
    private final TableModel model;
    private final TableCellBackgroundColorCreator backgroundColorCreator;
    private final SearchNextPreviousProvider searchNextPreviousProvider;

    private TableData tableData = null;

    private final String columnName;

    public ValueColumnLabelProvider(SearchNextPreviousProvider searchNextPreviousProvider, TableModel model,
            TimeMarkerManager timeMarkerManager, String columnName,
            TableCellBackgroundColorCreator backgroundColorCreator)
    {
        this.searchNextPreviousProvider = searchNextPreviousProvider;
        this.model = model;
        this.timeMarkerManager = timeMarkerManager;
        this.columnName = columnName;
        this.backgroundColorCreator = backgroundColorCreator;
    }

    @Override
    public void update(ViewerCell cell)
    {

        Object element = cell.getElement();
        cell.setText( getText( element ) );
        cell.setBackground( getBackground( element ) );
        cell.setFont( getFont( element ) );

        markSearchOccurences( cell );
    }

    private void markSearchOccurences(ViewerCell cell)
    {
        Object element = cell.getElement();
        if (hasTableDataSearchTerm( element ))
        {
            List<Position> positionList = tableData.getSearchPositionMap().get( element ).get( this );

            Color color = ColorPreferences.SEARCH_CELL_HIGHLIGHTED_BG_COLOR;
            int indexToHighlight = searchNextPreviousProvider.getHighlightIndex();
            Object[] array = tableData.getSearchPositionMap().keySet().toArray();
            Object elementToHighlight = array[indexToHighlight];

            if (element.equals( elementToHighlight ))
            {
                color = ColorPreferences.SELECTED_SEARCH_CELL_HIGHLIGHTED_BG_COLOR;
            }
            cell.setStyleRanges( SearchHighlighterUtil.getStyleRanges( positionList, color ) );
        }
        else
        {
            cell.setStyleRanges( null );
        }
    }

    private boolean hasTableDataSearchTerm(Object element)
    {
        if (tableData != null)
        {
            Map<?, Map<RowFormatter, List<Position>>> searchPositionMap = tableData.getSearchPositionMap();
            if (searchPositionMap.containsKey( element ))
            {
                return searchPositionMap.get( element ).containsKey( this );
            }
        }

        return false;
    }

    @Override
    public String getText(Object element)
    {
        String result = null;
        if (element instanceof RuntimeEvent<?>)
        {
            RuntimeEvent<?> event = (RuntimeEvent<?>)element;

            if (columnName.equals( "Value" ))
            {
                if (event.getRuntimeEventChannel().getValueColumnNames().isEmpty())
                {
                    result = event.getSummary();
                }
                else
                {
                    if (event.getValue() instanceof String)
                    {
                        result = new SimpleJsonPath( (String)event.getValue() ).stringValueOf( "value.summary" );
                    }
                }
            }
            else
            {
                if (event.getValue() instanceof String)
                {
                    String valueAsString = (String)event.getValue();
                    if (JsonHelper.isJson( valueAsString ))
                    {
                        result = new SimpleJsonPath( valueAsString ).stringValueOf( "value.details." + columnName );
                    }
                }
                else if (event.getValue() instanceof JsonEvent)
                {
                    JsonEvent e = (JsonEvent)event.getValue();
                    JsonElement details = e.getValue().getDetails();
                    if (details != null)
                    {
                        JsonObject asJsonObject = details.getAsJsonObject();
                        JsonElement resultElement = asJsonObject.get( columnName );
                        if (resultElement != null)
                        {
                            if (resultElement.isJsonPrimitive())
                            {
                                result = resultElement.getAsString();
                            }
                            else
                            {
                                result = resultElement.toString();
                            }
                        }
                    }
                    else if (columnName.equals( "Value" ))
                    {
                        result = e.getValue().getSummary().toString();
                    }
                }
            }
        }

        return result == null ? "" : result;
    }

    private Color getBackground(Object element)
    {
        return backgroundColorCreator.getBackground( model, element );
    }

    private Font getFont(Object element)
    {
        Font result = null;
        if (element instanceof TimeMarker)
        {
            if (element.equals( timeMarkerManager.getCurrentSelectedTimeMarker() ))
            {
                result = timeMarkerFont;
            }
        }
        return result;
    }

    public void setTableData(TableData tableData)
    {
        this.tableData = tableData;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        backgroundColorCreator.dispose();
        timeMarkerFont.dispose();
    }

    public String getColumnName()
    {
        return columnName;
    }
}
