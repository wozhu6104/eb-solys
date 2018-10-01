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

import org.apache.commons.codec.binary.Base64;
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

public class SocketReaderMessageParserTest
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private Timestamp timestamp;
    private String message;
    private SocketReaderMessageParser socketReaderMessageParser;
    private RuntimeEventChannel<String> socketReaderChannel;
    private DataSourceContext dataSourceContext;

    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        socketReaderChannel = Mockito.mock( RuntimeEventChannel.class );
        runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, "test" );
        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( dataSourceContext,
                                                                           "trace.socketreaderdata",
                                                                           Unit.TEXT,
                                                                           "Raw data of socket." ) )
                .thenReturn( socketReaderChannel );
        timestamp = Mockito.mock( Timestamp.class );
        message = "MyMessage";
        socketReaderMessageParser = new SocketReaderMessageParser( runtimeEventAcceptor, dataSourceContext );
    }

    private SocketReaderMessage createBase64SocketReaderMessage(final String messageData)
    {
        final byte[] bytesEncoded = Base64.encodeBase64( messageData.getBytes() );
        final String traceMessageAsUtf8 = new String( bytesEncoded );

        SocketReaderInnerMessage.Builder innerMessageBuilder = SocketReaderInnerMessage.newBuilder();
        innerMessageBuilder.setPortNo( 55555 );
        innerMessageBuilder.setData( traceMessageAsUtf8 );

        SocketReaderMessage.Builder traceMessageBuilder = SocketReaderTAProto.SocketReaderMessage.newBuilder();
        traceMessageBuilder.setEncoding( SocketReaderMessageEncoding.Encoding_Base64 );
        traceMessageBuilder.setMessage( innerMessageBuilder );
        traceMessageBuilder.setType( SocketReaderMessageType.SOCKET_READER_MESSAGE );

        return traceMessageBuilder.build();
    }

    private SocketReaderMessage createPlainSocketReaderMessage(final String messageData)
            throws UnsupportedEncodingException
    {
        final String traceMessageAsAscii = new String( messageData.getBytes( "US-ASCII" ) );

        SocketReaderInnerMessage.Builder innerMessageBuilder = SocketReaderInnerMessage.newBuilder();
        innerMessageBuilder.setPortNo( 55555 );
        innerMessageBuilder.setData( traceMessageAsAscii );

        SocketReaderMessage.Builder traceMessageBuilder = SocketReaderTAProto.SocketReaderMessage.newBuilder();
        traceMessageBuilder.setEncoding( SocketReaderMessageEncoding.Encoding_PlainAscii );
        traceMessageBuilder.setMessage( innerMessageBuilder );
        traceMessageBuilder.setType( SocketReaderMessageType.SOCKET_READER_MESSAGE );

        return traceMessageBuilder.build();
    }

    @Test
    public void isSocketReaderChannelLazyCreated() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createBase64SocketReaderMessage( message ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                 "trace.socketreaderdata",
                                                 Unit.TEXT,
                                                 "Raw data of socket." );
    }

    @Test
    public void isSocketReaderChannelOnlyOnceCreated() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createBase64SocketReaderMessage( message ).toByteArray() );
        socketReaderMessageParser.parseMessage( timestamp, createBase64SocketReaderMessage( message ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .createOrGetRuntimeEventChannel( dataSourceContext,
                                                 "trace.socketreaderdata",
                                                 Unit.TEXT,
                                                 "Raw data of socket." );
    }

    @Test
    public void isBase64EventInSocketReaderChannelCreated() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createBase64SocketReaderMessage( message ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .<String> acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                             socketReaderChannel,
                                             ModelElement.NULL_MODEL_ELEMENT,
                                             message );
    }

    @Test
    public void isPlainEventInSocketReaderChannelCreated() throws Exception
    {
        socketReaderMessageParser.parseMessage( timestamp, createPlainSocketReaderMessage( message ).toByteArray() );

        Mockito.verify( runtimeEventAcceptor, Mockito.times( 1 ) )
                .<String> acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                             socketReaderChannel,
                                             ModelElement.NULL_MODEL_ELEMENT,
                                             message );
    }

}
