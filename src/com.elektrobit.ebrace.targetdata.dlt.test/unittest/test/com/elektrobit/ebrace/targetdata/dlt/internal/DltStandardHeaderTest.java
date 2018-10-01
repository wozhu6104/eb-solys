/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.ByteArrayHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStandardHeader;

public class DltStandardHeaderTest
{

    private final DltStandardHeader header = new DltStandardHeader();

    @Test
    public void setAndGetAttributes()
    {
        byte[] timestamp = new byte[]{0x22, 0x11, 0x00, 0x00};
        header.setEcuId( "worm" );
        header.setExtendedHeaderBit();
        header.setMessageLength( 12345 );
        header.setTimestamp( timestamp );
        assertTrue( header.hasECUId() );
        assertTrue( !header.hasSessionId() );
        assertTrue( header.hasTimeStamp() );
        assertEquals( header.getECUId(), "worm" );
        assertEquals( header.getTimeStamp(),
                      ByteArrayHelper.byteArrayToInt( timestamp, false ) / DltStandardHeader.DLT_TIME_UNITS_IN_MS );
    }

    @Test
    public void getLength()
    {
        header.setMessageLength( 40000 );
        assertTrue( header.getMessageLength() == 40000 );

        header.setMessageLength( 13 );
        assertTrue( header.getMessageLength() == 13 );

        header.setMessageLength( 13 );
        assertTrue( header.getMessageLength() == 13 );
    }

    @Test(expected = Exception.class)
    public void setLengthOutOfBounds()
    {
        header.setMessageLength( 65536 );
    }

    @Test(expected = Exception.class)
    public void setLengthNegative()
    {
        header.setMessageLength( -12 );
    }
}
