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

import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.google.protobuf.InvalidProtocolBufferException;

public class TargetAgentMessageReader implements MessageReader<ProtoMsgContainer>
{
    @Override
    public ProtoMsgContainer readNextMessage(BytesFromStreamReader bytesReader)
    {

        byte[] headerLegthByte = bytesReader.readNBytes( 1 );
        final int headerLength = headerLegthByte[0];

        final byte[] headerRaw = bytesReader.readNBytes( headerLength );
        if (headerRaw == null)
        {
            return null;
        }
        Header header;

        try
        {
            header = Header.parseFrom( headerRaw );
        }
        catch (InvalidProtocolBufferException e)
        {
            e.printStackTrace();
            return null;
        }

        final byte[] payloadRaw = bytesReader.readNBytes( header.getLength() );
        if (payloadRaw == null)
        {
            return null;
        }

        return new ProtoMsgContainer( header, payloadRaw );
    }
}
