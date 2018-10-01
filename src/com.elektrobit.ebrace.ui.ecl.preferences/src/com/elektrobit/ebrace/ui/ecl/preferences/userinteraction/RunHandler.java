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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;

public class RunHandler extends AbstractHandler
{
    private final GenericOSGIServiceTracker<UserInteractionPreferences> userInteractionPreferencesTracker = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        userInteractionPreferencesTracker.getService().setIsLiveMode( true );
        return null;
    }
}
