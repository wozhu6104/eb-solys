/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;

public class DltMessageWithStorageHeaderParser implements MessageReader<DltMessage>
{
    final static int STANDARD_HEADER_SIZE = 4;
    final static int EXTENDED_HEADER_SIZE = 10;
    final static byte[] expectedPattern = new byte[]{68, 76, 84, 1};

    private final DltStreamMessageServiceImpl withoutStorageHeaderParser;

    public DltMessageWithStorageHeaderParser(DltChannelFromLogInfoCreator channelFromLogInfoCreator)
    {
        withoutStorageHeaderParser = new DltStreamMessageServiceImpl( channelFromLogInfoCreator );
    }

    @Override
    public DltMessage readNextMessage(BytesFromStreamReader bytesReader)
    {
        try
        {
            return readNextMessageUnChecked( bytesReader );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public DltMessage readNextMessageUnChecked(BytesFromStreamReader bytesReader)
            throws IOException, DltMessageParseException
    {
        DltMessage dltMsg = new DltMessage();

        if (locateNextStorageHeader( bytesReader ))
        {
            dltMsg = withoutStorageHeaderParser.readNextMessage( bytesReader );
        }
        else
        {
            dltMsg = null;
        }

        return dltMsg;
    }

    private boolean locateNextStorageHeader(BytesFromStreamReader bytesReader) throws IOException
    {
        boolean headerFound = false;
        boolean eofReached = false;
        List<Byte> dropList = new ArrayList<Byte>();
        byte[] asciiChar = new byte[1];
        int patternIt = 0;

        while (!headerFound)
        {
            asciiChar = bytesReader.readNBytes( 1 );
            eofReached = bytesReader.isEndOfStream();
            if (eofReached)
            {
                break;
            }

            if (isExpectedCharFound( asciiChar[0], expectedPattern[patternIt], dropList ))
            {
                patternIt++;
            }
            else
            {
                patternIt = 0;
            }
            if (patternIt == 4)
            {
                headerFound = true;
                if (dropList.size() > 0)
                {
                    System.out.println( "Following content was dropped" + dropList );
                }
                bytesReader.readNBytes( 12 );
                eofReached = bytesReader.isEndOfStream();
            }
        }
        return !eofReached;
    }

    private boolean isExpectedCharFound(byte actual, int expected, List<Byte> dropList) throws IOException
    {
        boolean retVal = false;

        if (actual == expected)
        {
            retVal = true;
        }
        else
        {
            dropList.add( actual );
        }
        return retVal;
    }
}
