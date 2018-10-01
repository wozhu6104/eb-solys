/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.graph;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

import com.elektrobit.ebrace.viewer.ViewerPlugin;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class GraphLabelProvider extends LabelProvider
        implements
            IEntityConnectionStyleProvider,
            ISelfStyleProvider,
            IEntityStyleProvider
{
    private final static Logger LOG = Logger.getLogger( GraphLabelProvider.class );

    private static final RGB DEFAULT_CONNECTION_COLOR = new RGB( 71, 71, 71 );
    private static final RGB BLACK_COLOR = new RGB( 0, 0, 0 );

    private final LocalResourceManager localResourceManager = new LocalResourceManager( JFaceResources.getResources() );
    private final ImageRegistry imageRegistry;
    private Color blackColor = null;

    public GraphLabelProvider()
    {
        super();
        imageRegistry = ViewerPlugin.getPluginInstance().getImageRegistry();
    }

    private Color getBlackColor()
    {
        if (blackColor == null)
        {
            blackColor = localResourceManager.createColor( BLACK_COLOR );
        }
        return blackColor;
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof TreeNode)
        {
            TreeNode node = (TreeNode)element;
            return node.getName();
        }
        return "";
    }

    @Override
    public Image getImage(Object element)
    {
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
        else if (element instanceof EntityConnectionData)
        {
            return null;
        }
        LOG.warn( "No icon found for element: " + element );
        return ViewerPlugin.getPluginInstance().getImage( "missing_icon", "gif" );
    }

    @Override
    public void dispose()
    {
        localResourceManager.dispose();
        super.dispose();
    }

    @Override
    public int getConnectionStyle(Object src, Object dest)
    {
        return ZestStyles.CONNECTIONS_DIRECTED;
    }

    @Override
    public Color getColor(Object src, Object dest)
    {
        return null;
    }

    @Override
    public Color getHighlightColor(Object src, Object dest)
    {
        return null;
    }

    @Override
    public int getLineWidth(Object src, Object dest)
    {
        if ((src instanceof TreeNode) && (dest instanceof TreeNode))
        {
            return 1;
        }

        return 0;
    }

    @Override
    public IFigure getTooltip(Object entity)
    {
        return null;
    }

    @Override
    public void selfStyleConnection(Object element, GraphConnection connection)
    {
        connection.setCurveDepth( 20 );
        setColorOfConnection( connection );
    }

    private void setColorOfConnection(GraphConnection connection)
    {
        RGB colorOfComRelationAsRGB = DEFAULT_CONNECTION_COLOR;
        connection.setLineColor( localResourceManager.createColor( colorOfComRelationAsRGB ) );
    }

    @Override
    public void selfStyleNode(Object element, GraphNode node)
    {
    }

    @Override
    public Color getNodeHighlightColor(Object entity)
    {

        if (entity instanceof TreeNode)
        {
            return Display.getCurrent().getSystemColor( SWT.COLOR_WHITE );
        }
        return null;
    }

    @Override
    public Color getBorderColor(Object entity)
    {
        if (entity instanceof TreeNode)
        {
            return getBlackColor();
        }
        return null;
    }

    @Override
    public Color getBorderHighlightColor(Object entity)
    {
        if (entity instanceof TreeNode)
        {
            return getBlackColor();
        }
        return null;
    }

    @Override
    public int getBorderWidth(Object entity)
    {
        if (entity instanceof TreeNode)
        {
            return 1;
        }
        return 0;
    }

    @Override
    public Color getBackgroundColour(Object entity)
    {
        if (entity instanceof TreeNode)
        {
            return Display.getCurrent().getSystemColor( SWT.COLOR_GRAY );
        }
        return null;
    }

    @Override
    public Color getForegroundColour(Object entity)
    {
        if (entity instanceof TreeNode)
        {
            return Display.getCurrent().getSystemColor( SWT.COLOR_BLACK );
        }
        return null;
    }

    @Override
    public boolean fisheyeNode(Object entity)
    {
        return false;
    }
}
