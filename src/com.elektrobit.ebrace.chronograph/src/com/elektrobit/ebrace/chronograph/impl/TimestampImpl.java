/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.chronograph.impl;

import com.elektrobit.ebrace.chronograph.api.TimestampMode;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

public class TimestampImpl implements Timestamp
{

    private final long currentAbsolueTargetTimeInMillies;
    private final TargetTimebase targetTimebase;
    private final TimestampMode timestampMode;

    public TimestampImpl(TimestampMode timestampMode, TargetTimebase targetTimebase,
            long currentAbsolueTargetTimeInMillies)
    {
        this.timestampMode = timestampMode;
        this.targetTimebase = targetTimebase;
        this.currentAbsolueTargetTimeInMillies = currentAbsolueTargetTimeInMillies;
    }

    @Override
    public long getRelativeRaceTimeInMillies()
    {
        return getAbsoluteRaceTimeInMillis() - targetTimebase.getInitialAbsoluteRaceTimeInMillis();
    }

    @Override
    public long getAbsoluteRaceTimeInMillis()
    {
        return targetTimebase.getInitialAbsoluteRaceTimeInMillis()
                + targetTimebase.getTargetRegistrationOffsetTimeInMillies() + getRelativeTargetTimeInMillies();
    }

    @Override
    public long getRelativeTargetTimeInMillies()
    {
        return currentAbsolueTargetTimeInMillies - targetTimebase.getInitialAbsolueTargetTimeAtRegistrationInMillies();
    }

    @Override
    public long getAbsoluteTargetTimeInMillies()
    {
        return currentAbsolueTargetTimeInMillies;
    }

    @Override
    public long getTimeInMillis()
    {
        long timeInMillis = -1;
        switch (timestampMode)
        {
            case ABSOLUTE_RACE_TIME :
                timeInMillis = getAbsoluteRaceTimeInMillis();
                break;
            case ABSOLUTE_TARGET_TIME :
                timeInMillis = getAbsoluteTargetTimeInMillies();
                break;
            case RELATIVE_RACE_TIME :
                timeInMillis = getRelativeRaceTimeInMillies();
                break;
            case RELATIVE_TARGET_TIME :
                timeInMillis = getRelativeTargetTimeInMillies();
                break;
            default :
                break;
        }

        return timeInMillis;
    }
}
