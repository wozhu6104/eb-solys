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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartEntry;

public class GanttChartEntryImpl implements GanttChartEntry
{
    private final long endTimestamp;
    private final long startTimestamp;

    public GanttChartEntryImpl(long startTimestamp, long endTimestamp)
    {
        RangeCheckUtils.assertCorrectIntervalBoundaries( "gantt entry", startTimestamp, endTimestamp );
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    @Override
    public long getStartTimeStamp()
    {
        return startTimestamp;
    }

    @Override
    public long getEndTimeStamp()
    {
        return endTimestamp;
    }

    @Override
    public long getLength()
    {
        return endTimestamp - startTimestamp;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GanttChartEntryImpl other = (GanttChartEntryImpl)obj;
        if (endTimestamp != other.endTimestamp)
            return false;
        if (startTimestamp != other.startTimestamp)
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(endTimestamp ^ (endTimestamp >>> 32));
        result = prime * result + (int)(startTimestamp ^ (startTimestamp >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "[" + startTimestamp + "," + endTimestamp + "]";
    }
}
