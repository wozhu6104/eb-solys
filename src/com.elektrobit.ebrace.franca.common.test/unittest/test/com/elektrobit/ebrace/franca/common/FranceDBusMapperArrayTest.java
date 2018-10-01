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
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.franca.common.franca.mapper.api.FrancaDBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
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
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;

public class FranceDBusMapperArrayTest
{

    private FrancaDBusDecodedRuntimeEvent decodedRuntimeEvent;
    private DecodedNode addressNode;

    @Before
    public void setup()
    {
// @formatter:off
// Relevant part:
//     traceMessage {
//            header {
//              type: DBUS_MSG_TYPE_METHOD_RETURN
//            }
//      payload {
//      param {
//        type: DBUS_MSG_PARAM_TYPE_STRUCT
//        composite_val {
//          param {
//            type: DBUS_MSG_PARAM_TYPE_STRING
//            str_val: "John"
//          }
//          param {
//            type: DBUS_MSG_PARAM_TYPE_STRING
//            str_val: "Smith"
//          }
//          param {
//            type: DBUS_MSG_PARAM_TYPE_STRING
//            str_val: "0000000000"
//          }
//          param {
//            type: DBUS_MSG_PARAM_TYPE_ARRAY
//            composite_val {
//              param {
//                type: DBUS_MSG_PARAM_TYPE_STRUCT
//                composite_val {
//                  param {
//                    type: DBUS_MSG_PARAM_TYPE_UINT32
//                    uint_val: 0
//                  }
//                  param {
//                    type: DBUS_MSG_PARAM_TYPE_STRING
//                    str_val: "one"
//                  }
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//    instance: DBUS_INSTANCE_SESSION_BUS
// @formatter:on

        DBusApplicationMessage dbusMessage = DBusApplicationMessage.newBuilder().setTraceMessage( DBusEvtTraceMessage
                .newBuilder()
                .setHeader( DBusMessageHeader.newBuilder().setType( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN ) )
                .setPayload( DBusMessagePayload.newBuilder().addParam( DBusMessagePayloadItem
                        .newBuilder().setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRUCT )
                        .setCompositeVal( DBusMessagePayloadCompositeItem
                                .newBuilder()
                                .addParam( DBusMessagePayloadItem
                                        .newBuilder().setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRING )
                                        .setStrVal( "John" ) )
                                .addParam( DBusMessagePayloadItem.newBuilder()
                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRING ).setStrVal( "Smith" ) )
                                .addParam( DBusMessagePayloadItem.newBuilder()
                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRING ).setStrVal( "0000000000" ) )
                                .addParam( DBusMessagePayloadItem.newBuilder()
                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_ARRAY ).setCompositeVal( DBusMessagePayloadCompositeItem
                                                .newBuilder()
                                                .addParam( DBusMessagePayloadItem.newBuilder()
                                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRUCT ).setCompositeVal( DBusMessagePayloadCompositeItem
                                                                .newBuilder()
                                                                .addParam( DBusMessagePayloadItem.newBuilder()
                                                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_UINT32 ).setUintVal( 0 ) )
                                                                .addParam( DBusMessagePayloadItem.newBuilder()
                                                                        .setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRING ).setStrVal( "one" ) ) ) ) ) ) ) ) )
                .setInstance( DBusInstanceType.DBUS_INSTANCE_SESSION_BUS ) ).build();

        RuntimeEvent<ProtoMessageValue> runtimeEvent = FrancaDBusMapperTestHelper
                .mockValueInRuntimeEvent( " <- showcase.geniviamm.Contacts::getContact", dbusMessage );

        DBusDecodedRuntimeEvent dBusDecodedRuntimeEvent = new DBusDecodedRuntimeEvent( runtimeEvent );

        decodedRuntimeEvent = new FrancaDBusDecodedRuntimeEvent( FrancaModelLoaderHelper.loadFrancaDefaultModels(),
                                                                 dBusDecodedRuntimeEvent );

        addressNode = decodedRuntimeEvent.getDecodedTree().getRootNode().getChildren().get( 0 ).getChildren().get( 0 )
                .getChildren().get( 3 );
    }

    @Test
    public void isArrayMainNodeCorrect() throws Exception
    {
        assertEquals( "address", addressNode.getName() );
    }

    @Test
    public void isFirstArrayEntryCorrect() throws Exception
    {
        DecodedNode firstAddressItemNode = addressNode.getChildren().get( 0 );
        assertEquals( "AddressItem[0]", firstAddressItemNode.getName() );
        assertNull( addressNode.getValue() );
    }

    @Test
    public void isFirstArrayEntryAdressItemFieldCorrect() throws Exception
    {
        DecodedNode firstAddressItemFieldNode = addressNode.getChildren().get( 0 ).getChildren().get( 0 );
        assertEquals( "key", firstAddressItemFieldNode.getName() );
        assertEquals( "COUNTRY", firstAddressItemFieldNode.getValue() );
    }

    @Test
    public void isSecondArrayEntryAdressItemFieldCorrect() throws Exception
    {
        DecodedNode sndAddressItemFieldNode = addressNode.getChildren().get( 0 ).getChildren().get( 1 );
        assertEquals( "value", sndAddressItemFieldNode.getName() );
        assertEquals( "one", sndAddressItemFieldNode.getValue() );
    }

}
