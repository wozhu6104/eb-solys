/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.impl;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;

@Component
public class UserInteractionPreferencesImpl implements UserInteractionPreferences, ResetListener
{
    private boolean isLiveMode = true;

    public UserInteractionPreferencesImpl()
    {
    }

    @Override
    public boolean isLiveMode()
    {
        return isLiveMode;
    }

    @Override
    public void setIsLiveMode(boolean isLiveMode)
    {
        this.isLiveMode = isLiveMode;
        notifyRunningStateChangedListeners( isLiveMode );
    }

    private void notifyRunningStateChangedListeners(final boolean isRunning)
    {
        new OSGIWhiteBoardPatternCaller<UserInteractionPreferencesListener>( UserInteractionPreferencesListener.class )
                .callOSGIService( new OSGIWhiteBoardPatternCommand<UserInteractionPreferencesListener>()
                {
                    @Override
                    public void callOSGIService(UserInteractionPreferencesListener listener)
                    {
                        listener.onIsLiveModeChanged( isRunning );
                    }
                } );
    }

    @Override
    public void onReset()
    {
        setIsLiveMode( true );
    }
}
