/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class PropertyElement
{
    private final String m_column;
    private final String m_value;
    private Color m_backgroundColor;
    private Color m_foregroundColor;
    private Font m_font;

    public PropertyElement(String column, String value)
    {
        this( column,
                value,
                new Color( Display.getDefault(), 255, 255, 255 ),
                new Color( Display.getDefault(), 0, 0, 0 ),
                Display.getDefault().getSystemFont() );
    }

    public PropertyElement(String column, String value, Color backgroundColor, Color foregroundColor, Font font)
    {
        m_column = column;
        m_value = value;
        m_backgroundColor = backgroundColor;
        m_foregroundColor = foregroundColor;
        m_font = font;
    }

    public Font getFont()
    {
        return m_font;
    }

    public void setFont(Font font)
    {
        m_font = font;
    }

    public String getColumn()
    {
        return m_column;
    }

    public String getValue()
    {
        return m_value;
    }

    public Color getForegroundColor()
    {
        return m_foregroundColor;
    }

    public void setForegroundColor(Color color)
    {
        m_foregroundColor = color;
    }

    public Color getBackgroundColor()
    {
        return m_backgroundColor;
    }

    public void setBackgroundColor(Color color)
    {
        m_backgroundColor = color;
    }
}
