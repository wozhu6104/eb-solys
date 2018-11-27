/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.tokenizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.elektrobit.ebrace.core.datainput.api.DataStreamTokenizer;

public class ByteArrayDelimiterTokenizer extends DataStreamTokenizer
{
    private final ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
    private final byte[] delimiter;

    public ByteArrayDelimiterTokenizer(byte[] delimiter) throws IOException
    {
        this.delimiter = delimiter;
    }

    @Override
    public String getId()
    {
        return this.getClass().getName();
    }

    @Override
    public byte[] readNextMessage() throws IOException
    {
        int nextByte;
        int delimiterPosition = 0;
        byte[] delimiterBuffer = new byte[delimiter.length - 1];
        while ((nextByte = stream.read()) >= 0)
        {
            if (nextByte == delimiter[delimiterPosition])
            {
                if (delimiterPosition < delimiter.length - 1)
                {
                    delimiterBuffer[delimiterPosition] = (byte)nextByte;
                    delimiterPosition++;
                }
                else
                {
                    return bufferToMessage();
                }
            }
            else
            {
                if (delimiterPosition > 0)
                {
                    messageBuffer.write( delimiterBuffer, 0, delimiterPosition );
                    delimiterPosition = 0;
                }
                messageBuffer.write( nextByte );
            }
        }
        // hasNextMessage = false;
        return bufferToMessage();
    }

    private byte[] bufferToMessage()
    {
        byte[] message = messageBuffer.toByteArray();
        messageBuffer.reset();
        return message;
    }
}
