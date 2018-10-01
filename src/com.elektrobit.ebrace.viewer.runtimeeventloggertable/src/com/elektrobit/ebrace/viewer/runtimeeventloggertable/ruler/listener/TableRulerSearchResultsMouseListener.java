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

import org.eclipse.swt.events.MouseEvent;

import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class TableRulerSearchResultsMouseListener extends TableRulerMouseListener
{

    private TimebasedObject searchResultUnderCursor = null;
    private final CommonFilteredTable table;

    public TableRulerSearchResultsMouseListener(CommonFilteredTable table)
    {
        this.table = table;
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        searchResultUnderCursor = findNearbyTimeStamp( e.y );
        if (searchResultUnderCursor != null)
        {
            table.centerElement( searchResultUnderCursor );
        }
    }

}
