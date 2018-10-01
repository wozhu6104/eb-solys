/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.timemarkers;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.timemarkers.TimeMarkersInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class TimeMarkersInteractionUseCaseImpl implements TimeMarkersInteractionUseCase
{
    private final TimeMarkerManager timeMarkerManager;

    public TimeMarkersInteractionUseCaseImpl(TimeMarkerManager timeMarkerManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "timeMarkerManager", timeMarkerManager );
        this.timeMarkerManager = timeMarkerManager;
    }

    @Override
    public void createTimeMarker(long timestamp)
    {
        timeMarkerManager.createNewTimeMarker( timestamp );
    }

    @Override
    public void removeTimeMarker(TimeMarker timeMarker)
    {
        timeMarkerManager.removeTimeMarker( timeMarker );
    }

    @Override
    public void selectTimeMarker(TimeMarker timeMarker)
    {
        timeMarkerManager.setCurrentSelectedTimeMarker( timeMarker );
    }

    @Override
    public void unregister()
    {
    }
}
