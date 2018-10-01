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

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.HexStringHelper;

public class HexStringHelperTest
{
    @Test
    public void checkInt8() throws Exception
    {
        Assert.assertEquals( "" + Byte.MIN_VALUE, HexStringHelper.convertHexToInt8( "80" ) );
        Assert.assertEquals( "" + Byte.MAX_VALUE, HexStringHelper.convertHexToInt8( "7f" ) );
    }

    @Test
    public void checkInt16() throws Exception
    {
        Assert.assertEquals( "" + Short.MIN_VALUE, HexStringHelper.convertHexToInt16( "8000" ) );
        Assert.assertEquals( "" + Short.MAX_VALUE, HexStringHelper.convertHexToInt16( "7fff" ) );
    }

    @Test
    public void checkInt32() throws Exception
    {
        Assert.assertEquals( "" + Integer.MIN_VALUE, HexStringHelper.convertHexToInt32( "80000000" ) );
        Assert.assertEquals( "" + Integer.MAX_VALUE, HexStringHelper.convertHexToInt32( "7fffffff" ) );
    }

    @Test
    public void checkUInt8() throws Exception
    {
        Assert.assertEquals( new Integer( 0 ), HexStringHelper.convertHexIDToUInt16( "00" ) );
        Assert.assertEquals( new Integer( 255 ), HexStringHelper.convertHexIDToUInt16( "ff" ) );
    }

    @Test
    public void checkUInt16() throws Exception
    {
        Assert.assertEquals( new Integer( 0 ), HexStringHelper.convertHexIDToUInt16( "0000" ) );
        Assert.assertEquals( new Integer( 65535 ), HexStringHelper.convertHexIDToUInt16( "ffff" ) );
    }

    @Test
    public void checkUInt32() throws Exception
    {
        Assert.assertEquals( new Long( 0 ), HexStringHelper.convertHexIDToUInt32( "00000000" ) );
        Assert.assertEquals( new Long( 4294967295l ), HexStringHelper.convertHexIDToUInt32( "ffffffff" ) );
    }

    @Test
    public void checkUInt64() throws Exception
    {
        Assert.assertEquals( "0", HexStringHelper.convertHexIDToUInt64( "0000000000000000" ).toString( 10 ) );
        Assert.assertEquals( "18446744073709551615",
                             HexStringHelper.convertHexIDToUInt64( "ffffffffffffffff" ).toString( 10 ) );
    }

    @Test
    public void readBitsFromToTest() throws Exception
    {
        Assert.assertEquals( "1", HexStringHelper.readBitsFromTo( "F111", 0, 2 ) );
        Assert.assertEquals( "f", HexStringHelper.readBitsFromTo( "F111", 12, 4 ) );

        Assert.assertEquals( "ff", HexStringHelper.readBitsFromTo( "ffffffffffff", 0, 8 ) );
        Assert.assertEquals( "ff", HexStringHelper.readBitsFromTo( "ffffffffffff", 8, 8 ) );
        Assert.assertEquals( "ffff", HexStringHelper.readBitsFromTo( "ffffffffffff", 16, 16 ) );
        Assert.assertEquals( "ffff", HexStringHelper.readBitsFromTo( "ffffffffffff", 32, 16 ) );

    }

    @Test
    public void takeBitsFromToTest() throws Exception
    {
        Assert.assertEquals( "ffff", HexStringHelper.readBitsFromTo( "ffffffffffff", 16, 16 ) );

        Assert.assertEquals( "1", HexStringHelper.takeBitsFromTo( "F111", 0, 2 ) );
        Assert.assertEquals( "f000", HexStringHelper.takeBitsFromTo( "F111", 12, 4 ) );

    }

    @Test
    public void removeNullTerminatorGoodCaseTest() throws Exception
    {
        Assert.assertEquals( "00", HexStringHelper.removeNullTerminatorFromHexString( "0000" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTerminatorSizeNotEvenTest() throws Exception
    {
        HexStringHelper.removeNullTerminatorFromHexString( "100" );
    }
}
