/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.chart;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineChartModelSettings
{
    public enum LineChartType {
        LINE_CHART, BAR_CHART
    };

    public enum LineChartRepresentation {
        FILLED, LINE_ONLY, STACKED
    };

    public enum LineChartYaxisScaleMode {
        DYNAMIC, SEMI_DYNAMIC, FIXED
    };

    private LineChartType lineChartType;
    private LineChartRepresentation lineChartRepresentation;
    private LineChartYaxisScaleMode lineChartYaxisScaleMode;

    private int yAxisMinValue;
    private int yAxisMaxValue;

    public LineChartModelSettings()
    {
        lineChartType = LineChartType.BAR_CHART;
        lineChartRepresentation = LineChartRepresentation.LINE_ONLY;
        lineChartYaxisScaleMode = LineChartYaxisScaleMode.DYNAMIC;
        yAxisMinValue = 0;
        yAxisMaxValue = 100;
    }
}
