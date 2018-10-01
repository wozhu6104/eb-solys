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

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class JumpToTimeMarkerFunction extends BrowserFunction
{

    private final GenericOSGIServiceTracker<TimeMarkerManager> timeMarkerManagerTracker = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class );

    JumpToTimeMarkerFunction(Browser browser, String name)
    {
        super( browser, name );
    }

    @Override
    public Object function(Object[] arguments)
    {
        if (arguments != null && arguments[0] != null)
        {
            TimeMarkerManager timeMarkerManager = timeMarkerManagerTracker.getService();
            TimeMarker marker = timeMarkerManager.getTimeMarkerForTimestamp( Long.parseLong( (String)arguments[0] ) );
            timeMarkerManager.setCurrentSelectedTimeMarker( marker );
        }
        return null;
    }

}
