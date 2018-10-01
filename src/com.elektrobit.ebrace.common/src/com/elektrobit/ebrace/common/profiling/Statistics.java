/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.profiling;

import java.util.Arrays;
import java.util.List;

public class Statistics
{
    double[] data;
    int size;

    public Statistics(double[] data)
    {
        initMemberVariables( data );
    }

    private void initMemberVariables(double[] data)
    {
        this.data = data;
        size = data.length;
    }

    public Statistics(long[] data)
    {
        double[] doubleArray = convertLongArrayInDoubleArray( data );
        initMemberVariables( doubleArray );
    }

    private double[] convertLongArrayInDoubleArray(long[] data)
    {
        double[] doubleArray = new double[data.length];
        for (int i = 0; i < data.length; i++)
        {
            doubleArray[i] = data[i];
        }
        return doubleArray;
    }

    public Statistics(List<Double> data)
    {
        double[] doubleArray = convertDoubleListInDoubleArray( data );
        initMemberVariables( doubleArray );
    }

    private double[] convertDoubleListInDoubleArray(List<Double> data)
    {
        double[] doubleArray = new double[data.size()];
        for (int i = 0; i < data.size(); i++)
        {
            doubleArray[i] = data.get( i );
        }
        return doubleArray;
    }

    public double getMean()
    {
        double sum = 0.0;
        for (double a : data)
            sum += a;
        return sum / size;
    }

    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for (double a : data)
            temp += (mean - a) * (mean - a);
        return temp / size;
    }

    public double getMeanDev()
    {
        double mean = getMean();
        double temp = 0;
        for (double a : data)
            temp += Math.abs( mean - a );
        return temp / size;
    }

    public double getMeanDevInPercent()
    {
        return getMeanDev() / getMean();
    }

    public double getStdDev()
    {
        return Math.sqrt( getVariance() );
    }

    public double median()
    {
        Arrays.sort( data );

        if (data.length % 2 == 0)
        {
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        }
        else
        {
            return data[data.length / 2];
        }
    }

    public double min()
    {
        double result = data[0];

        for (double nextVal : data)
        {
            if (nextVal < result)
                result = nextVal;
        }

        return result;
    }

    public double avg()
    {
        return getMean();
    }

    public double max()
    {
        double result = data[0];

        for (double nextVal : data)
        {
            if (nextVal > result)
                result = nextVal;
        }

        return result;
    }
}
