/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.chartdata;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;

public interface ChartDataCallback
{
    public void onNewLineChartData(LineChartData lineChartData);

    public void onNewGanttChartData(GanttChartData ganttChartData);

    public void onSelectedChannelsChanged();

    public int getWidth();

    public void onTimeMarkerChanged();
}
