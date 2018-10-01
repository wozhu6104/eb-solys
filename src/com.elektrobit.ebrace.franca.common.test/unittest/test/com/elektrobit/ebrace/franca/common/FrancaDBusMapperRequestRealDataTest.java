/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.franca.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.franca.common.franca.mapper.api.FrancaDBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayload;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayloadItem;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusParamType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;

public class FrancaDBusMapperRequestRealDataTest
{
    private FrancaDBusDecodedRuntimeEvent francaDecodedEvent;

    @Before
    public void setup()
    {
        DBusApplicationMessage dBusApplicationMessage = DBusApplicationMessage.newBuilder()
                .setTraceMessage( DBusEvtTraceMessage.newBuilder().setHeader( DBusMessageHeader.newBuilder()
                        .setType( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL ).setSender( ":1.202" )
                        .setReceiver( "org.genivi.navigation.poiservice.POIContentAccess" ).setSenderPid( 18659 )
                        .setReceiverPid( 18645 ).setSenderUserId( 1000 ).setReceiverUserId( 1000 )
                        .addReceiverAliasNames( "org.genivi.navigation.poiservice.Configuration" ).setSerial( 3 )
                        .setPath( "/org/genivi/navigation/poiservice/POIContentAccess" )
                        .setInterface( "org.genivi.navigation.poiservice.POIContentAccess" )
                        .setMember( "RegisterContentAccessModule" ).setSenderProcessName( "18659" )
                        .setReceiverProcessName( "18645" ) )
                        .setPayload( DBusMessagePayload.newBuilder()
                                .addParam( DBusMessagePayloadItem.newBuilder()
                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRING )
                                        .setStrVal( "org.genivi.navigation.poiservice.POIContentAccessModuleNavit" ) ) )
                        .setInstance( DBusInstanceType.DBUS_INSTANCE_SESSION_BUS ) )
                .build();

        RuntimeEvent<ProtoMessageValue> runtimeEvent = FrancaDBusMapperTestHelper
                .mockValueInRuntimeEvent( dBusApplicationMessage );

        DBusDecodedRuntimeEvent dBusDecodedRuntimeEvent = new DBusDecodedRuntimeEvent( runtimeEvent );

        francaDecodedEvent = new FrancaDBusDecodedRuntimeEvent( FrancaModelLoaderHelper.loadFrancaDefaultModels(),
                                                                dBusDecodedRuntimeEvent );

    }

    @Test
    public void exactlyTwoMainNodes() throws Exception
    {
        assertTrue( francaDecodedEvent.getDecodedTree().getRootNode().getChildren().size() == 2 );
    }

    @Test
    public void firstNodeIsInterfaceNode() throws Exception
    {
        assertEquals( "org.genivi.navigation.poiservice.POIContentAccess.RegisterContentAccessModule",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 ).getName() );
    }

    @Test
    public void interfaceNodeValueCorrect() throws Exception
    {
        assertEquals( "moduleName",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 ).getChildren().get( 0 )
                              .getName() );
        assertEquals( "org.genivi.navigation.poiservice.POIContentAccessModuleNavit",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 ).getChildren().get( 0 )
                              .getValue() );
    }

    @Test
    public void secondNodeIsMetaDataNode() throws Exception
    {
        assertEquals( "MetaData", francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getName() );
    }

    @Test
    public void metaSerialCorrect() throws Exception
    {
        assertEquals( "Message Serial",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getChildren().get( 1 )
                              .getName() );
        assertEquals( "3",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getChildren().get( 1 )
                              .getValue() );
    }

    @Test
    public void senderCorrect() throws Exception
    {
        assertEquals( "Sender",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getChildren().get( 2 )
                              .getName() );
        assertEquals( "18659",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getChildren().get( 2 )
                              .getValue() );
    }

    @Test
    public void receiverCorrect() throws Exception
    {
        assertEquals( "Receiver",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getChildren().get( 3 )
                              .getName() );
        assertEquals( "18645",
                      francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 ).getChildren().get( 3 )
                              .getValue() );
    }

}
