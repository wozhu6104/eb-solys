/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.MinMax;

public class MinMaxTest
{
    @Test
    public void testBothNull() throws Exception
    {
        MinMax<Integer> sut = new MinMax<Integer>();
        Assert.assertFalse( sut.contains( 3 ) );
    }

    @Test
    public void testMaxNull() throws Exception
    {
        MinMax<Integer> sut = new MinMax<Integer>();
        sut.setMin( 0 );
        Assert.assertFalse( sut.contains( 3 ) );
    }

    @Test
    public void testMinNull() throws Exception
    {
        MinMax<Integer> sut = new MinMax<Integer>();
        sut.setMax( 0 );
        Assert.assertFalse( sut.contains( 3 ) );
    }

    @Test
    public void testNoNull() throws Exception
    {
        MinMax<Integer> sut = new MinMax<Integer>();
        sut.setMax( 0 );
        sut.setMin( -1 );
        Assert.assertFalse( sut.contains( 3 ) );
    }

    @Test
    public void testMaxMatch() throws Exception
    {
        MinMax<Integer> sut = new MinMax<Integer>();
        sut.setMax( 3 );
        sut.setMin( -1 );
        Assert.assertTrue( sut.contains( 3 ) );
    }

    @Test
    public void testMinMatch() throws Exception
    {
        MinMax<Integer> sut = new MinMax<Integer>();
        sut.setMax( 3 );
        sut.setMin( -1 );
        Assert.assertTrue( sut.contains( -1 ) );
    }
}
