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

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.preferences.SetColorPreferencesInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class SetColorPreferencesInteractionUseCaseImpl implements SetColorPreferencesInteractionUseCase
{
    private final PreferencesService preferencesService;

    public SetColorPreferencesInteractionUseCaseImpl(PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
    }

    @Override
    public void setColorPreferences(List<SColor> colors)
    {
        preferencesService.setColorPreferences( colors );
    }

    @Override
    public void setColorTranspPreferences(double value)
    {
        preferencesService.setColorTransparencyPreferences( value );
    }

    @Override
    public void unregister()
    {

    }
}
