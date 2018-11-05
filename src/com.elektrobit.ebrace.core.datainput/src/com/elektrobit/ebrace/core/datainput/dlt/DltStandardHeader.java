/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.dlt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.elektrobit.ebrace.common.utils.ByteArrayHelper;

import lombok.extern.log4j.Log4j;

@Log4j
public class DltStandardHeader
{
    private final static int STANDARD_HEADER_SIZE = 4;
    private final static int FIELD_SIZE = 4;
    public final static int DLT_TIME_UNITS_IN_MS = 10;

    private byte[] header = new byte[STANDARD_HEADER_SIZE];

    private final byte[] ecuId = new byte[FIELD_SIZE];

    private final byte[] sessionId = new byte[FIELD_SIZE];

    private final byte[] timestamp = new byte[FIELD_SIZE];

    // Standard Header
    private final static int UEH = 0x01;
    private final static int MSBF = 0x02;
    private final static int WEID = 0x04;
    private final static int WSID = 0x08;
    private final static int WTMS = 0x10;

    public DltStandardHeader(byte[] header)
    {
        this.header = header;
        setVersion();
    }

    public DltStandardHeader()
    {
        setVersion();
    }

    public void setEcuId(String ecuId)
    {
        ByteArrayHelper.setBytesInRange( 0, 3, this.ecuId, ecuId.getBytes() );
        setHeaderBit( WEID );
    }

    public void setExtendedHeaderBit()
    {
        header[0] |= UEH;
    }

    public void setSessionId(byte[] sessionId)
    {
        ByteArrayHelper.setBytesInRange( 0, 3, this.sessionId, sessionId );
        setHeaderBit( WSID );
    }

    public void setTimestamp(byte[] timestamp)
    {
        ByteArrayHelper.setBytesInRange( 0, 3, this.timestamp, timestamp );
        setHeaderBit( WTMS );
    }

    private void setHeaderBit(int bitmask)
    {
        header[0] |= bitmask;
    }

    private void setVersion()
    {
        header[0] |= 0x20;
    }

    public void setMessageLength(int length)
    {
        if (length > 65535 || length < 0)
        {
            header[2] = 0;
            header[3] = 0;
            log.error( "Message length needs to be in the range 0 <= length <= 65535 (2^16-1)" );
            return;
        }
        header[2] = (byte)(length >> 8 & 0xff);
        header[3] = (byte)(length & 0xff);
    }

    public int getMessageLength()
    {
        return (((header[2] & 0xff) << 8) | (header[3] & 0xff));
    }

    public boolean hasExtendedHeader()
    {
        return ((header[0] & UEH) == UEH);
    }

    public boolean isPayloadInBigEndian()
    {
        return ((header[0] & MSBF) == MSBF);
    }

    public boolean hasSessionId()
    {
        return ((header[0] & WSID) == WSID);
    }

    public boolean hasECUId()
    {
        return ((header[0] & WEID) == WEID);
    }

    public boolean hasTimeStamp()
    {
        return ((header[0] & WTMS) == WTMS);
    }

    byte getMessageCounter()
    {
        return header[1];
    }

    public String getECUId()
    {
        return new String( ecuId, StandardCharsets.US_ASCII );
    }

    public int getSessionId()
    {
        return ByteBuffer.wrap( sessionId ).getInt();
    }

    public int getTimeStamp()
    {
        return ByteBuffer.wrap( timestamp ).getInt() / DLT_TIME_UNITS_IN_MS;
    }

    public byte[] getBytes()
    {
        byte[] returnHeader = header;
        if (hasECUId())
        {
            returnHeader = ByteArrayHelper.appendBytes( returnHeader, ecuId );
        }
        if (hasSessionId())
        {
            returnHeader = ByteArrayHelper.appendBytes( returnHeader, sessionId );
        }
        if (hasTimeStamp())
        {
            returnHeader = ByteArrayHelper.appendBytes( returnHeader, timestamp );
        }
        return returnHeader;
    }

    public short getLength()
    {
        short length = 4;
        if (hasECUId())
        {
            length += 4;
        }
        if (hasSessionId())
        {
            length += 4;
        }
        if (hasTimeStamp())
        {
            length += 4;
        }
        return length;
    }
}
