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

import org.junit.Test;

import com.elektrobit.ebrace.common.profiling.PerformanceHelper;

import junit.framework.Assert;

public class PerformanceHelperTest
{
    @Test
    public void simpleMeasureTest() throws Exception
    {
        PerformanceHelper performanceHelper = new PerformanceHelper( 1 );

        performanceHelper.start();
        Thread.sleep( 100 );
        performanceHelper.stop();

        Assert.assertEquals( 100.0, performanceHelper.min() );
        Assert.assertEquals( 100.0, performanceHelper.avg() );
        Assert.assertEquals( 100.0, performanceHelper.max() );

    }

    @Test
    public void complexMeasureTest() throws Exception
    {
        PerformanceHelper performanceHelper = new PerformanceHelper( 2 );

        performanceHelper.start();
        Thread.sleep( 100 );
        performanceHelper.stop();
        Thread.sleep( 33 );
        performanceHelper.start();
        Thread.sleep( 50 );
        performanceHelper.stop();

        Assert.assertEquals( 50.0, performanceHelper.min() );
        Assert.assertEquals( 75.0, performanceHelper.avg() );
        Assert.assertEquals( 100.0, performanceHelper.max() );

    }
}
