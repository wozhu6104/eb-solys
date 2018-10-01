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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class TextWithOutlineRenderer
{
    private static final String THREE_DOTS = "...";
    private static final int WIDTH_PADDING = 2;

    /**
     * Draw a text into desired box, trim it when necessary and add "...". Text is drawn with a outline of different
     * color, to make sure it is visible on every background.
     * 
     * @param font
     * 
     */
    public static void drawTextWithOutlineAndTrim(GC gc, String text, int x, int y, int width, int height,
            boolean isCenteredHeigth, boolean isCenteredWidth, Color fontColor, Color outlineColor, Font font)
    {
        if (width < 1 || text.isEmpty())
        {
            return;
        }

        String stringToDisplay;

        int realX = x;
        int realY = y;

        Point textSize = getTextWidth( gc, text, font );
        if (textSize.x <= width)
        {
            stringToDisplay = text;
        }
        else
        {
            isCenteredWidth = false;
            int lengthToPrint = findMaxLengthThatFits( gc, text, font, width );
            String substringThatFits = text.substring( 0, lengthToPrint );

            stringToDisplay = substringThatFits + THREE_DOTS;
        }

        if (stringToDisplay.isEmpty())
        {
            return;
        }

        if (isCenteredWidth)
        {
            realX += (width - textSize.x) / 2;
        }
        realX += WIDTH_PADDING;
        if (isCenteredHeigth)
        {
            realY += (height - textSize.y) / 2 - 1;
        }
        drawTextWithOutLine( gc, stringToDisplay, realX, realY, fontColor, outlineColor, font );
    }

    private static int findMaxLengthThatFits(GC gc, String text, Font font, int maxWidth)
    {
        int previousLength = 0;
        int printedLength = 1;
        while (printedLength <= text.length())
        {
            Point textSize = getTextWidth( gc, text.substring( 0, printedLength ) + THREE_DOTS, font );
            if (textSize.x > maxWidth)
            {
                printedLength = previousLength;
                break;
            }
            previousLength = printedLength;
            printedLength++;
        }
        return printedLength;
    }

    private static Point getTextWidth(GC gc, String text, Font font)
    {
        Path path = new Path( Display.getCurrent() );
        path.addString( text, 0, 0, font );
        float[] bounds = new float[4];
        path.getBounds( bounds );
        path.dispose();
        return new Point( (int)bounds[2] + WIDTH_PADDING * 2, (int)bounds[3] );
    }

    private static void drawTextWithOutLine(GC gc, String text, int x, int y, Color fontColor, Color outlineColor,
            Font font)
    {
        Path path = new Path( Display.getCurrent() );
        path.addString( text, x, y, font );
        gc.setAntialias( SWT.ON );

        if (outlineColor != null)
        {
            drawOutLine( gc, outlineColor, path );
        }

        drawText( gc, fontColor, path );

        path.dispose();
    }

    private static void drawText(GC gc, Color fontColor, Path path)
    {
        gc.setForeground( fontColor );
        gc.setBackground( fontColor );
        gc.setLineWidth( 1 );
        gc.fillPath( path );
    }

    private static void drawOutLine(GC gc, Color outlineColor, Path path)
    {
        gc.setBackground( outlineColor );
        gc.setForeground( outlineColor );
        gc.setLineWidth( 7 );
        gc.drawPath( path );
    }
}
