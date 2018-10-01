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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;

public class ScrollbarListener implements SelectionListener
{

    CommonFilteredTable table;

    public ScrollbarListener(CommonFilteredTable table)
    {
        this.table = table;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        table.onScrollbarSelected();
        if (table.isNotScrollLocked())
        {
            TableLockerUtil.changeLockStateTable();
        }
        table.getViewer().getTable().redraw();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

}
