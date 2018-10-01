/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.connectionhandling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;

public class TargetAgentQueuedMessageImpl implements OutgoingMessage
{

    private final MessageType type;
    private final byte[] msg;
    private final int serializedPayloadSize;
    private final int version;

    public TargetAgentQueuedMessageImpl(byte[] m, MessageType t, int v)
    {
        type = t;
        msg = m;
        version = v;
        serializedPayloadSize = m.length;
    }

    private byte[] getRawMessagePayload()
    {
        return msg;
    }

    private Header createProtocolMessageHeader()
    {
        Header.Builder builder = Header.newBuilder();
        builder.setType( type );
        builder.setLength( serializedPayloadSize );
        builder.setTimestamp( System.currentTimeMillis() );
        builder.setVersionToken( version );
        return builder.build();
    }

    @Override
    public byte[] toByteArray()
    {
        Header header = createProtocolMessageHeader();
        byte headerLength = (byte)header.getSerializedSize();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write( headerLength );
        try
        {
            outputStream.write( header.toByteArray() );
            outputStream.write( getRawMessagePayload() );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

}
