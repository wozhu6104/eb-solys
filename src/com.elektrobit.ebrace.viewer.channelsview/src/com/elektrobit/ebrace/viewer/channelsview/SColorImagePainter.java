/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class SColorImagePainter
{
    private final ResourceManager resourceManager;
    private static final Map<SColor, Image> colorImages = new HashMap<>();
    public static final int IMAGE_BORDER_LENGTH = 18;
    private static final int COLOR_BORDER_LENGTH = 16;
    private static final int MARGIN = (IMAGE_BORDER_LENGTH - COLOR_BORDER_LENGTH) / 2;
    private static final RGB WHITE = new RGB( 255, 255, 255 );

    public SColorImagePainter(ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    public Image getImageForColor(SColor color)
    {
        if (colorImages.containsKey( color ))
        {
            return colorImages.get( color );
        }
        else
        {
            Image image = createImageForColor( color );
            colorImages.put( color, image );
            return image;
        }
    }

    private Image createImageForColor(SColor color)
    {
        Image image = new Image( Display.getCurrent(), IMAGE_BORDER_LENGTH, IMAGE_BORDER_LENGTH );
        GC gc = new GC( image );
        drawSquare( gc, WHITE, 0, IMAGE_BORDER_LENGTH );
        drawSquare( gc, new RGB( color.getRed(), color.getGreen(), color.getBlue() ), MARGIN, COLOR_BORDER_LENGTH );
        gc.dispose();
        return image;
    }

    private void drawSquare(GC gc, RGB color, int margin, int width)
    {
        gc.setBackground( resourceManager.createColor( color ) );
        gc.fillRectangle( margin, margin, width, width );
    }
}
