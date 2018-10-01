/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.chartData;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.core.interactor.api.chartdata.RunMode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;

public class ChartAxisMinMaxGenerator
{
    private final static Logger LOG = Logger.getLogger( ChartAxisMinMaxGenerator.class );

    public static class MinMax<T extends Number>
    {
        private final T min;
        private final T max;

        public MinMax(T min, T max)
        {
            this.min = min;
            this.max = max;
        }

        public T getMin()
        {
            return min;
        }

        public T getMax()
        {
            return max;
        }
    }

    public static MinMax<Double> computeYaxisMinMax(double foundMin, double foundMax, ChartModel chartmodel)
    {
        double maxFromModel = chartmodel.getMaxYAxis();
        double minFromModel = chartmodel.getMinYAxis();

        boolean isSemiDynamic = chartmodel.isSemiDynamic();
        boolean isFix = chartmodel.isFix();

        if (isSemiDynamic && isFix)
            LOG.warn( "Both dynamic and fix flags are set! Choose only one" );
        if (maxFromModel < minFromModel)
            LOG.warn( "Maximum from chart model smaller then minimum!" );

        if (isSemiDynamic)
        {
            double resultMin = foundMin < minFromModel ? foundMin : minFromModel;
            double resultMax = foundMax > maxFromModel ? addSpaceToMax( foundMax ) : maxFromModel;

            return new MinMax<Double>( resultMin, resultMax );
        }
        if (isFix)
            return new MinMax<Double>( minFromModel, maxFromModel );

        double resultMax = addSpaceToMax( foundMax );
        double resultMin = addSpaceToMin( foundMin );
        return new MinMax<Double>( resultMin, resultMax );
    }

    public static MinMax<Long> computeXaxisMinMax(RunMode runMode,
            AnalysisTimespanPreferences analysisTimespanPreferences)
    {
        long startTimestamp = 0;
        long endTimestamp = 0;

        switch (runMode)
        {
            case LIVE :
                startTimestamp = analysisTimespanPreferences.getAnalysisTimespanStart();
                endTimestamp = analysisTimespanPreferences.getAnalysisTimespanEnd();
                break;
            case ANALYSIS :
                startTimestamp = analysisTimespanPreferences.getAnalysisTimespanStart();
                endTimestamp = analysisTimespanPreferences.getAnalysisTimespanEnd();
                break;
            case FULL :
                startTimestamp = analysisTimespanPreferences.getFullTimespanStart();
                endTimestamp = analysisTimespanPreferences.getFullTimespanEnd();
                break;
            default :
                LOG.error( "Run mode " + runMode + " not considered" );
                break;
        }
        return new MinMax<Long>( startTimestamp, endTimestamp );
    }

    private static int addSpaceToMax(double max)
    {
        int rest = (int)max % 10;
        return (int)Math.floor( max ) + 10 - rest;
    }

    private static int addSpaceToMin(double min)
    {
        int rest = (int)min % 10;
        return ((int)Math.floor( min ) - rest);
    }
}
