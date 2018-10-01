/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class CommonFilteredTree extends FilteredTree
{
    private final int DEFAULT_WIDTH = 1;
    private final boolean DEFAULT_WIDTH_IN_PERCENT = true;
    private final boolean DEFAULT_RESIZABLE = true;

    private TreeColumnLayout treeColumnLayout;
    private int width = DEFAULT_WIDTH;
    private boolean widthInPercent = DEFAULT_WIDTH_IN_PERCENT;
    private boolean resizable = DEFAULT_RESIZABLE;
    private Object pendingInput = null;
    private boolean scrollbarBeingDragged = false;

    public CommonFilteredTree(Composite parent, int treeStyle, PatternFilter filter)
    {
        super( parent, treeStyle, filter, true );
        this.getViewer().getTree().setHeaderVisible( true );
        this.getViewer().getTree().setLinesVisible( true );
    }

    @Override
    protected Control createTreeControl(Composite parent, int style)
    {
        initTreeColumnLayout();
        Control control = super.createTreeControl( parent, style );
        control.getParent().setLayout( treeColumnLayout );
        registerVerticalBarListener();
        return control;
    }

    private void registerVerticalBarListener()
    {
        ScrollBar verticalBar = getViewer().getTree().getVerticalBar();
        verticalBar.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if ((e.stateMask & SWT.BUTTON1) != 0)
                {
                    scrollbarBeingDragged = true;
                }
                if (e.stateMask == 0)
                {
                    scrollbarBeingDragged = false;
                    onScrollbarReleased();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );
    }

    /**
     * Use this function to set input instead of getViewer().setInput() to enable 2 features of this class:
     * 
     * <ul>
     * <li>when scrollbar is being dragged, setting the new input is delayed until the scrollbar is released</li>
     * <li>save and apply expanded nodes when setting new content</li>
     * </ul>
     * 
     * Note: Setting input to FilteredTree while scrollbar is being dragged, causes main thread to be blocked for
     * several seconds, this class offers a workaround
     * 
     * @param input
     */
    public void setInput(Object input)
    {
        if (scrollbarBeingDragged)
        {
            pendingInput = input;
        }
        else
        {
            setInputPreserveExpandedElements( input );
        }
    }

    private void setInputPreserveExpandedElements(Object input)
    {
        Object[] expandedElements = getViewer().getExpandedElements();
        getViewer().setInput( input );
        getViewer().setExpandedElements( expandedElements );
    }

    private void onScrollbarReleased()
    {
        if (pendingInput != null)
        {
            setInputPreserveExpandedElements( pendingInput );
            pendingInput = null;
        }
    }

    private void initTreeColumnLayout()
    {
        treeColumnLayout = new TreeColumnLayout();
    }

    public void setHeaderVisible(boolean show)
    {
        this.getViewer().getTree().setHeaderVisible( show );
    }

    public void setLinesInvisible(boolean show)
    {
        this.getViewer().getTree().setLinesVisible( show );
    }

    /**
     * Sets the width of the next created {@link TreeViewerColumn} in percent.
     * 
     * @param width
     *            the width of the {@link TreeViewerColumn} in percent
     */
    public void setWidthInPercent(int width)
    {
        this.width = width;
        this.widthInPercent = true;
    }

    /**
     * Sets the width of the next created {@link TreeViewerColumn} in pixel.
     * 
     * @param width
     *            the width of the {@link TreeViewerColumn} in pixel
     */
    public void setWidthInPixel(int width)
    {
        this.width = width;
        this.widthInPercent = false;
    }

    /**
     * Specify if the next created {@link TreeViewerColumn} is resizable.
     * 
     * @param resizable
     *            if true the {@link TreeViewerColumn} is resizable
     */
    public void setResizable(boolean resizable)
    {
        this.resizable = resizable;
    }

    /**
     * Adds a {@link TreeViewerColumn} to the {@link TreeColumnLayout}. By default the {@link TreeViewerColumn} is
     * resizable, with all columns having the same percentage size.
     * 
     * @param columnName
     *            the name of the {@link TreeViewerColumn}
     * @param labelProvider
     *            the label provider of the {@link TreeViewerColumn}
     */
    public TreeViewerColumn createColumn(String columnName, CellLabelProvider labelProvider)
    {
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn( this.getViewer(), SWT.NONE );
        treeViewerColumn.getColumn().setText( columnName );
        treeViewerColumn.setLabelProvider( labelProvider );
        treeViewerColumn.getColumn().setResizable( resizable );
        if (widthInPercent)
        {
            treeColumnLayout.setColumnData( treeViewerColumn.getColumn(), new ColumnWeightData( width, resizable ) );
        }
        else
        {
            treeColumnLayout.setColumnData( treeViewerColumn.getColumn(), new ColumnPixelData( width, resizable ) );
        }
        resetDefaultValues();
        return treeViewerColumn;
    }

    private void resetDefaultValues()
    {
        setWidthInPercent( DEFAULT_WIDTH );
        setResizable( DEFAULT_RESIZABLE );
    }
}
