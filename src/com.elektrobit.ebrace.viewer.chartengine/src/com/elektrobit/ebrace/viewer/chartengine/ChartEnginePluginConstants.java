/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine;

public interface ChartEnginePluginConstants
{
    public static final String PLUGIN_ID = "com.elektrobit.ebrace.viewer.chartengine";
    public static final String GENERIC_CHART_VIEW_ID = "com.elektrobit.ebrace.viewer.chartengine.ChartView";
    public static final String GANTT_CHART_VIEW_ID = "com.elektrobit.ebrace.viewer.chartengine.ganttchartview";

    public static final String RUNTIME_EVENT_LOGGER_TABLE_VIEW_ID = "com.elektrobit.ebrace.viewer.runtimeeventlogger.RuntimeEventLoggerTableView";
    public static final String SELECTION_COMMAND = "com.elektrobit.ebrace.viewer.runtimeeventlogger.clearselection";
    public static final String SELECTION_HANDLER_PARAMETER = "com.elektrobit.ebrace.viewer.runtimeeventlogger.action";

    public static final ChartTimeIntervalCategory CHART_TIME_IN_MILLIS = ChartTimeIntervalCategory.ONE_MINUTE;

    public static enum ChartTimeIntervalCategory {
        TEN_SECONDS(10000), ONE_MINUTE(60000), TEN_MINUTES(600000), HALF_HOUR(1800000);

        private final long chartTimeIntervalInMillis;

        ChartTimeIntervalCategory(long chartTimeintervalInMillis)
        {
            this.chartTimeIntervalInMillis = chartTimeintervalInMillis;
        }

        public long getChartTimeIntervallInMillis()
        {
            return chartTimeIntervalInMillis;
        }
    }
}
