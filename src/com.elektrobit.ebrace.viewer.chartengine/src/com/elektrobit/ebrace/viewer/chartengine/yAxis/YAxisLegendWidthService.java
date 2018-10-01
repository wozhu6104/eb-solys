/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.yAxis;

import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;

public interface YAxisLegendWidthService
{
    public void notifyLegendWidthChanged(ChartModel model, int value);

    public void notifyChartClosed(ChartModel model);

    public int getYAxisLegendMaxWidth();

    public void addToHidden(ChartModel chartModel);

    public void removeFromHidden(ChartModel chartModel);
}
