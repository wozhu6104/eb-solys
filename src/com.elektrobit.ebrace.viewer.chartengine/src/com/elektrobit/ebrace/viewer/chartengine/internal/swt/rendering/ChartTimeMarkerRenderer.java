/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.rendering;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class ChartTimeMarkerRenderer
{
    private static final int TIMEMARKER_ICON_TO_LINE_MARGIN = 3;
    private static final int TIMEMARKER_LINE_WIDTH = 2;
    private static final String FONT_NAME = "Arial";
    private static final int FONT_SIZE = 8;
    private static final int TIMEMARKER_TEXT_BOX_HEIGHT = 15;
    private static final int TIMEMARKER_TEXT_BOX_WIDTH = 140;
    private static final int TILTED_TIMEMARKER_TEXT_BOX_WIDTH = 92;
    private static final int TIMEMARKER_BOX_TO_LINE_MARGIN = 10;
    private static final int TIMEMARKER_BOX_ARROW_WIDTH_HALF = 5;
    private static final int TIMEMARKER_BOX_ARROW_HEIGHT = 5;
    private static final RGB WHITE_COLOR_RGB = new RGB( 255, 255, 255 );
    private static final RGB BLACK_COLOR_RGB = new RGB( 0, 0, 0 );

    public static void drawTimeMarkerHorizontalText(GC gc, Point topPoint, Point bottomPoint, TimeMarker timeMarker,
            boolean timeMarkerSelected, ResourceManager resManager)
    {
        SColor colorAsRaceColor = timeMarker.getColor();
        Color timeMarkerColor = resManager
                .createColor( new RGB( colorAsRaceColor.getRed(),
                                       colorAsRaceColor.getGreen(),
                                       colorAsRaceColor.getBlue() ) );
        Color blackColor = resManager.createColor( BLACK_COLOR_RGB );

        gc.setLineStyle( timeMarkerSelected ? SWT.LINE_SOLID : SWT.LINE_DASH );
        gc.setForeground( timeMarkerColor );
        gc.setLineWidth( TIMEMARKER_LINE_WIDTH );
        gc.drawLine( topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y );
        String timeMarkerName = timeMarker.getName();

        int textBoxX = topPoint.x - TIMEMARKER_TEXT_BOX_WIDTH / 2;
        textBoxX = textBoxX < 0 ? 0 : textBoxX;
        int textBoxY = topPoint.y - TIMEMARKER_TEXT_BOX_HEIGHT - TIMEMARKER_BOX_TO_LINE_MARGIN;
        Rectangle timeMarkerTextBox = new Rectangle( textBoxX,
                                                     textBoxY,
                                                     TIMEMARKER_TEXT_BOX_WIDTH,
                                                     TIMEMARKER_TEXT_BOX_HEIGHT );
        if (timeMarkerSelected)
        {
            drawHorizontaActivelTimeMarkerTextBox( gc, timeMarkerColor, timeMarkerTextBox, topPoint, true );
            drawHorizontalActiveTimeMarkerText( gc, timeMarkerSelected, blackColor, timeMarkerName, timeMarkerTextBox );
        }
        else
        {
            drawTimeMarkerIcon( gc, topPoint );
        }
    }

    private static void drawHorizontaActivelTimeMarkerTextBox(GC gc, Color timeMarkerColor, Rectangle box,
            Point topTimeMarkerPoint, boolean drawTip)
    {
        gc.setBackground( timeMarkerColor );
        gc.fillRectangle( box );

        //@formatter:off
        //
        // topCenterPoint - X
        // --------X-------
        //     \      /
        //      \    /
        //       \  /
        //        \/
        //
        //@formatter:on

        if (drawTip)
        {
            Point topCenterPoint = new Point( topTimeMarkerPoint.x, box.y + box.height );
            Point topLeftCorner = new Point( topCenterPoint.x - TIMEMARKER_BOX_ARROW_WIDTH_HALF, topCenterPoint.y );
            Point topRightCorner = new Point( topCenterPoint.x + TIMEMARKER_BOX_ARROW_WIDTH_HALF, topCenterPoint.y );
            Point bottomCorner = new Point( topCenterPoint.x, topCenterPoint.y + TIMEMARKER_BOX_ARROW_HEIGHT );

            gc.fillPolygon( new int[]{topLeftCorner.x, topLeftCorner.y, topRightCorner.x, topRightCorner.y,
                    bottomCorner.x, bottomCorner.y} );
        }
    }

    private static void drawHorizontalActiveTimeMarkerText(GC gc, boolean timeMarkerSelected, Color blackColor,
            String timeMarkerName, Rectangle timeMarkerTextBox)
    {
        Font font = new Font( Display.getCurrent(),
                              new FontData( FONT_NAME, FONT_SIZE, timeMarkerSelected ? SWT.BOLD : SWT.NORMAL ) );
        gc.setForeground( blackColor );
        gc.setFont( font );
        CenteredTextRenderer.drawTextAndTrim( gc,
                                              timeMarkerName,
                                              timeMarkerTextBox.x,
                                              timeMarkerTextBox.y,
                                              timeMarkerTextBox.width,
                                              timeMarkerTextBox.height,
                                              true,
                                              true,
                                              true );
        font.dispose();
    }

    private static void drawTimeMarkerIcon(GC gc, Point topPoint)
    {
        Image image = ViewerCommonPlugin.getDefault().getImage( "timemarker_green", "png" );
        gc.drawImage( image,
                      topPoint.x - image.getBounds().width / 2,
                      topPoint.y - image.getBounds().height - TIMEMARKER_ICON_TO_LINE_MARGIN );

    }

    public static void drawTimeMarkerTiltedText(GC gc, Point topPoint, Point bottomPoint, TimeMarker timeMarker,
            boolean timeMarkerSelected, ResourceManager resManager)
    {

        SColor colorAsRaceColor = timeMarker.getColor();
        Color timeMarkerColor = resManager
                .createColor( new RGB( colorAsRaceColor.getRed(),
                                       colorAsRaceColor.getGreen(),
                                       colorAsRaceColor.getBlue() ) );
        Color whiteColor = resManager.createColor( WHITE_COLOR_RGB );
        Color blackColor = resManager.createColor( BLACK_COLOR_RGB );

        gc.setLineStyle( timeMarkerSelected ? SWT.LINE_SOLID : SWT.LINE_DASH );

        gc.setForeground( timeMarkerColor );
        gc.setLineWidth( TIMEMARKER_LINE_WIDTH );
        gc.drawLine( topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y );
        String timeMarkerName = timeMarker.getName();

        int textBoxY = -TIMEMARKER_TEXT_BOX_HEIGHT;
        Rectangle timeMarkerTextBox = new Rectangle( 0,
                                                     textBoxY,
                                                     TILTED_TIMEMARKER_TEXT_BOX_WIDTH + 10,
                                                     TIMEMARKER_TEXT_BOX_HEIGHT );

        Transform transformationToTM = new Transform( Display.getCurrent() );
        Transform originalTransformation = new Transform( Display.getCurrent() );
        setTransformationToTimeMarkerStart( topPoint, bottomPoint, transformationToTM );

        gc.getTransform( originalTransformation );
        gc.setTransform( transformationToTM );

        if (timeMarkerSelected)
        {
            drawHorizontaActivelTimeMarkerTextBox( gc, timeMarkerColor, timeMarkerTextBox, topPoint, false );
        }
        else
        {
            drawWhiteBackground( gc, whiteColor, timeMarkerTextBox );
        }
        Font font = new Font( Display.getCurrent(),
                              new FontData( FONT_NAME, FONT_SIZE, timeMarkerSelected ? SWT.BOLD : SWT.NORMAL ) );
        gc.setForeground( blackColor );
        gc.setFont( font );
        boolean textHorizontallyCentered = timeMarkerSelected ? true : false;

        TextWithOutlineRenderer.drawTextWithOutlineAndTrim( gc,
                                                            timeMarkerName,
                                                            0,
                                                            -timeMarkerTextBox.height,
                                                            timeMarkerTextBox.width,
                                                            timeMarkerTextBox.height,
                                                            true,
                                                            textHorizontallyCentered,
                                                            blackColor,
                                                            null,
                                                            font );
        font.dispose();
        gc.setTransform( originalTransformation );
        transformationToTM.dispose();
        originalTransformation.dispose();
    }

    private static void drawWhiteBackground(GC gc, Color whiteColor, Rectangle timeMarkerTextBox)
    {
        gc.setBackground( whiteColor );
        gc.fillRectangle( timeMarkerTextBox );
    }

    private static void setTransformationToTimeMarkerStart(Point startPoint, Point endPoint, Transform transformation)
    {
        transformation.translate( startPoint.x, startPoint.y /* + TIMEMARKER_TEXT_BOX_HEIGHT */ );
        transformation.rotate( -20 );
    }
}
