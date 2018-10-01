/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.preferences.util;

public interface ChartPreferencesConstants
{
    String CHART_TIME_PRESENTATION_PREFERENCE_ID = "chartDateFormat";
    String CHART_TIME_PRESENTATION_PREFERENCE_AS_DATE_ID = "date";
    String CHART_TIME_PRESENTATION_PREFERENCE_CHART_LEGEND_VISIBLE_LABEL = "Chart legend visible";

    String DATA_CHART_PRESENTATION_PREFERENCE_ID = "dataChartRepresentation";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_BAR_STRING = "Bar chart";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_AREA_STRING = "Line chart";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_BAR_ID = "bar";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_AREA_ID = "area";

    String LINE_CHART_PRESENTATION_PREFERENCE_ID = "lineChartRepresentation";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_LINE_TYPE_STRING = "Line only";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_FILLED_TYPE_STRING = "Filled";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_STACKED_TYPE_STRING = "Stacked";

    String LINE_CHART_PRESENTATION_PREFERENCE_AS_LINE_CHART_ID = "lineChartType";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_AREA_CHART_ID = "areaChartType";
    String LINE_CHART_PRESENTATION_PREFERENCE_AS_STACKED_CHART_ID = "stackedChartType";

    String LINE_CHART_PRESENTATION_Y_AXIS_PREFERENCE_ID = "yAxisFormatPreferenceId";
    String LINE_CHART_PRESENTATION_Y_AXIS_DYNAMIC = "Dynamic";
    String LINE_CHART_PRESENTATION_Y_AXIS_SEMI_DYNAMIC = "Semi dynamic";
    String LINE_CHART_PRESENTATION_Y_AXIS_FIX = "Fixed";

    String LINE_CHART_PRESENTATION_Y_AXIS_DYNAMIC_ID = LINE_CHART_PRESENTATION_Y_AXIS_PREFERENCE_ID + "." + "dynamic";
    String LINE_CHART_PRESENTATION_Y_AXIS_SEMI_DYNAMIC_ID = LINE_CHART_PRESENTATION_Y_AXIS_PREFERENCE_ID + "."
            + "semi_dynamic";
    String LINE_CHART_PRESENTATION_Y_AXIS_FIX_ID = LINE_CHART_PRESENTATION_Y_AXIS_PREFERENCE_ID + "." + "fix";

    String LINE_CHART_PRESENTATION_Y_AXIS_FIX_MIN = "yAxisMin";
    String LINE_CHART_PRESENTATION_Y_AXIS_FIX_MAX = "yAxisMax";
    String LINE_CHART_DATA_PRESENTATION_LABEL = "Chart type";
    String LINE_CHART_LINE_CHART_PRESENTATION_LABEL = "Chart representation";
    String LINE_CHART_Y_AXIS_FORMAT_LABEL = "Y-axis scale";

}
