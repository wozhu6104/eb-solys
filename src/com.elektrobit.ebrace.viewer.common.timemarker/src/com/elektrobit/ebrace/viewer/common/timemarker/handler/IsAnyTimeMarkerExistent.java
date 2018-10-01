/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.handler;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class IsAnyTimeMarkerExistent extends PropertyTester implements TimeMarkersChangedListener
{
    private static final String CLEAR_TIMELINES_IS_ANY_TIMELINE_EXISTENT_PROPERTY = "com.elektrobit.ebrace.viewer.common.clearTimeMarkers.isAnyTimelineExistent";
    private final String PROPERTY_NAME = "isAnyTimelineExistent";
    private final GenericOSGIServiceTracker<TimeMarkerManager> timeMarkerManagerServiceTracker = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class );

    public IsAnyTimeMarkerExistent()
    {
        timeMarkerManagerServiceTracker.getService().registerListener( this );
    }

    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue)
    {
        final TimeMarkerManager timeMarkerManager = timeMarkerManagerServiceTracker.getService();

        if (timeMarkerManager != null && PROPERTY_NAME.equals( property )
                && !timeMarkerManager.getAllTimeMarkers().isEmpty())
        {
            return true;
        }
        return false;
    }

    private void reevaluate()
    {
        IEvaluationService evaluationService = getEvaluationService();
        evaluationService.requestEvaluation( CLEAR_TIMELINES_IS_ANY_TIMELINE_EXISTENT_PROPERTY );
    }

    private IEvaluationService getEvaluationService()
    {
        return PlatformUI.getWorkbench().getService( IEvaluationService.class );

    }

    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
        reevaluate();
    }

    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
        reevaluate();
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerSelected(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerNameChanged(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
    }

    @Override
    public void allTimeMarkersRemoved()
    {
        reevaluate();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
    }
}
