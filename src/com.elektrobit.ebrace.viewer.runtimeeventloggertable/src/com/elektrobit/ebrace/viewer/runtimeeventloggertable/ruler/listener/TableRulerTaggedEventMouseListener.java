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
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class TableRulerTaggedEventMouseListener extends TableRulerMouseListener
{
    private RuntimeEvent<?> eventUnderCursor = null;
    private final CommonFilteredTable table;

    public TableRulerTaggedEventMouseListener(CommonFilteredTable table)
    {
        this.table = table;
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        eventUnderCursor = (RuntimeEvent<?>)findNearbyTimeStamp( e.y );
        if (eventUnderCursor != null)
        {
            table.centerElement( eventUnderCursor );
        }
    }

}
