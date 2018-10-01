/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class LineChartDataImpl implements LineChartData
{
    private final Map<RuntimeEventChannel<?>, List<Number>> seriesForChannels;
    private final List<Long> timestamps;
    private final Double minValue;
    private final Double maxValue;
    private final Double maxValueStacked;

    public LineChartDataImpl(Map<RuntimeEventChannel<?>, List<Number>> seriesForChannels, List<Long> timestamps,
            Double minValue, Double maxValue, Double maxValueStacked)
    {
        checkDataFormat( seriesForChannels, timestamps );
        this.seriesForChannels = seriesForChannels;
        this.timestamps = timestamps;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.maxValueStacked = maxValueStacked;
    }

    private void checkDataFormat(Map<RuntimeEventChannel<?>, List<Number>> seriesForChannels, List<Long> timestamps)
    {
        int length = timestamps.size();
        for (List<Number> list : seriesForChannels.values())
            RangeCheckUtils.assertSizeOfListIsExactly( "Series list length", list, length );
    }

    @Override
    public Map<RuntimeEventChannel<?>, List<Number>> getSeriesData()
    {
        return seriesForChannels;
    }

    @Override
    public List<Long> getTimestamps()
    {
        return timestamps;
    }

    @Override
    public double getMaxValue()
    {
        return maxValue;
    }

    @Override
    public double getMinValue()
    {
        return minValue;
    }

    @Override
    public double getMaxValueStacked()
    {
        return maxValueStacked;
    }
}
