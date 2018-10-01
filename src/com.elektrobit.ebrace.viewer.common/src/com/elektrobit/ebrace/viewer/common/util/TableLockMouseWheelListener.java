/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.util;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;

public class TableLockMouseWheelListener implements MouseWheelListener
{

    CommonFilteredTable table;

    public TableLockMouseWheelListener(CommonFilteredTable table)
    {
        this.table = table;
    }

    @Override
    public void mouseScrolled(MouseEvent e)
    {
        if (isScrollUp( e ))
        {
            if (table.isNotScrollLocked())
            {
                TableLockerUtil.changeLockStateTable();
            }
            table.getViewer().getTable().redraw();
        }
    }

    private boolean isScrollUp(MouseEvent e)
    {
        return e.count >= 0 ? true : false;
    }
}
