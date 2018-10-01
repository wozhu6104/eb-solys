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

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.chronograph.api.TimestampMode;
import com.elektrobit.ebrace.chronograph.impl.TargetTimebase;
import com.elektrobit.ebrace.chronograph.impl.TimestampImpl;

import junit.framework.Assert;

public class TimestampTest
{
    private TimestampImpl timestamp;
    private TargetTimebase targetTimebase;

    @Before
    public void setup()
    {
        targetTimebase = new TargetTimebase( 10, 0, 100 );
    }

    @Test
    public void absoluteTargetTimeTest()
    {
        timestamp = new TimestampImpl( TimestampMode.ABSOLUTE_TARGET_TIME, targetTimebase, 200 );
        Assert.assertEquals( 200, timestamp.getTimeInMillis() );
    }

    @Test
    public void absoluteRaceTimeTest()
    {
        timestamp = new TimestampImpl( TimestampMode.ABSOLUTE_RACE_TIME, targetTimebase, 200 );
        Assert.assertEquals( 300, timestamp.getTimeInMillis() );
    }

    @Test
    public void relativeTargetTimeTest()
    {
        timestamp = new TimestampImpl( TimestampMode.RELATIVE_TARGET_TIME, targetTimebase, 200 );
        Assert.assertEquals( 200, timestamp.getTimeInMillis() );
    }

    @Test
    public void relativeRaceTimeTest()
    {
        timestamp = new TimestampImpl( TimestampMode.RELATIVE_RACE_TIME, targetTimebase, 200 );
        Assert.assertEquals( 290, timestamp.getTimeInMillis() );
    }

}
