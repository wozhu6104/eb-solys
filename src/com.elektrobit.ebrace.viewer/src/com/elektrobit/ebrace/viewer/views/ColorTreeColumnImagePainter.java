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

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ColorTreeColumnImagePainter extends AbstractTreeColumnImagePainter
{
    private final HashMap<Color, Image> cachedImages = new HashMap<Color, Image>();
    private final LocalResourceManager localResourceManager;

    public ColorTreeColumnImagePainter(TreeColumn treeColumn)
    {
        super( treeColumn );
        localResourceManager = new LocalResourceManager( JFaceResources.getResources() );
    }

    @Override
    public Image getImageForViewerNode(TreeNode node)
    {
        return null;
        // return getColorImage( node.getColor(), 16, 16 );
    }

    @SuppressWarnings("unused")
    private Image getColorImage(Color color, int width, int height)
    {
        if (cachedImages.containsKey( color ))
        {
            return cachedImages.get( color );
        }
        else
        {
            Image image = new Image( Display.getCurrent(), width, height );
            localResourceManager.createImage( ImageDescriptor.createFromImage( image ) );
            GC gc = new GC( image );
            gc.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_BLACK ) );
            gc.drawRectangle( 0, 0, width - 1, height - 1 );
            gc.setBackground( color );
            gc.fillRectangle( 1, 1, width - 2, height - 2 );
            gc.dispose();
            storeImage( color, image );

            return image;
        }
    }

    private void storeImage(Color color, Image newImage)
    {
        if (!cachedImages.containsKey( color ))
        {
            cachedImages.put( color, newImage );
        }
    }

}
