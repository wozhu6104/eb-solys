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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebrace.viewer.common.util.ColorPreferences;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimeMarkerLabelProvider extends ColumnLabelProvider implements RowFormatter
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();

    @Override
    public String getText(Object element)
    {
        if (element instanceof TimeMarker)
        {
            return ((TimeMarker)element).getName();
        }
        return null;
    }

    @Override
    public Image getImage(Object element)
    {
        if (element instanceof TimeMarker)
        {
            if (((TimeMarker)element).isEnabled())
            {
                return ViewerCommonPlugin.getDefault().getImage( "timemarker", "png" );
            }
            return ViewerCommonPlugin.getDefault().getImage( "timemarker_disabled", "png" );
        }
        return null;
    }

    @Override
    public Color getBackground(Object element)
    {
        if (element instanceof TimeMarker)
        {
            if (element.equals( timeMarkerManager.getCurrentSelectedTimeMarker() ))
            {
                return ColorPreferences.TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR;
            }
        }
        return super.getBackground( element );
    }
}
