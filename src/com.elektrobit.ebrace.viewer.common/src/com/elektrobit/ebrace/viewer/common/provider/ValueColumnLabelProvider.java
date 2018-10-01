/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class ValueColumnLabelProvider extends ColumnLabelProvider implements RowFormatter
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();
    private final Font font = new Font( null, new FontData( "Arial", 9, SWT.BOLD | SWT.ITALIC ) );

    @Override
    public String getText(Object element)
    {
        if (element instanceof RuntimeEvent<?>)
        {
            return ((RuntimeEvent<?>)element).getSummary();
        }
        else if (element instanceof TimeMarker)
        {
            return ((TimeMarker)element).getName();
        }
        return null;
    }

    @Override
    public Font getFont(Object element)
    {
        if (element instanceof TimeMarker)
        {
            if (element.equals( timeMarkerManager.getCurrentSelectedTimeMarker() ))
            {
                return font;
            }
        }
        return super.getFont( element );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        font.dispose();
    }

    @Override
    public Color getBackground(Object element)
    {
        if (element instanceof TimeMarker)
        {
            return ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
        }
        return super.getBackground( element );
    }
}
