/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.channelColor;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class ColorPreferencesNotifyUseCaseImpl implements ColorPreferencesNotifyUseCase, PreferencesListener
{
    private ColorPreferencesNotifyCallback callback;
    private final PreferencesService preferencesService;

    public ColorPreferencesNotifyUseCaseImpl(ColorPreferencesNotifyCallback _callback,
            PreferencesService _preferencesService)
    {
        this.callback = _callback;
        this.preferencesService = _preferencesService;
        preferencesService.registerPreferencesListener( this );
        postTransparencyValueToCallback();
        postNewColorPaletteToCallback();
    }

    private void postTransparencyValueToCallback()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    double transparencyValue = preferencesService.getColorTransparencyValue();
                    callback.onColorTransparencyChanged( transparencyValue );
                }
            }
        } );
    }

    private void postNewColorPaletteToCallback()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    List<SColor> raceColors = preferencesService.getColorPreferences();
                    callback.onColorPaletteChanged( raceColors );
                }
            }
        } );
    }

    @Override
    public void onTimestampFormatPreferencesChanged()
    {

    }

    @Override
    public void onColorTransparencyChanged(double value)
    {
        postTransparencyValueToCallback();
    }

    @Override
    public void onColorPaletteChanged(List<SColor> colors)
    {
        postNewColorPaletteToCallback();
    }

    @Override
    public void onTableSearchTermsHistoryChanged(List<String> keystosearch, String viewID)
    {
    };

    @Override
    public void onConnectionsChanged(List<ConnectionSettings> connections)
    {
    }

    @Override
    public void unregister()
    {
        callback = null;
        preferencesService.unregisterPreferencesListener( this );
    }

    @Override
    public void onLineChartModelSettingsChanged(LineChartModelSettings lineChartModelSettings)
    {
    }

    @Override
    public void onScriptFolderPathChanged(String newScriptFolderPath)
    {
    }
}
