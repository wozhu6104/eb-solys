/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.listener;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimespanUtil;
import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimeMarkerChangeTimeSpanMouseListener extends TimeMarkerMouseListener
        implements
            MouseTrackListener,
            MouseMoveListener
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();
    protected final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();

    public TimeMarkerChangeTimeSpanMouseListener(Control parent, int parentControlOrientation, boolean isLiveMode)
    {
        super( parent, parentControlOrientation );
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
        if (isClickInsideChartArea( e ))
        {
            if ((e.button == 1))
            {
                TimeMarker line = timeMarkerManager.getCurrentSelectedTimeMarker();
                changeTimespan( e, line );
            }
        }
        super.mouseUp( e );
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
        super.mouseDown( e );
    }

    @Override
    public void mouseMove(MouseEvent e)
    {
    }

    private void changeTimespan(MouseEvent e, TimeMarker line)
    {
        int clicked = getCoordXOrYMousePositionForOrientation( e );
        long timestamp = calculateClickedTimestamp( line, clicked );
        TimespanUtil.changeTimespanIfNeeded( timestamp, ANALYSIS_TIMESPAN_CHANGE_REASON.USER_SHIFT );
    }

    private long calculateClickedTimestamp(TimeMarker line, int clickedPosition)
    {
        long result;
        if (line != null)
        {
            result = line.getTimestamp();
        }
        else
        {
            result = (long)TimestampPositionInChartConverter
                    .calculateTimestampForClick( xOffset,
                                                 plotWidth,
                                                 clickedPosition,
                                                 minTimeAxis,
                                                 maxTimeAxis - minTimeAxis );
        }
        return result;
    }
}
