/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.userinteraction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;

public class ChartState extends AbstractSourceProvider implements UserInteractionPreferencesListener
{
    private final static String CHART_STATE_ID = "com.elektrobit.ebrace.ui.ecl.preferences.running.state";
    private final static String CHART_RUNNING = "CHART_RUNNING";
    private final static String CHART_PAUSING = "CHART_PAUSING";

    enum State {
        CHART_RUNNING, CHART_PAUSING
    };

    private final GenericOSGIServiceTracker<UserInteractionPreferences> userInteractionPreferencesTracker = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class );

    public ChartState()
    {
        registerThisAsUserInteractionPreferencesListener();
    }

    private void registerThisAsUserInteractionPreferencesListener()
    {
        GenericOSGIServiceRegistration.registerService( UserInteractionPreferencesListener.class, this );
    }

    @Override
    public String[] getProvidedSourceNames()
    {
        return new String[]{CHART_STATE_ID};
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map getCurrentState()
    {
        Map map = new HashMap( 1 );
        boolean isRunning = userInteractionPreferencesTracker.getService().isLiveMode();

        if (isRunning)
        {
            map.put( CHART_STATE_ID, CHART_RUNNING );
        }
        else
        {
            map.put( CHART_STATE_ID, CHART_PAUSING );
        }
        return map;
    }

    private void firePlaying()
    {
        fireSourceChanged( ISources.WORKBENCH, CHART_STATE_ID, CHART_RUNNING );
    }

    private void firePaused()
    {
        fireSourceChanged( ISources.WORKBENCH, CHART_STATE_ID, CHART_PAUSING );
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void onIsLiveModeChanged(final boolean isLiveMode)
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                if (isLiveMode)
                {
                    firePlaying();
                }
                else
                {
                    firePaused();
                }
            }
        } );
    }
}
