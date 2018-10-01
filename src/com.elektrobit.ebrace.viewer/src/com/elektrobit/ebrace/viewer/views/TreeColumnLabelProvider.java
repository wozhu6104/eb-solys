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
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elektrobit.ebrace.viewer.ViewerPlugin;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class TreeColumnLabelProvider extends ColumnLabelProvider
{
    private final static Logger LOG = Logger.getLogger( TreeColumnLabelProvider.class );

    private final ImageRegistry imageRegistry;

    public TreeColumnLabelProvider()
    {
        imageRegistry = ViewerPlugin.getPluginInstance().getImageRegistry();
    }

    @Override
    public Image getImage(Object element)
    {
        if (element instanceof Tree)
        {
            return getImage( ((Tree)element).getRootNode() );
        }
        if (element instanceof TreeNode)
        {
            TreeNode node = (TreeNode)element;
            String iconPath = node.getTreeLevel().getIconPath();
            Image image = imageRegistry.get( iconPath );
            if (image == null)
            {
                image = new Image( null, iconPath );
                imageRegistry.put( iconPath, image );
            }
            return image;
        }
        LOG.warn( "No icon found for element: " + element.getClass() );
        return ViewerPlugin.getPluginInstance().getImage( "missing_icon", "gif" );
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof Tree)
        {
            return getText( ((Tree)element).getRootNode() );
        }
        if (element instanceof TreeNode)
        {
            return ((TreeNode)element).getName();
        }
        LOG.warn( "No label found for element: " + element );
        return null;
    }
}
