/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf.services;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;
import com.elektrobit.ebrace.decoder.protobuf.model.DefaultPrimitiveDecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

@Component(property = {ServiceConstants.CLAZZ_TYPE + "=java.lang.String"})
public class StringDecoderService implements DecoderService, PreferencesListener
{
    private PreferencesService preferencesService;
    private String timestampFormat = "HH:mm:ss.SSS";
    private final JsonDecoder jsonDecoder;

    public StringDecoderService()
    {
        jsonDecoder = new JsonDecoder();
    }

    @Reference
    public void bindPreferencesService(PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
        timestampFormat = preferencesService.getTimestampFormatPreferences();
        preferencesService.registerPreferencesListener( this );
    }

    @Override
    public DecodedRuntimeEvent decode(RuntimeEvent<?> event)
    {

        DecodedRuntimeEvent runtimeEvent = jsonDecoder.decode( event );
        if (runtimeEvent == null)
        {
            runtimeEvent = new DefaultPrimitiveDecodedRuntimeEvent( event, timestampFormat );

        }
        return runtimeEvent;
    }

    @Override
    public void onTimestampFormatPreferencesChanged()
    {
        if (preferencesService != null)
        {
            timestampFormat = preferencesService.getTimestampFormatPreferences();
        }
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

    public void unbindPreferencesService(PreferencesService preferencesService)
    {
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
