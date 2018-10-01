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

import java.text.SimpleDateFormat;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetPreferencesInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.SetTimestampPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

public class SetTimestampPreferencesInteractionUseCaseImpl implements SetTimestampPreferencesInteractionUseCase
{
    private SetPreferencesInteractionCallback callback;
    private final PreferencesService preferencesService;

    public SetTimestampPreferencesInteractionUseCaseImpl(SetPreferencesInteractionCallback callback,
            PreferencesService preferencesService)
    {
        this.callback = callback;
        this.preferencesService = preferencesService;
    }

    @Override
    public void setTimestampFormatPreferences(String timestampFormatPreferences)
    {
        if (isTimestampFormatValid( timestampFormatPreferences ))
        {
            preferencesService.setTimestampFormatPreferences( timestampFormatPreferences );
        }
        else
            callback.onCustomFormatNotValid();
    }

    @Override
    public boolean isTimestampFormatValid(String pattern)
    {
        if (pattern.isEmpty())
        {
            return false;
        }

        if (pattern.equalsIgnoreCase( TimeFormatter.TIMESTAMP_MICROSECONDS )
                || pattern.equalsIgnoreCase( TimeFormatter.TIMESTAMP_MILLISECONDS ))
        {
            return true;
        }

        try
        {
            new SimpleDateFormat( pattern );
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    @Override
    public String getTimestampFormatPreferences()
    {
        return preferencesService.getTimestampFormatPreferences();
    }

    @Override
    public void unregister()
    {
        callback = null;
    }
}
