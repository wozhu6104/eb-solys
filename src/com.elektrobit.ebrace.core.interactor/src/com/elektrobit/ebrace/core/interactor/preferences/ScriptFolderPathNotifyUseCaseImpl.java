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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class ScriptFolderPathNotifyUseCaseImpl implements ScriptFolderPathNotifyUseCase, PreferencesListener
{
    private final PreferencesService preferencesService;
    private ScriptFolderPathNotifyCallback callback;

    public ScriptFolderPathNotifyUseCaseImpl(PreferencesService preferencesService,
            ScriptFolderPathNotifyCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "preferencesService", preferencesService );
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        this.preferencesService = preferencesService;
        this.callback = callback;

        preferencesService.registerPreferencesListener( this );

        postCurrentScriptFolderPath();
    }

    private void postCurrentScriptFolderPath()
    {
        final String path = preferencesService.getScriptFolderPath();
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onScriptSourceFolderPathChanged( path );
            }
        } );
    }

    @Override
    public void unregister()
    {
        preferencesService.unregisterPreferencesListener( this );
        callback = null;
    }

    @Override
    public void onScriptFolderPathChanged(String newScriptFolderPath)
    {
        postCurrentScriptFolderPath();
    }

    @Override
    public void onTimestampFormatPreferencesChanged()
    {
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
    public void onTableSearchTermsHistoryChanged(List<String> KeysToSearch, String viewID)
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
}
