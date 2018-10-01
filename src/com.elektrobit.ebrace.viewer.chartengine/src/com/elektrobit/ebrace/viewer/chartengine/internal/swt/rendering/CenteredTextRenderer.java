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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class CenteredTextRenderer
{
    private static final String THREE_DOTS = "...";

    /**
     * Draw a text into desired box, trim it when necessary and add "..."
     * 
     */
    public static void drawTextAndTrim(GC gc, String text, int x, int y, int width, int height,
            boolean isCenteredHeight, boolean isCenteredWidth, boolean transparentBackground)
    {
        if (width < 1 || text.isEmpty())
        {
            return;
        }

        String stringToDisplay;

        int realX = x;
        int realY = y;

        Point textSize = getTextSize( gc, text );
        if (textSize.x <= width)
        {
            stringToDisplay = text;
        }
        else
        {
            isCenteredWidth = false;
            int lengthToPrint = findMaxLengthThatFits( gc, text, gc.getFont(), width );
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
        if (isCenteredHeight)
        {
            realY += (height - textSize.y) / 2 - 1;
        }

        gc.drawText( stringToDisplay, realX, realY, transparentBackground );
    }

    private static int findMaxLengthThatFits(GC gc, String text, Font font, int maxWidth)
    {
        int previousLength = 0;
        int printedLength = 1;
        while (printedLength <= text.length())
        {
            Point textSize = getTextSize( gc, text.substring( 0, printedLength ) + THREE_DOTS );
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

    private static Point getTextSize(GC gc, String text)
    {
        Point textExtent = gc.textExtent( text );
        return textExtent;
    }
}
