/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetadapter.socketreader;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderInnerMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessageEncoding;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessageType;
import com.elektrobit.ebrace.targetadapter.socketreader.service.SocketReaderMessageParser;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class TargetAgentTraceMessageParserTest
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private Timestamp timestamp;
    private SocketReaderMessageParser socketReaderMessageParser;
    private RuntimeEventChannel<String> traceMessageChannel;
    private RuntimeEventChannel<String> traceConfigChannel;
    private DataSourceContext dataSourceContext;

    @Before
    public void setup() throws Exception
    {
        runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, "test." );

        createTraceMessageChannelMock();
        createTraceConfigChannelMock();

        timestamp = Mockito.mock( Timestamp.class );
        socketReaderMessageParser = new SocketReaderMessageParser( runtimeEventAcceptor, dataSourceContext );
    }

    @SuppressWarnings("unchecked")
    private void createTraceMessageChannelMock()
    {
        traceMessageChannel = Mockito.mock( RuntimeEventChannel.class );
        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( dataSourceContext,
                                                                           "trace.targetagent.messages",
                                                                           Unit.TEXT,
                                                                           "Target-Agent internal trace messages." ) )
                .thenReturn( traceMessageChannel );
    }

    @SuppressWarnings("unchecked")
    private void createTraceConfigChannelMock()
    {
        traceConfigChannel = Mockito.mock( RuntimeEventChannel.class );
        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( dataSourceContext,
                                                                           "trace.targetagent.config",
                                                                           Unit.TEXT,
                                                                           "Target-Agent config file." ) )
                .thenReturn( traceConfigChannel );
    }

    private SocketReaderMessage createTATraceMessage(final String messageData) throws UnsupportedEncodingException
    {
        final String traceMessageAsAscii = new String( messageData.getBytes( "US-ASCII" ) );

        SocketReaderInnerMessage.Builder innerMessageBuilder = SocketReaderInnerMessage.newBuilder();
        innerMessageBuilder.setPortNo( 55555 );
        innerMessageBuilder.setData( traceMessageAsAscii );

        SocketReaderMessage.Builder traceMessageBuilder = SocketReaderTAProto.SocketReaderMessage.newBuilder();
        traceMessageBuilder.setEncoding( SocketReaderMessageEncoding.Encoding_PlainAscii );
        traceMessageBuilder.setMessage( innerMessageBuilder );
        traceMessageBuilder.setType( SocketReaderMessageType.TARGET_AGENT_MESSAGE );

        return traceMessageBuilder.build();
    }

    @Test
    public void isTATraceChannelLazyCreated() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "<config>" ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                 "trace.targetagent.messages",
                                                 Unit.TEXT,
                                                 "Target-Agent internal trace messages." );
    }

    @Test
    public void isTATraceChannelOnlyOnceCreated() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "<config>" ).toByteArray() );
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "</config>" ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                 "trace.targetagent.config",
                                                 Unit.TEXT,
                                                 "Target-Agent config file." );
    }

    @Test
    public void isConfigOnlyOneEvent() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "<config>" ).toByteArray() );
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "</config>" ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .<String> acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                             traceConfigChannel,
                                             ModelElement.NULL_MODEL_ELEMENT,
                                             "<config>\n" + "</config>\n" );
    }

    @Test
    public void isMessageAfterConfigParsedRight() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "<config>" ).toByteArray() );
        socketReaderMessageParser.parseMessage( timestamp, createTATraceMessage( "</config>" ).toByteArray() );
        socketReaderMessageParser.parseMessage( timestamp,
                                                createTATraceMessage( "Some trace Message." ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .<String> acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                             traceConfigChannel,
                                             ModelElement.NULL_MODEL_ELEMENT,
                                             "<config>\n" + "</config>\n" );
        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .<String> acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                             traceMessageChannel,
                                             ModelElement.NULL_MODEL_ELEMENT,
                                             "Some trace Message." );
    }

}
