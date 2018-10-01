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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.dbus.decoder.api.DBusDecoderConstants;
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

public class DBusDecoderMapTest
{

    private DBusDecodedRuntimeEvent dBusDecodedRuntimeEvent;

    @Before
    public void setup()
    {
        @SuppressWarnings("unchecked")
        RuntimeEvent<ProtoMessageValue> runtimeEvent = mock( RuntimeEvent.class );

        DBusMessagePayloadItem.Builder dictKeyBuilder = DBusMessagePayloadItem.newBuilder();
        dictKeyBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_INT32 );
        dictKeyBuilder.setIntVal( 160 );

        DBusMessagePayloadItem.Builder variantTypeValueBuilder = DBusMessagePayloadItem.newBuilder();
        variantTypeValueBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_BYTE );
        variantTypeValueBuilder.setIntVal( 0 );

        DBusMessagePayloadItem.Builder variantValueContentBuilder = DBusMessagePayloadItem.newBuilder();
        variantValueContentBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_DOUBLE );
        variantValueContentBuilder.setDoubleVal( 47.42716650652187 );

        DBusMessagePayloadCompositeItem.Builder variantValueBuilder = DBusMessagePayloadCompositeItem.newBuilder();
        variantValueBuilder.addParam( variantValueContentBuilder );

        DBusMessagePayloadItem.Builder variantBuilder = DBusMessagePayloadItem.newBuilder();
        variantBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_VARIANT );
        variantBuilder.setCompositeVal( variantValueBuilder );

        DBusMessagePayloadCompositeItem.Builder structBuilder = DBusMessagePayloadCompositeItem.newBuilder();
        structBuilder.addParam( variantTypeValueBuilder );
        structBuilder.addParam( variantBuilder );

        DBusMessagePayloadItem.Builder dictValBuilder = DBusMessagePayloadItem.newBuilder();
        dictValBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRUCT );
        dictValBuilder.setCompositeVal( structBuilder );

        DBusMessagePayloadCompositeItem.Builder dictKeyCompositeBuilder = DBusMessagePayloadCompositeItem.newBuilder();
        dictKeyCompositeBuilder.addParam( dictKeyBuilder );
        dictKeyCompositeBuilder.addParam( dictValBuilder );

        DBusMessagePayloadItem.Builder dictEntryPayloadBuilder = DBusMessagePayloadItem.newBuilder();
        dictEntryPayloadBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_DICT_ENTRY );
        dictEntryPayloadBuilder.setCompositeVal( dictKeyCompositeBuilder );

        DBusMessagePayloadCompositeItem.Builder arrayCompositeItemBuilder = DBusMessagePayloadCompositeItem
                .newBuilder();
        arrayCompositeItemBuilder.addParam( dictEntryPayloadBuilder );

        DBusMessagePayloadItem.Builder arrayItemBuilder = DBusMessagePayloadItem.newBuilder();
        arrayItemBuilder.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_ARRAY );
        arrayItemBuilder.setCompositeVal( arrayCompositeItemBuilder.build() );

        DBusMessagePayload.Builder builder = DBusMessagePayload.newBuilder();
        builder.addParam( arrayItemBuilder );

        DBusMessageHeader.Builder headerBuilder = DBusMessageHeader.newBuilder();
        headerBuilder.setType( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN );
        headerBuilder.addSenderAliasNames( "org.genivi.navigationcore.MapMatchedPosition" );

        DBusEvtTraceMessage.Builder msgBuilder = DBusEvtTraceMessage.newBuilder();
        msgBuilder.setInstance( DBusInstanceType.DBUS_INSTANCE_SESSION_BUS );
        msgBuilder.setHeader( headerBuilder );
        msgBuilder.setPayload( builder );
        DBusEvtTraceMessage msg = msgBuilder.build();

        @SuppressWarnings("unchecked")
        RuntimeEventChannel<ProtoMessageValue> channel = mock( RuntimeEventChannel.class );
        when( channel.getName() ).thenReturn( "trace.dbus.sessionbus" );
        when( runtimeEvent.getRuntimeEventChannel() ).thenReturn( channel );
        when( runtimeEvent.getValue() )
                .thenReturn( new ProtoMessageValue( "<- org.genivi.navigationcore.MapMatchedPosition::GetPosition", msg ) );

        dBusDecodedRuntimeEvent = new DBusDecodedRuntimeEvent( runtimeEvent );
    }

    @Test
    public void rootNodeNameEqualsChannelName() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();

        assertEquals( "trace.dbus.sessionbus", rootNode.getName() );
    }

    @Test
    public void firstChildNameEqualsMsgSignature() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();

        assertEquals( "org.genivi.navigationcore.MapMatchedPosition.GetPosition",
                      rootNode.getChildren().get( 0 ).getName() );
    }

    @Test
    public void messageTypeCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode messageTypeNode = rootNode.getChildren().get( 1 ).getChildren().get( 0 );

        assertEquals( DBusDecoderConstants.MESSAGE_TYPE, messageTypeNode.getName() );
        assertEquals( "Response", messageTypeNode.getValue() );
    }

    @Test
    public void isArrayTypeCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode arrayStartNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_ARRAY", arrayStartNode.getName() );
        assertNull( arrayStartNode.getValue() );
    }

    @Test
    public void isDictEntryCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode firstDictEntryNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 ).getChildren().get( 0 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_DICT_ENTRY", firstDictEntryNode.getName() );
        assertNull( firstDictEntryNode.getValue() );
    }

    @Test
    public void isKeyOfFirstDictEntryCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode firstDictEntryNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 ).getChildren().get( 0 )
                .getChildren().get( 0 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_INT32", firstDictEntryNode.getName() );
        assertEquals( "160", firstDictEntryNode.getValue() );
    }

    @Test
    public void isValueStructedCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode firstDictEntryNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 ).getChildren().get( 0 )
                .getChildren().get( 1 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_STRUCT", firstDictEntryNode.getName() );
        assertNull( firstDictEntryNode.getValue() );
    }

    @Test
    public void isVariantTypeCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode variantTypeNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 ).getChildren().get( 0 )
                .getChildren().get( 1 ).getChildren().get( 0 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_BYTE", variantTypeNode.getName() );
        assertEquals( "0", variantTypeNode.getValue() );
    }

    @Test
    public void isVariantCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode variantNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 ).getChildren().get( 0 )
                .getChildren().get( 1 ).getChildren().get( 1 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_VARIANT", variantNode.getName() );
        assertNull( variantNode.getValue() );
    }

    @Test
    public void isVariantValueCorrect() throws Exception
    {
        DecodedNode rootNode = dBusDecodedRuntimeEvent.getDecodedTree().getRootNode();
        DecodedNode variantValueNode = rootNode.getChildren().get( 0 ).getChildren().get( 0 ).getChildren().get( 0 )
                .getChildren().get( 1 ).getChildren().get( 1 ).getChildren().get( 0 );

        assertEquals( "DBUS_MSG_PARAM_TYPE_DOUBLE", variantValueNode.getName() );
        assertEquals( "47.42716650652187", variantValueNode.getValue() );
    }

}
