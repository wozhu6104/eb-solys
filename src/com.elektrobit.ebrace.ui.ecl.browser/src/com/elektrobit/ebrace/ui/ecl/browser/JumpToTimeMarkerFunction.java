/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.browser;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class JumpToTimeMarkerFunction
{

    private final GenericOSGIServiceTracker<TimeMarkerManager> timeMarkerManagerTracker = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class );

    public void jump(long timestamp)
    {
        TimeMarkerManager timeMarkerManager = timeMarkerManagerTracker.getService();
        TimeMarker marker = timeMarkerManager.getTimeMarkerForTimestamp( timestamp );
        timeMarkerManager.setCurrentSelectedTimeMarker( marker );
    }

}
