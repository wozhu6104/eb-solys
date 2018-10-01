/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public class TableSearchTermNotifyUseCaseImpl implements TableSearchTermNotifyUseCase, PreferencesListener

{
    private TableSearchTermNotifyCallback callback;
    private final PreferencesService preferencesService;
    private final String viewID;

    public TableSearchTermNotifyUseCaseImpl(TableSearchTermNotifyCallback callback, String viewID,
            PreferencesService preferencesService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "viewID", viewID );
        RangeCheckUtils.assertReferenceParameterNotNull( "preferencesService", preferencesService );

        this.callback = callback;
        this.viewID = viewID;
        this.preferencesService = preferencesService;

        postCurrentValuesToCallback();
        preferencesService.registerPreferencesListener( this );
    }

    private void postCurrentValuesToCallback()
    {
        List<String> searchTermsHistory = preferencesService.getTableSearchTermsHistory( viewID );
        final List<String> searchTermsHistoryCopy = new ArrayList<String>( searchTermsHistory );
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                callback.onSearchTermsChanged( searchTermsHistoryCopy );
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
    public void onTableSearchTermsHistoryChanged(final List<String> KeysToSearch, String viewID)
    {
        if (this.viewID.equals( viewID ))
        {
            postCurrentValuesToCallback();
        }
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
