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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ColorColumnCellEditor extends CellEditor
{
    private Color m_color;

    public ColorColumnCellEditor()
    {
    }

    @Override
    protected void doSetValue(Object value)
    {

        if (value instanceof Color)
        {
            m_color = (Color)value;
        }
        else
        {
            System.out.println( "ColorColumnEditingSupport: Object has unknown type." );
        }
    }

    @Override
    protected void doSetFocus()
    {
    }

    @Override
    protected Object doGetValue()
    {
        return m_color;
    }

    @Override
    protected Control createControl(Composite parent)
    {
        return parent;
    }

    @Override
    public void activate()
    {
        fireApplyEditorValue();
    }

}
