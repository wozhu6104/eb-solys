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

public final class TimingEntry
{
    private final long _startTime;
    private long _stopTime;

    public TimingEntry(final long startTime)
    {
        _startTime = startTime;
    }

    public void setStopTime(final long stopTime)
    {
        _stopTime = stopTime;
    }

    public long getStartTime()
    {
        return _startTime;
    }

    public long getStoptime()
    {
        return _stopTime;
    }
}
