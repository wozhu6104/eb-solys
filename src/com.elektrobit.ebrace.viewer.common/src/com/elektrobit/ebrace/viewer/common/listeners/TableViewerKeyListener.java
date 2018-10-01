/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.listeners;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

public class TableViewerKeyListener implements KeyListener
{
    private final TableViewer tableViewer;
    private final Text filterTextField;

    public TableViewerKeyListener(TableViewer tableViewerBuilder, Text filterTextField)
    {
        this.tableViewer = tableViewerBuilder;
        this.filterTextField = filterTextField;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        ViewerFilter searchFilter = new ViewerFilter()
        {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element)
            {
                return element.toString().contains( filterTextField.getText() );
            }

        };
        tableViewer.setFilters( new ViewerFilter[]{searchFilter} );
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
    }
}
