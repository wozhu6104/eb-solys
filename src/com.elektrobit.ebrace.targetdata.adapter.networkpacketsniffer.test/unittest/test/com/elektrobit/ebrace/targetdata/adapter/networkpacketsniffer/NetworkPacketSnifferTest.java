/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.dev.test.util.datamanager.TimestampMocker;
import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.NetworkPacketSnifferTAProto.MESSAGE_SOURCE;
import com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.NetworkPacketSnifferTAProto.NetworkPacket;
import com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.internal.NetworkPacketSnifferAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import static org.mockito.Mockito.mock;

public class NetworkPacketSnifferTest
{

    RuntimeEventAcceptor mockedRuntimeEventAcceptor;
    RuntimeEventChannel<String> mockedChannel;
    NetworkPacketSnifferAdapter networkAdaptor;

    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {

        mockedRuntimeEventAcceptor = mock( RuntimeEventAcceptor.class );
        mockedChannel = Mockito.mock( RuntimeEventChannel.class );
        networkAdaptor = new NetworkPacketSnifferAdapter( mockedRuntimeEventAcceptor,
                                                          null,
                                                          null,
                                                          new DataSourceContext( SOURCE_TYPE.FILE, "test." ) );
    }

    @Test
    public void testValidJson()
    {

        NetworkPacket.Builder builder = NetworkPacket.newBuilder();
        String value = "\"id\": 700,\"jsonrpc\": \"2.0\", \"method\": \"MB.registerComponent\",\"params\": { \"componentName\": \"VehicleInfo\"}}";
        builder.setSource( MESSAGE_SOURCE.MESSAGE_SOURCE_WEBSOCK );
        builder.setSrcAddr( "127.0.0.1:1234" );
        builder.setDstAddr( "127.0.0.1:5678" );
        builder.setPduPayload( value );

        Mockito.when( mockedRuntimeEventAcceptor.createOrGetRuntimeEventChannel( "ipc.websockets",
                                                                                 Unit.TEXT,
                                                                                 "websockets data" ) )
                .thenReturn( mockedChannel );

        networkAdaptor.onProtocolMessageReceived( TimestampMocker.mock( 1494242567 ),
                                                  TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_NETWORKPACKETSNIFFER_PLUGIN,
                                                  builder.build().toByteArray(),
                                                  new MockedTimestampCreator() );

        Mockito.verify( mockedRuntimeEventAcceptor ).acceptEventMicros( 1494242567 * 1000L,
                                                                        mockedChannel,
                                                                        null,
                                                                        value );

    }

}
