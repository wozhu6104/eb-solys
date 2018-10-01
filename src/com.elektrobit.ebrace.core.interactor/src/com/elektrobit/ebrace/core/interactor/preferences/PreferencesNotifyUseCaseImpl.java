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

import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class PreferencesNotifyUseCaseImpl implements PreferencesNotifyUseCase, PreferencesListener
{
    private final PreferencesService preferencesService;
    private PreferencesNotifyCallback callback;

    public PreferencesNotifyUseCaseImpl(PreferencesNotifyCallback _callback, PreferencesService _preferencesService)
    {
        this.callback = _callback;
        this.preferencesService = _preferencesService;
        preferencesService.registerPreferencesListener( this );
        postTimestampFormatToCallback();
    }

    @Override
    public void onTimestampFormatPreferencesChanged()
    {
        postTimestampFormatToCallback();
    }

    private void postTimestampFormatToCallback()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    final String timestampFormat = preferencesService.getTimestampFormatPreferences();
                    callback.onTimestampFormatChanged( timestampFormat );
                }
            }
        } );
    }

    @Override
    public void unregister()
    {
        callback = null;
        preferencesService.unregisterPreferencesListener( this );
    }

    @Override
    public void onColorTransparencyChanged(double value)
    {
    }

    @Override
    public void onColorPaletteChanged(List<SColor> colors)
    {
    }

    @Override
    public void onTableSearchTermsHistoryChanged(List<String> keystosearch, String viewID)
    {
    }

    @Override
    public void onConnectionsChanged(List<ConnectionSettings> connections)
    {
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
