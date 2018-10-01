/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.preferences.page;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

public class GlobalLineChartSettingsPreferencePage extends LineChartSettingsBasePreferencePage
{
    private final PreferencesService preferencesService = new GenericOSGIServiceTracker<PreferencesService>( PreferencesService.class )
            .getService();

    @Override
    public LineChartModelSettings getModelToAdapt()
    {
        return preferencesService.getGlobalLineChartSettings();
    }

    @Override
    public void saveAdaptedPreferences(LineChartModelSettings lineChartModelSettings)
    {
        preferencesService.setLineChartModelSettings( lineChartModelSettings );
    }

    @Override
    public LineChartModelSettings getDefaultSettings()
    {
        return new LineChartModelSettings();
    }
}
