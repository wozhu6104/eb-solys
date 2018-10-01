/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt.logimporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStandardHeader;

public class DltStandardHeaderTest
{
    @Test
    public void stdHeaderWithTimestampAndEcu() throws Exception
    {
        DltStandardHeader header = parseDltStandardMessageHeader( "3daf1356b4c70e204545764d" );

        assertTrue( header.hasECUId() );
        assertTrue( header.hasTimeStamp() );
        assertEquals( 4950, header.getMessageLength() );
    }

    @Test
    public void stdHeaderNoTimestampNoEcu() throws Exception
    {
        DltStandardHeader header = parseDltStandardMessageHeader( "e8af1356b4c70e204545764d" );

        assertFalse( header.hasECUId() );
        assertFalse( header.hasTimeStamp() );
    }

    @Test
    public void littleEndianPayload() throws Exception
    {
        DltStandardHeader header = parseDltStandardMessageHeader( "a8af1356b4c70e204545764d" );

        assertFalse( header.isPayloadInBigEndian() );
    }

    @Test
    public void setProperties() throws Exception
    {
        DltStandardHeader header = new DltStandardHeader();
        header.setExtendedHeaderBit();
        header.setEcuId( "ECU1" );
        assertTrue( header.hasExtendedHeader() );
        assertTrue( header.getECUId().equals( "ECU1" ) );
    }

    private DltStandardHeader parseDltStandardMessageHeader(String hexString) throws Exception
    {
        byte[] decimalsString = HexStringHelper.hexStringToByteArray( hexString );
        return new DltStandardHeader( decimalsString );
    }
}
