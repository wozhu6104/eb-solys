/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.preferences;

import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.LiveModeNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;

public class LiveModeNotifyUseCaseImpl implements LiveModeNotifyUseCase, UserInteractionPreferencesListener
{
    private final UserInteractionPreferences userInteractionPreferences;
    private ServiceRegistration<?> serviceRegistration;
    private LiveModeNotifyCallback callback;

    public LiveModeNotifyUseCaseImpl(LiveModeNotifyCallback callback,
            UserInteractionPreferences userInteractionPreferences)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "userInteractionPreferences", userInteractionPreferences );
        this.callback = callback;
        this.userInteractionPreferences = userInteractionPreferences;

        postInitialValue();
    }

    private void postInitialValue()
    {
        boolean isLiveMode = userInteractionPreferences.isLiveMode();
        UIExecutor.post( () -> callback.onIsLiveModeChanged( isLiveMode ) );
    }

    @Override
    public void unregister()
    {
        serviceRegistration.unregister();
        callback = null;
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onIsLiveModeChanged( isLiveMode );
            }
        } );
    }

    public void setServiceRegistration(ServiceRegistration<?> serviceRegistration)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "serviceRegistration", serviceRegistration );
        this.serviceRegistration = serviceRegistration;
    }
}
