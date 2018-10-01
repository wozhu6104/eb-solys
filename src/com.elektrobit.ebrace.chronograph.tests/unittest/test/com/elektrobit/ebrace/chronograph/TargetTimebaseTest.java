/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.chronograph;

import org.junit.Test;

import com.elektrobit.ebrace.chronograph.impl.TargetTimebase;

import junit.framework.Assert;

public class TargetTimebaseTest
{
    @Test
    public void offsetCalculationTest()
    {
        final long absRaceTimeInMillis = 10;
        final long absTargetTimeInMillis = 0;
        final long nowInMillis = 1000;

        TargetTimebase targetTimebase = new TargetTimebase( absRaceTimeInMillis, absTargetTimeInMillis, nowInMillis );

        Assert.assertEquals( absRaceTimeInMillis, targetTimebase.getInitialAbsoluteRaceTimeInMillis() );
        Assert.assertEquals( absTargetTimeInMillis,
                             targetTimebase.getInitialAbsolueTargetTimeAtRegistrationInMillies() );
        Assert.assertEquals( 990, targetTimebase.getTargetRegistrationOffsetTimeInMillies() );
    }
}
