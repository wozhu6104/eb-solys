/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.resources.model.chart;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface ChartModel extends ResourceModel, IAdaptable
{
    public ChartTypes getType();

    public LineChartModelSettings getLineChartModelSettings();

    public void setLineChartModelSettings(LineChartModelSettings lineChartModelSettings);

    public void setType(ChartTypes type);

    public void loadGlobalSettings();

    public boolean channelsMatchChartType(List<RuntimeEventChannel<?>> channels);

    public boolean isAreaChartType();

    public boolean isLineChartPresAsBar();

    public boolean isStackedChart();

    public int getMinYAxis();

    public int getMaxYAxis();

    public boolean isFix();

    public boolean isSemiDynamic();
}
