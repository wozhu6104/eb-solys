/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.views;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.elektrobit.ebrace.viewer.ViewerPlugin;

public class TreeViewerColumnBuilder
{
    private final TreeViewerColumn treeViewerColumn;
    private final TreeViewer treeViewer;
    private CellLabelProvider labelProvider;

    public TreeViewerColumnBuilder(TreeViewer treeViewer)
    {
        this.treeViewer = treeViewer;
        treeViewerColumn = new TreeViewerColumn( treeViewer, SWT.NONE );
    }

    TreeViewerColumn getTreeViewerColumn()
    {
        return treeViewerColumn;
    }

    public TreeViewerColumnBuilder columnHeaderName(String columnHeaderName)
    {

        treeViewerColumn.getColumn().setText( columnHeaderName );
        return this;
    }

    public TreeViewerColumnBuilder columnHeaderIcon(String iconName, String extension)
    {
        Image columnHeaderIcon = ViewerPlugin.getPluginInstance().getImage( iconName, extension );
        treeViewerColumn.getColumn().setImage( columnHeaderIcon );
        return this;
    }

    public TreeViewerColumnBuilder columnHeaderTooltip(final String columnHeaderTooltip)
    {
        treeViewerColumn.getColumn().getDisplay().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                treeViewerColumn.getColumn().setToolTipText( columnHeaderTooltip );
            }
        } );
        return this;
    }

    public TreeViewerColumnBuilder columnHeaderResizable(boolean resizable)
    {
        treeViewerColumn.getColumn().setResizable( resizable );
        return this;
    }

    public TreeViewerColumnBuilder columnWidth(int columnWidth)
    {
        treeViewerColumn.getColumn().setWidth( columnWidth );
        return this;
    }

    public TreeViewerColumnBuilder labelProvider(CellLabelProvider labelProvider)
    {
        this.labelProvider = labelProvider;
        treeViewerColumn.setLabelProvider( labelProvider );
        return this;
    }

    public TreeViewerColumnBuilder columnEditingSupport(EditingSupport editingSupport)
    {
        treeViewerColumn.setEditingSupport( editingSupport );
        return this;
    }

    public TreeViewerColumnBuilder listener(int eventType,
            AbstractTreeColumnImagePainter visibleNodeIconPaintItemListener)
    {
        treeViewer.getTree().addListener( eventType, visibleNodeIconPaintItemListener );
        return this;
    }

    public TreeViewerColumn build()
    {
        if (labelProvider == null)
        {
            labelProvider( new EmptyColumnLabelProvider() );
        }
        return treeViewerColumn;
    }
}
