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

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public abstract class AbstractTreeColumnImagePainter implements Listener
{
    private static final Logger LOG = Logger.getLogger( AbstractTreeColumnImagePainter.class );

    protected final TreeColumn treeColumn;

    public AbstractTreeColumnImagePainter(TreeColumn treeColumn)
    {
        this.treeColumn = treeColumn;
    }

    @Override
    public void handleEvent(Event event)
    {
        TreeNode node = getTreeNode( event );
        if (node == null)
        {
            return;
        }

        Image tmpImage = getImageForViewerNode( node );
        if (tmpImage == null)
        {
            return;
        }
        int tmpWidth = 0;
        int tmpHeight = 0;
        int tmpX = 0;
        int tmpY = 0;

        tmpWidth = treeColumn.getWidth();
        tmpHeight = getTreeItem( event ).getBounds().height;

        tmpX = tmpImage.getBounds().width;
        tmpX = (tmpWidth / 2 - tmpX / 2);
        tmpY = tmpImage.getBounds().height;
        tmpY = (tmpHeight / 2 - tmpY / 2);
        if (tmpX <= 0)
        {
            tmpX = event.x;
        }
        else
        {
            tmpX += event.x;
        }
        if (tmpY <= 0)
        {
            tmpY = event.y;
        }
        else
        {
            tmpY += event.y;
        }
        event.gc.drawImage( tmpImage, tmpX, tmpY );
    }

    private TreeNode getTreeNode(Event event)
    {

        TreeItem treeItem = getTreeItem( event );
        if (treeItem == null)
        {
            return null;
        }
        if (treeItem.getData() instanceof TreeNode)
        {
            return (TreeNode)treeItem.getData();
        }
        else if (treeItem.getData() instanceof com.elektrobit.ebsolys.core.targetdata.api.structure.Tree)
        {
            com.elektrobit.ebsolys.core.targetdata.api.structure.Tree tree = (com.elektrobit.ebsolys.core.targetdata.api.structure.Tree)treeItem
                    .getData();
            return tree.getRootNode();
        }
        else
        {
            LOG.warn( "Unknow type of object in tree " + treeItem.getData().getClass() );
        }
        return null;

    }

    private TreeItem getTreeItem(Event event)
    {
        if (event.widget instanceof Tree)
        {
            Tree tree = (Tree)event.widget;
            if (tree.getColumn( event.index ) == treeColumn)
            {
                if (event.item instanceof TreeItem)
                {
                    TreeItem treeItem = (TreeItem)event.item;
                    return treeItem;
                }
            }
        }
        return null;
    }

    public abstract Image getImageForViewerNode(TreeNode node);

}
