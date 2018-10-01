/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.properties;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.viewer.common.propertySupport.EBRacePropertyChangeSupport;
import com.elektrobit.ebrace.viewer.common.propertySupport.PropertyChangeConstants;
import com.elektrobit.ebrace.viewer.preferences.page.LineChartSettingsBasePreferencePage;

public class LineChartPropertyPage extends LineChartSettingsBasePreferencePage
{
    private final PreferencesService preferencesService = new GenericOSGIServiceTracker<PreferencesService>( PreferencesService.class )
            .getService();

    @Override
    public LineChartModelSettings getModelToAdapt()
    {
        ChartModel chartModel = (ChartModel)getElement();
        return chartModel.getLineChartModelSettings();
    }

    @Override
    public void saveAdaptedPreferences(LineChartModelSettings lineChartModelSettings)
    {
        ChartModel chartModel = (ChartModel)getElement();
        chartModel.setLineChartModelSettings( lineChartModelSettings );
        EBRacePropertyChangeSupport
                .firePropertyChangedEvent( chartModel, PropertyChangeConstants.MODEL_PROPERTIES_CHANGED, "old", "new" );
    }

    @Override
    public LineChartModelSettings getDefaultSettings()
    {
        return preferencesService.getGlobalLineChartSettings();
    }
}
