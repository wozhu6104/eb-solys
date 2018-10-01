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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteOrder;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.ByteArrayHelper;

public class ByteArrayHelperTest
{
    @Test
    public void getLongFromByte()
    {
        assertEquals( 1, ByteArrayHelper.bytesToLong( new byte[]{1}, ByteOrder.LITTLE_ENDIAN ) );
        assertEquals( 255, ByteArrayHelper.bytesToLong( new byte[]{(byte)0xff}, ByteOrder.LITTLE_ENDIAN ) );
        assertEquals( 255,
                      ByteArrayHelper.bytesToLong( new byte[]{0, 0, 0, 0, 0, 0, 0, (byte)0xff},
                                                   ByteOrder.BIG_ENDIAN ) );
        assertEquals( Long.MIN_VALUE,
                      ByteArrayHelper.bytesToLong(
                                                   new byte[]{(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,
                                                           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
                                                   ByteOrder.BIG_ENDIAN ) );
        assertEquals( Long.MAX_VALUE,
                      ByteArrayHelper.bytesToLong(
                                                   new byte[]{(byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff,
                                                           (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff},
                                                   ByteOrder.BIG_ENDIAN ) );
    }

    @Test
    public void testIntToArrayConversion()
    {
        assertArrayEquals( new byte[]{0x00, 0x00, 0x00, 0x01}, ByteArrayHelper.intToByteArray( 1, false ) );
        assertArrayEquals( new byte[]{0x01, 0x00, 0x00, 0x00}, ByteArrayHelper.intToByteArray( 1, true ) );
        assertEquals( 1, ByteArrayHelper.byteArrayToInt( new byte[]{0x00, 0x00, 0x00, 0x01}, false ) );
        assertEquals( 1, ByteArrayHelper.byteArrayToInt( new byte[]{0x01, 0x00, 0x00, 0x00}, true ) );

        assertArrayEquals( new byte[]{0x00, 0x00, 0x00, 0x01},
                           ByteArrayHelper.intToByteArray( ByteArrayHelper
                                   .byteArrayToInt( new byte[]{0x00, 0x00, 0x00, 0x01}, false ), false ) );
        assertArrayEquals( new byte[]{0x01, 0x00, 0x00, 0x00},
                           ByteArrayHelper.intToByteArray( ByteArrayHelper
                                   .byteArrayToInt( new byte[]{0x01, 0x00, 0x00, 0x00}, true ), true ) );
    }

    @Test
    public void testShortToArrayConversion()
    {
        assertArrayEquals( new byte[]{0x00, 0x01}, ByteArrayHelper.shortToByteArray( (short)1, false ) );
        assertArrayEquals( new byte[]{0x01, 0x00}, ByteArrayHelper.shortToByteArray( (short)1, true ) );
        assertEquals( 1, ByteArrayHelper.byteArrayToShort( new byte[]{0x00, 0x01}, false ) );
        assertEquals( 1, ByteArrayHelper.byteArrayToShort( new byte[]{0x01, 0x00}, true ) );

        assertArrayEquals( new byte[]{0x00, 0x01},
                           ByteArrayHelper
                                   .shortToByteArray( ByteArrayHelper.byteArrayToShort( new byte[]{0x00, 0x01}, false ),
                                                      false ) );
        assertArrayEquals( new byte[]{0x01, 0x00},
                           ByteArrayHelper
                                   .shortToByteArray( ByteArrayHelper.byteArrayToShort( new byte[]{0x01, 0x00}, true ),
                                                      true ) );
    }

    @Test
    public void testIntToArrayConversionMinMax()
    {
        assertArrayEquals( new byte[]{(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00},
                           ByteArrayHelper.intToByteArray( Integer.MIN_VALUE, false ) );
        assertArrayEquals( new byte[]{(byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff},
                           ByteArrayHelper.intToByteArray( Integer.MAX_VALUE, false ) );
    }

}
