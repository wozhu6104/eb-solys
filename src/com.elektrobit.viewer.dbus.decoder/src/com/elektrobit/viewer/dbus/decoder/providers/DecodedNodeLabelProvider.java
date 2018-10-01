/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.viewer.dbus.decoder.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;

public class DecodedNodeLabelProvider extends ColumnLabelProvider
{
    @Override
    public Color getBackground(Object element)
    {
        if (element instanceof DecodedNode)
        {
            DecodedNode node = (DecodedNode)element;
            Display display = Display.getDefault();
            if (FindIndexUtil.isFirst( node ) == 0)
            {
                return display.getSystemColor( SWT.COLOR_TITLE_BACKGROUND_GRADIENT );

            }
            else
            {
                return display.getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW );
            }
        }
        return super.getBackground( element );
    }
}
