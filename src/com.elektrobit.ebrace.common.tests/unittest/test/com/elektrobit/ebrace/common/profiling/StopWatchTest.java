/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.profiling;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

import junit.framework.Assert;

public class StopWatchTest
{
    @Test
    public void simpleTest() throws Exception
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Thread.sleep( 50 );
        stopWatch.stop();

        Assert.assertEquals( 50, stopWatch.getTime() );

    }

    @Test
    public void testName() throws Exception
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Thread.sleep( 50 );
        stopWatch.split();
        System.out.println( stopWatch.toSplitString() );
        Thread.sleep( 100 );
        stopWatch.stop();

        Assert.assertEquals( 150, stopWatch.getTime() );

    }
}
