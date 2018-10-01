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

import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class FigureHelper
{

    public static void renderFigureToImage(Image image, XYGraph figure, List<Trace> traces)
    {
        GC gc = new GC( image );

        SWTGraphics swtGraphics = new SWTGraphics( gc );
        Graphics graphics = swtGraphics;

        final int x = image.getBounds().x;
        final int y = image.getBounds().y;
        final int width = image.getBounds().width;
        final int height = image.getBounds().height;
        figure.setBounds( new org.eclipse.draw2d.geometry.Rectangle( x, y, width, height ) );

        for (Trace trace : traces)
        {
            trace.paint( graphics );
        }

        gc.dispose();
        graphics.dispose();

    }

    /**
     * Return a copy of image without given margin. Images must be dispose from caller.
     * 
     * @param image
     *            Original image with margin.
     * @param marginX
     * @param marginY
     * @return Returns new image without margin.
     */
    public static Image cropMarginFromImage(Image image, int marginX, int marginY)
    {
        ImageData data = image.getImageData();
        final int newWidth = data.width - (marginX * 2 - 1);
        final int newHeight = data.height - (marginY * 2 - 1);

        Image newImage = new Image( image.getDevice(), newWidth, newHeight );
        GC gc = new GC( newImage );
        gc.drawImage( image, data.x + marginX, data.y + marginY, newWidth, newHeight, 0, 0, newWidth, newHeight );
        gc.dispose();

        return newImage;
    }

    public static void saveImageToFile(String path, Image image)
    {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[]{image.getImageData()};
        imageLoader.save( path, SWT.IMAGE_PNG );
    }
}
