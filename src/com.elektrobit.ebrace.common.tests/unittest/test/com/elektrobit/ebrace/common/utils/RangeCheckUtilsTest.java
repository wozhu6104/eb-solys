/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class RangeCheckUtilsTest
{
    @Test(expected = IllegalArgumentException.class)
    public void valueGreaterThanCompareValueThrowsException() throws Exception
    {
        RangeCheckUtils.assertGreaterOrEqual( "startParam", 5L, 3L );
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueEqualThanCompareValueThrowsException() throws Exception
    {
        RangeCheckUtils.assertGreaterOrEqual( "startParam", 5L, 5L );
    }

    @Test
    public void valueSmallerThanCompareValueThrowsNoException()
    {
        RangeCheckUtils.assertGreaterOrEqual( "startParam", 3L, 5L );
        assertTrue( true );
    }

    @Test
    public void assertIntegerInInterval() throws Exception
    {
        RangeCheckUtils.assertIntegerInInterval( "interval", 2, 1, 3 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIntegerOverInterval() throws Exception
    {
        RangeCheckUtils.assertIntegerInInterval( "interval", 4, 1, 3 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIntegerUnderInterval() throws Exception
    {
        RangeCheckUtils.assertIntegerInInterval( "interval", 0, 1, 3 );
    }
}
