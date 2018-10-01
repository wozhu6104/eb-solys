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
import org.eclipse.swt.widgets.TreeColumn;

import com.elektrobit.ebrace.viewer.ViewerPlugin;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;

public class CheckboxColumnImagePainter extends AbstractTreeColumnImagePainter
{
    private final static Logger LOG = Logger.getLogger( CheckboxColumnImagePainter.class );
    private TreeNodesCheckState nodesCheckState;

    public CheckboxColumnImagePainter(TreeColumn treeColumn)
    {
        super( treeColumn );
    }

    public void setTreeCheckStates(TreeNodesCheckState nodesCheckState)
    {
        this.nodesCheckState = nodesCheckState;
    }

    @Override
    public Image getImageForViewerNode(TreeNode node)
    {
        if (nodesCheckState == null)
        {
            return getDefaultImage( node );
        }

        if (nodesCheckState.getNodeCheckState( node ) == CHECKED_STATE.CHECKED)
        {
            return ViewerPlugin.getPluginInstance().getImage( "selection_full", "png" );
        }

        if (nodesCheckState.getNodeCheckState( node ) == CHECKED_STATE.PARTIALLY_CHECKED)
        {
            return ViewerPlugin.getPluginInstance().getImage( "selection_partial", "png" );
        }

        if (nodesCheckState.getNodeCheckState( node ) == CHECKED_STATE.UNCHECKED)
        {
            return ViewerPlugin.getPluginInstance().getImage( "selection_none", "png" );
        }

        return getDefaultImage( node );
    }

    private Image getDefaultImage(TreeNode node)
    {
        Image image;
        image = ViewerPlugin.getPluginInstance().getImage( "missing_icon", "gif" );
        LOG.warn( "No icon found for node " + node );
        return image;
    }

}
