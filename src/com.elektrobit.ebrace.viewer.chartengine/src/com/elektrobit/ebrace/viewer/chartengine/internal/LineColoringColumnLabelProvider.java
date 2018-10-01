/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public final class LineColoringColumnLabelProvider extends ColumnLabelProvider
{
    private List<Object> selectedElements = new ArrayList<Object>();

    @Override
    public void update(ViewerCell cell)
    {
        super.update( cell );

        for (int i = 0; i < cell.getViewerRow().getColumnCount(); i++)
        {
            if (selectedElements.contains( cell.getElement() ))
            {
                cell.getViewerRow().setBackground( i, Display.getCurrent().getSystemColor( SWT.COLOR_GRAY ) );
            }
            else
            {
                cell.getViewerRow().setBackground( i, Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
            }
        }

    }

    @Override
    public String getText(Object element)
    {
        RuntimeEvent<?> rte = (RuntimeEvent<?>)element;

        return new Long( rte.getTimestamp() ).toString();
    }

    public void setSelectedElements(List<Object> selectedElements)
    {
        this.selectedElements = selectedElements;
    }
}
