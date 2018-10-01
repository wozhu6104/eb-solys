/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener;

import org.eclipse.swt.events.MouseEvent;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.TableRuler;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TableRulerTimemarkerMouseListener extends TableRulerMouseListener
{
    private final TableRuler ruler;
    private TimeMarker timeMarkerUnderCursor = null;

    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();

    public TableRulerTimemarkerMouseListener(TableRuler ruler)
    {
        this.ruler = ruler;
    }

    @Override
    public void mouseExit(MouseEvent e)
    {
        ruler.setToolTipText( null );
    }

    @Override
    public void mouseHover(MouseEvent e)
    {
        timeMarkerUnderCursor = (TimeMarker)findNearbyTimeStamp( e.y );
        if (timeMarkerUnderCursor != null)
        {
            ruler.setToolTipText( timeMarkerUnderCursor.getName() );
        }
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        timeMarkerUnderCursor = (TimeMarker)findNearbyTimeStamp( e.y );
        if (timeMarkerUnderCursor != null)
        {
            timeMarkerManager.setCurrentSelectedTimeMarker( timeMarkerUnderCursor );
        }
    }

}
