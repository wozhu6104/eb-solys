/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.dbus.decoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayload;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayloadCompositeItem;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayloadItem;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusParamType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;

public class DBusMessageDecodedNodeTest
{
    private DBusDecodedRuntimeEvent dBusDecodedRuntimeEvent;

    @Before
    public void setup()
    {
        @SuppressWarnings("unchecked")
        RuntimeEvent<ProtoMessageValue> runtimeEvent = mock( RuntimeEvent.class );

        DBusMessagePayloadCompositeItem.Builder structCompositeBuilder = DBusMessagePayloadCompositeItem.newBuilder();

        DBusMessagePayloadItem.Builder structBuilder = DBusMessagePayloadItem.newBuilder();
        structBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRUCT );
        structBuilder.setCompositeVal( structCompositeBuilder.build() );

        DBusMessagePayloadCompositeItem.Builder arrayCompositeItemBuilderFirst = DBusMessagePayloadCompositeItem
                .newBuilder();

        DBusMessagePayloadCompositeItem.Builder arrayCompositeItemBuilderSecond = DBusMessagePayloadCompositeItem
                .newBuilder();

        DBusMessagePayloadItem.Builder firstEmptyArrayBuilder = DBusMessagePayloadItem.newBuilder();
        firstEmptyArrayBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_ARRAY );
        firstEmptyArrayBuilder.setCompositeVal( arrayCompositeItemBuilderFirst.build() );

        DBusMessagePayloadItem.Builder secondEmptyArrayBuilder = DBusMessagePayloadItem.newBuilder();
        secondEmptyArrayBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_ARRAY );
        secondEmptyArrayBuilder.setCompositeVal( arrayCompositeItemBuilderSecond.build() );

        DBusMessagePayload.Builder payloadBuilder = DBusMessagePayload.newBuilder();
        payloadBuilder.addParam( structBuilder );
        payloadBuilder.addParam( firstEmptyArrayBuilder.build() );
        payloadBuilder.addParam( secondEmptyArrayBuilder.build() );

        DBusMessageHeader.Builder headerBuilder = DBusMessageHeader.newBuilder();
        headerBuilder.setType( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL );
        headerBuilder.setSender( ":1.202" );
        headerBuilder.setReceiver( "org.genivi.poiservice.POIContentAccess" );
        headerBuilder.setSenderPid( 18659 );
        headerBuilder.setReceiverPid( 18645 );
        headerBuilder.setMember( "AddCategories" );

        DBusEvtTraceMessage.Builder msgBuilder = DBusEvtTraceMessage.newBuilder();
        msgBuilder.setInstance( DBusInstanceType.DBUS_INSTANCE_SESSION_BUS );
        msgBuilder.setHeader( headerBuilder );
        msgBuilder.setPayload( payloadBuilder );
        DBusEvtTraceMessage msg = msgBuilder.build();

        @SuppressWarnings("unchecked")
        RuntimeEventChannel<ProtoMessageValue> channel = mock( RuntimeEventChannel.class );
        when( channel.getName() ).thenReturn( "trace.dbus.sessionbus" );
        when( runtimeEvent.getRuntimeEventChannel() ).thenReturn( channel );
        when( runtimeEvent.getValue() )
                .thenReturn( new ProtoMessageValue( "<- org.genivi.poiservice.POIContentAccess::AddCategories", msg ) );

        dBusDecodedRuntimeEvent = new DBusDecodedRuntimeEvent( runtimeEvent );
    }

    @Test
    public void duplicateEmptyArray() throws Exception
    {
        List<DecodedNode> childList = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode().getChildren();

        assertEquals( 3, childList.get( 0 ).getChildren().size() );
    }

}
