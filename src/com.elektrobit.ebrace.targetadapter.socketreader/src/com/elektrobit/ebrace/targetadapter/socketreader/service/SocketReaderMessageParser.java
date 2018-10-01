/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.socketreader.service;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessageEncoding;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.protobuf.InvalidProtocolBufferException;

public class SocketReaderMessageParser
{
    private static final Logger LOG = Logger.getLogger( SocketReaderMessageParser.class );

    private final RuntimeEventAcceptor runtimeEventAcceptor;

    private RuntimeEventChannel<String> targetAgentMessagesChannel;
    private RuntimeEventChannel<String> targetAgentConfigChannel;
    private RuntimeEventChannel<String> socketReaderDataChannel;

    private boolean configFileReceivingActive = false;
    private String configMessage = "";

    private final DataSourceContext dataSourceContext;

    public SocketReaderMessageParser(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
    }

    public void parseMessage(Timestamp timestamp, byte[] payload)
    {

        SocketReaderMessage message = null;
        try
        {
            message = SocketReaderTAProto.SocketReaderMessage.parseFrom( payload );
        }
        catch (InvalidProtocolBufferException e)
        {
            LOG.warn( "SocketReader's-Message is corrupted!" );
        }

        /*
         * Socket Reader is used for internal Target Agent trace messages. After start of Target Agent the Target Agent
         * config file is sent as a chain of messages. The config file is only sent once.
         */
        if (message.getType() == SocketReaderMessageType.TARGET_AGENT_MESSAGE)
        {
            initTAChannelsIfNeeded();

            if (isFirstPartOfConfigFile( message ))
            {
                configFileReceivingActive = true;
            }

            if (configFileReceivingActive)
            {
                configMessage += message.getMessage().getData() + "\n";
                if (isLastPartOfConfigFile( message ))
                {
                    configFileReceivingActive = false;
                    runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                            targetAgentConfigChannel,
                                                            ModelElement.NULL_MODEL_ELEMENT,
                                                            configMessage );
                }
            }
            else
            {
                runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                        targetAgentMessagesChannel,
                                                        ModelElement.NULL_MODEL_ELEMENT,
                                                        message.getMessage().getData() );
            }

        }
        else if (message.getType() == SocketReaderMessageType.SOCKET_READER_MESSAGE)
        {
            initSocketReaderChannelsIfNeeded();
            String messageValue = null;
            if (message.getEncoding() == SocketReaderMessageEncoding.Encoding_PlainAscii)
            {
                try
                {
                    messageValue = new String( message.getMessage().getData().getBytes( "US-ASCII" ) );
                }
                catch (UnsupportedEncodingException e)
                {
                    LOG.warn( "Couldn't parse message " + message.getMessage().getData() + " with encoding US-ASCII." );
                }
            }
            else if (message.getEncoding() == SocketReaderMessageEncoding.Encoding_Base64)
            {
                final byte[] bytesdecoded = message.getMessage().getData().getBytes();
                final byte[] bytesEncoded = Base64.decodeBase64( bytesdecoded );
                messageValue = new String( bytesEncoded );

            }

            if (messageValue != null)
            {
                runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                        socketReaderDataChannel,
                                                        ModelElement.NULL_MODEL_ELEMENT,
                                                        messageValue );
            }
        }

    }

    private void initTAChannelsIfNeeded()
    {
        if (targetAgentMessagesChannel == null && targetAgentConfigChannel == null)
        {
            targetAgentMessagesChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext,
                                                     "trace.targetagent.messages",
                                                     Unit.TEXT,
                                                     "Target-Agent internal trace messages." );

            targetAgentConfigChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext,
                                                     "trace.targetagent.config",
                                                     Unit.TEXT,
                                                     "Target-Agent config file." );
        }
    }

    private void initSocketReaderChannelsIfNeeded()
    {
        if (socketReaderDataChannel == null)
        {
            socketReaderDataChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext,
                                                     "trace.socketreaderdata",
                                                     Unit.TEXT,
                                                     "Raw data of socket." );
        }
    }

    private boolean isFirstPartOfConfigFile(SocketReaderMessage message)
    {
        return message.getMessage().getData().trim().toLowerCase().startsWith( "<config>" );
    }

    private boolean isLastPartOfConfigFile(SocketReaderMessage message)
    {
        return message.getMessage().getData().trim().toLowerCase().endsWith( "</config>" );
    }

}
