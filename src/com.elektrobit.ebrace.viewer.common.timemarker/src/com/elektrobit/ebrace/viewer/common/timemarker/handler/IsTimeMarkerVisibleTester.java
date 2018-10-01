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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebrace.viewer.common.util.PropertyTesterUtil;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class IsTimeMarkerVisibleTester extends PropertyTester implements UserInteractionPreferencesListener
{
    final String PROPERTY_NAME = "isTimeMarkerVisible";
    private final String PROPERTY_ID = "com.elektrobit.ebrace.viewer.common.timemarker.isTimeMarkerVisible";
    ServiceRegistration<?> sr = GenericOSGIServiceRegistration
            .registerService( UserInteractionPreferencesListener.class, this );
    private final GenericOSGIServiceTracker<UserInteractionPreferences> userInteractionPreferences = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class );

    public IsTimeMarkerVisibleTester()
    {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (userInteractionPreferences.getService() != null && !userInteractionPreferences.getService().isLiveMode()
                && PROPERTY_NAME.equals( property ))
        {
            if (receiver instanceof List<?>)
            {
                List<?> selected = (List<?>)receiver;
                Iterator<?> iterator = selected.iterator();
                if (iterator.hasNext())
                {
                    Object o = iterator.next();
                    if (o instanceof TimeMarker)
                    {
                        return ((TimeMarker)o).isEnabled();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        refreshPropertyTester();
    }

    private void refreshPropertyTester()
    {
        PropertyTesterUtil.refreshPropertyTesterEvaluation( PROPERTY_ID + "." + PROPERTY_NAME );
    }

}
