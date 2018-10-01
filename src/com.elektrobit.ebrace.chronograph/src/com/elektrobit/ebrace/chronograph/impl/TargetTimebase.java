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

public class TargetTimebase
{
    private final long initialAbsoluteRaceTimeInMillis;
    private long initialAbsoluteTargetTimeAtRegistrationInMillis;
    private long targetRegistrationOffsetTimeInMillis;

    public TargetTimebase(long initialAbsoluteRaceTimeInMillis, long initialAbsolueTargetTimeAtRegistrationInMillies,
            long currentTimeInMillis)
    {
        this.initialAbsoluteRaceTimeInMillis = initialAbsoluteRaceTimeInMillis;
        this.initialAbsoluteTargetTimeAtRegistrationInMillis = initialAbsolueTargetTimeAtRegistrationInMillies;
        this.targetRegistrationOffsetTimeInMillis = currentTimeInMillis - initialAbsoluteRaceTimeInMillis;
    }

    public void rebaseAbsoluteTargetTimeAtRegistration(long newAbsoluteTargetTimeInMillies, long currentTimeInMillis)
    {
        this.initialAbsoluteTargetTimeAtRegistrationInMillis = newAbsoluteTargetTimeInMillies;
        this.targetRegistrationOffsetTimeInMillis = currentTimeInMillis - initialAbsoluteRaceTimeInMillis;
    }

    public long getInitialAbsoluteRaceTimeInMillis()
    {
        return initialAbsoluteRaceTimeInMillis;
    }

    public long getInitialAbsolueTargetTimeAtRegistrationInMillies()
    {
        return initialAbsoluteTargetTimeAtRegistrationInMillis;
    }

    public long getTargetRegistrationOffsetTimeInMillies()
    {
        return targetRegistrationOffsetTimeInMillis;
    }

}
