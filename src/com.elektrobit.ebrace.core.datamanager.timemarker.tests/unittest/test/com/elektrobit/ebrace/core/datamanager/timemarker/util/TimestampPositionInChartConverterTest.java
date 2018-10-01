/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.timemarker.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.timemarker.util.TimestampPositionInChartConverter;

public class TimestampPositionInChartConverterTest
{
    double minTimeAxis = 0;
    double plotWidth = 100;
    long startTimeOfAnalysisChart = 0;
    long completeTimespanInAnalysisModeInMillis = 100;
    long timestamp;
    int position;

    @Test
    public void testCalculatePositionForTimestamp()
    {
        timestamp = 0;
        position = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( minTimeAxis,
                                                plotWidth,
                                                timestamp,
                                                startTimeOfAnalysisChart,
                                                completeTimespanInAnalysisModeInMillis );
        assertEquals( 0, position );
        timestamp = 50;
        position = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( minTimeAxis,
                                                plotWidth,
                                                timestamp,
                                                startTimeOfAnalysisChart,
                                                completeTimespanInAnalysisModeInMillis );
        assertEquals( 50, position );
        timestamp = 100;
        position = (int)TimestampPositionInChartConverter
                .calculatePositionForTimestamp( minTimeAxis,
                                                plotWidth,
                                                timestamp,
                                                startTimeOfAnalysisChart,
                                                completeTimespanInAnalysisModeInMillis );
        assertEquals( 100, position );
    }
}
