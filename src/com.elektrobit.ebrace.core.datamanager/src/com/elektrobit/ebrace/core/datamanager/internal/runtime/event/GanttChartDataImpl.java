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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartEntry;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class GanttChartDataImpl implements GanttChartData
{
    private final List<GanttChartEntry[]> data;
    public List<RuntimeEventChannel<?>> channels;

    public GanttChartDataImpl(List<RuntimeEventChannel<?>> channels, List<GanttChartEntry[]> data)
    {
        checkDataFormat( data );
        this.channels = channels;
        this.data = data;
    }

    private void checkDataFormat(List<GanttChartEntry[]> data)
    {
        if (!data.isEmpty())
        {
            int length = data.get( 0 ).length;
            for (GanttChartEntry[] ganttChartEntries : data)
                RangeCheckUtils.assertSizeOfArrayIsExactly( "Gantt Entries length", ganttChartEntries, length );
        }
    }

    @Override
    public List<GanttChartEntry[]> getData()
    {
        return data;
    }

    @Override
    public List<RuntimeEventChannel<?>> getChannels()
    {
        return channels;
    }
}
