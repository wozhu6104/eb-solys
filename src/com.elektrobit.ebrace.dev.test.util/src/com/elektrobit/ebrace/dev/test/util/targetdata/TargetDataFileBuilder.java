/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.targetdata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.MetaData;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrameOld.OldHeader;
import com.google.protobuf.AbstractMessageLite;

import lombok.Data;

public class TargetDataFileBuilder
{
    public static String PATH;

    private final List<ProtoMsg> messages = new ArrayList<ProtoMsg>();
    private final int protocolVersion;
    private final boolean oldHeader;

    public TargetDataFileBuilder(int protocolVersion)
    {
        this( protocolVersion, false );
    }

    public TargetDataFileBuilder(int protocolVersion, boolean oldHeader)
    {
        this.protocolVersion = protocolVersion;
        this.oldHeader = oldHeader;

        try
        {
            PATH = createTempDirectory().getAbsolutePath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public TargetDataFileBuilder addProtoMessage(long timestamp, MessageType messageType, byte[] data)
    {
        messages.add( new ProtoMsg( timestamp, messageType, data ) );
        return this;
    }

    public TargetDataFileBuilder addProtoMessage(long timestamp, MessageType messageType, String systemValueChannelName,
            String systemValue, byte[] data)
    {
        ProtoMsg protoMsg = new ProtoMsg( timestamp, messageType, data );
        protoMsg.setMetaDataKey( systemValueChannelName );
        protoMsg.setMetaDataValue( systemValue );
        messages.add( protoMsg );
        return this;
    }

    public File createFile(String fileName)
    {
        SimpleFileWriter fileWriter = new SimpleFileWriter();
        fileWriter.startNewFile( PATH, fileName );

        for (ProtoMsg protoMsg : messages)
        {
            if (protoMsg.metaDataKey == null)
                writeMessage( fileWriter, protoMsg.timestamp, protoMsg.messageType, protoMsg.data, protocolVersion );
            else
                writeMessage( fileWriter,
                              protoMsg.timestamp,
                              protoMsg.messageType,
                              protoMsg.data,
                              protocolVersion,
                              protoMsg.metaDataKey,
                              protoMsg.metaDataValue );
        }

        fileWriter.closeStream();

        String smallFile = PATH + "/" + fileName;
        return new File( smallFile );
    }

    private void writeMessage(SimpleFileWriter fileWriter, long timestamp, MessageType messageType,
            byte[] messageContentBytes, int procolVersion)
    {
        writeMessage( fileWriter, timestamp, messageType, messageContentBytes, procolVersion, null, null );
    }

    private void writeMessage(SimpleFileWriter fileWriter, long timestamp, MessageType messageType,
            byte[] messageContentBytes, int procolVersion, String systemValueChannelName, String cpuValue)
    {
        byte[] headerAsByteArray;
        AbstractMessageLite header;

        if (oldHeader)
        {
            OldHeader.Builder builder = OldHeader.newBuilder().setTimestamp( timestamp ).setType( messageType )
                    .setLength( messageContentBytes.length );
            header = builder.build();
        }
        else
        {
            Header.Builder builder = Header.newBuilder().setTimestamp( timestamp ).setType( messageType )
                    .setLength( messageContentBytes.length ).setVersionToken( procolVersion );

            if (systemValueChannelName != null && !systemValueChannelName.isEmpty())
            {
                MetaData.Builder systemValueBuilder = MetaData.newBuilder().setKey( systemValueChannelName )
                        .setValue( cpuValue );

                builder.addMetaDataInfo( systemValueBuilder );
            }

            header = builder.build();
        }

        headerAsByteArray = header.toByteArray();
        byte[] lengthByteArray = new byte[]{(byte)headerAsByteArray.length};

        fileWriter.writeBytes( lengthByteArray );
        fileWriter.writeBytes( headerAsByteArray );
        fileWriter.writeBytes( messageContentBytes );
    }

    public File createTempDirectory() throws IOException
    {
        final File temp;

        temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );

        if (!(temp.delete()))
        {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if (!(temp.mkdir()))
        {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        return temp;
    }

    @Data
    private class ProtoMsg
    {
        public final long timestamp;
        public final MessageType messageType;
        public String metaDataKey;
        public String metaDataValue;
        public final byte[] data;

    }
}
