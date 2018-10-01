/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.preferences;

import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;

public interface PreferencesNotifyCallback
{
    public void onTimestampFormatChanged(String newTimestampFormat);

    default public void onLineChartModelSettingsChanged(LineChartModelSettings newSettings)
    {
    };
}
