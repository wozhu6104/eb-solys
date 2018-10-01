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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public final class GraphConnectionColorStore
{
    private final Map<RGB, Color> graphConnectionToColorMap = new HashMap<RGB, Color>();

    public GraphConnectionColorStore()
    {
        final RGB defaultRGB = new RGB( 0, 0, 0 );
        final Color defaultColor = new Color( Display.getCurrent(), defaultRGB );
        graphConnectionToColorMap.put( defaultRGB, defaultColor );
    }

    public final Color convertRGBToColor(final RGB newColorAsRGB)
    {
        if (newColorAsRGB == null)
        {
            throw new IllegalArgumentException( "RGB value must not be null." );
        }

        if (graphConnectionToColorMap.containsKey( newColorAsRGB ))
        {
            return graphConnectionToColorMap.get( newColorAsRGB );
        }
        else
        {
            Color newColor = new Color( Display.getCurrent(), newColorAsRGB );
            graphConnectionToColorMap.put( newColorAsRGB, newColor );
            return newColor;
        }
    }

    public final void disposeAllColors()
    {
        for (RGB nextRGBValue : graphConnectionToColorMap.keySet())
        {
            Color nextColor = graphConnectionToColorMap.get( nextRGBValue );
            if (nextColor != null)
            {
                nextColor.dispose();
            }
        }
    }

    @Override
    public void finalize()
    {
        disposeAllColors();
    }
}
