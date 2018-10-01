/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dbus.decoder.api;

import java.util.List;

import com.elektrobit.ebrace.dbus.decoder.model.DBusMessageDecodedTree;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayloadItem;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;

public class DBusDecodedRuntimeEvent implements DecodedRuntimeEvent
{
    private static final String INVALID_STRING = "Invalid";
    private static final String ERROR_STRING = "Error";
    private static final String BROADCAST_STRING = "Broadcast";
    private static final String REQUEST_STRING = "Request";
    private static final String RESPONSE_STRING = "Response";
    private static final String REPLY_MESSAGE_SERIAL_STRING = "Reply Message Serial";
    private static final String RECEIVER_STRING = "Receiver";
    private static final String SENDER_STRING = "Sender";
    private static final String MESSAGE_SERIAL_STRING = "Message Serial";
    private static final String RESPONSE_DIRECTION_STRING = "<- ";
    private static final String METADATA_STRING = "MetaData";

    private final RuntimeEvent<?> selectedEvent;
    private DecodedNode newNode = null;
    private DecodedTree result;
    private RuntimeEventType runtimeEventType = RuntimeEventType.UNDEFINED;

    public DBusDecodedRuntimeEvent(RuntimeEvent<?> selectedEvent)
    {
        this.selectedEvent = selectedEvent;
        createDecodedTreeForRuntimeEvent( selectedEvent );
    }

    @Override
    public DecodedTree getDecodedTree()
    {
        return result;
    }

    @Override
    public String getSummary()
    {
        return SignatureSummaryBuilder.createSummary( getDecodedTree() );
    }

    @Override
    public RuntimeEventType getRuntimeEventType()
    {
        return runtimeEventType;
    }

    private DecodedTree createDecodedTreeForRuntimeEvent(RuntimeEvent<?> event)
    {
        resetMembers();
        result = new DBusMessageDecodedTree( event.getRuntimeEventChannel().getName() );
        if (event.getValue() instanceof ProtoMessageValue)
        {
            ProtoMessageValue valueObject = (ProtoMessageValue)event.getValue();
            createTreeForEvent( valueObject, result );
        }

        return result;
    }

    private void resetMembers()
    {
        runtimeEventType = RuntimeEventType.UNDEFINED;
    }

    private void createTreeFromDbusMessage(List<DBusMessagePayloadItem> paramValues, DecodedNode parentNode)
    {
        for (DBusMessagePayloadItem item : paramValues)
        {
            if (item.hasCompositeVal())
            {
                handleCompositePayload( parentNode, item );
            }
            else
            {
                if (newNode != null)
                {
                    newNode.setValue( getStringValueForParam( item ) );
                    newNode = null;
                }
                else
                {
                    handleNonCompositeValue( parentNode, item );
                }
            }
        }
    }

    private void handleCompositePayload(DecodedNode parentNode, DBusMessagePayloadItem item)
    {
        DecodedNode node = parentNode.createChildNode( item.getType().toString() );
        createTreeFromDbusMessage( item.getCompositeVal().getParamList(), node );
    }

    private void handleNonCompositeValue(DecodedNode parentNode, DBusMessagePayloadItem item)
    {
        DecodedNode node = parentNode.createChildNode( item.getType().toString() );
        node.setValue( getStringValueForParam( item ) );
    }

    private String getStringValueForParam(DBusMessagePayloadItem dBus)
    {
        String result = null;
        switch (dBus.getType())
        {
            case DBUS_MSG_PARAM_TYPE_BOOLEAN :
                result = String.valueOf( dBus.getIntVal() == 1 );
                break;
            case DBUS_MSG_PARAM_TYPE_DOUBLE :
                result = String.valueOf( dBus.getDoubleVal() );
                break;
            case DBUS_MSG_PARAM_TYPE_INT16 :
            case DBUS_MSG_PARAM_TYPE_INT32 :
            case DBUS_MSG_PARAM_TYPE_INT64 :
                result = String.valueOf( dBus.getIntVal() );
                break;
            case DBUS_MSG_PARAM_TYPE_UINT16 :
            case DBUS_MSG_PARAM_TYPE_UINT32 :
            case DBUS_MSG_PARAM_TYPE_UINT64 :
                result = String.valueOf( dBus.getUintVal() );
                break;
            case DBUS_MSG_PARAM_TYPE_STRING :
                result = String.valueOf( dBus.getStrVal() );
                break;
            case DBUS_MSG_PARAM_TYPE_ARRAY :
                break;
            case DBUS_MSG_PARAM_TYPE_VARIANT :
                break;
            case DBUS_MSG_PARAM_TYPE_DICT_ENTRY :
                break;
            case DBUS_MSG_PARAM_TYPE_BYTE :
                result = String.valueOf( dBus.getIntVal() );
                break;
            default :
                break;
        }
        return result;
    }

    private void createTreeForEvent(ProtoMessageValue valueObject, DecodedTree tree)
    {
        DBusEvtTraceMessage traceMessage = (DBusEvtTraceMessage)valueObject.getValue();
        switch (traceMessage.getHeader().getType())
        {
            case DBUS_MSG_TYPE_METHOD_CALL :
                createTreeForMethodCall( tree, valueObject );
                break;
            case DBUS_MSG_TYPE_METHOD_RETURN :
                createTreeForMethodReturn( tree, valueObject );
                break;
            case DBUS_MSG_TYPE_SIGNAL :
                createTreeForSignal( tree, valueObject );
                break;
            case DBUS_MSG_TYPE_ERROR :
                setRuntimeEventType( RuntimeEventType.ERROR );
                break;
            case DBUS_MSG_TYPE_INVALID :
                setRuntimeEventType( RuntimeEventType.UNDEFINED );
                break;
            default :
                break;
        }
    }

    private void createTreeForMethodCall(DecodedTree tree, ProtoMessageValue valueObject)
    {
        DBusEvtTraceMessage traceMessage = (DBusEvtTraceMessage)valueObject.getValue();
        String interfaceName = getInterfaceNameFromDBusMessage( traceMessage );
        String memberName = getMemberName( traceMessage );

        setRuntimeEventType( RuntimeEventType.REQUEST );
        DecodedNode interfaceNode = tree.getRootNode().createChildNode( interfaceName + "." + memberName );
        createTreeFromDbusMessage( ((DBusEvtTraceMessage)valueObject.getValue()).getPayload().getParamList(),
                                   interfaceNode );

        DecodedNode metaDataNode = tree.getRootNode().createChildNode( METADATA_STRING );
        metaDataNode.createChildNode( DBusDecoderConstants.MESSAGE_TYPE,
                                      getMethotTypeString( traceMessage.getHeader().getType() ) );
        int serial = traceMessage.getHeader().getSerial();
        metaDataNode.createChildNode( MESSAGE_SERIAL_STRING, String.valueOf( serial ) );
        metaDataNode.createChildNode( SENDER_STRING, getSender( traceMessage ) );
        metaDataNode.createChildNode( RECEIVER_STRING, getReceiver( traceMessage ) );
    }

    private void createTreeForMethodReturn(DecodedTree tree, ProtoMessageValue valueObject)
    {
        DBusEvtTraceMessage traceMessage = (DBusEvtTraceMessage)valueObject.getValue();
        String responseInterface = getInterfaceFromVObject( valueObject );
        String responseMethod = getInterfaceMethodFromVObject( valueObject );

        setRuntimeEventType( RuntimeEventType.RESPONSE );
        DecodedNode interfaceNode = tree.getRootNode().createChildNode( responseInterface + "." + responseMethod );
        createTreeFromDbusMessage( ((DBusEvtTraceMessage)valueObject.getValue()).getPayload().getParamList(),
                                   interfaceNode );

        DecodedNode metaDataNode = tree.getRootNode().createChildNode( METADATA_STRING );
        metaDataNode.createChildNode( DBusDecoderConstants.MESSAGE_TYPE,
                                      getMethotTypeString( traceMessage.getHeader().getType() ) );
        int replySerial = traceMessage.getHeader().getReplySerial();
        metaDataNode.createChildNode( REPLY_MESSAGE_SERIAL_STRING, String.valueOf( replySerial ) );
        metaDataNode.createChildNode( SENDER_STRING, getSender( traceMessage ) );
        metaDataNode.createChildNode( RECEIVER_STRING, getReceiver( traceMessage ) );
    }

    private void createTreeForSignal(DecodedTree tree, ProtoMessageValue valueObject)
    {
        DBusEvtTraceMessage traceMessage = (DBusEvtTraceMessage)valueObject.getValue();

        setRuntimeEventType( RuntimeEventType.BROADCAST );
        DecodedNode interfaceNode = tree.getRootNode()
                .createChildNode( getInterfaceNameFromDBusMessage( ((DBusEvtTraceMessage)valueObject.getValue()) ) + "."
                        + getMemberName( (DBusEvtTraceMessage)valueObject.getValue() ) );
        createTreeFromDbusMessage( ((DBusEvtTraceMessage)valueObject.getValue()).getPayload().getParamList(),
                                   interfaceNode );

        DecodedNode metaDataNode = tree.getRootNode().createChildNode( METADATA_STRING );
        metaDataNode.createChildNode( DBusDecoderConstants.MESSAGE_TYPE,
                                      getMethotTypeString( traceMessage.getHeader().getType() ) );
        metaDataNode.createChildNode( SENDER_STRING, getSender( traceMessage ) );
        metaDataNode.createChildNode( RECEIVER_STRING, "" );
    }

    private String getInterfaceFromVObject(ProtoMessageValue vObject)
    {
        String result = RESPONSE_STRING;
        String summary = vObject.getSummary().trim();
        int index = summary.lastIndexOf( RESPONSE_DIRECTION_STRING );
        if (index > -1)
        {
            String interfaceAndMethod = summary.substring( index + RESPONSE_DIRECTION_STRING.length() );
            String[] splitted = interfaceAndMethod.split( "::" );
            result = splitted[0];
        }
        return result;
    }

    private String getInterfaceMethodFromVObject(ProtoMessageValue vObject)
    {
        String result = "method";
        String summary = vObject.getSummary();
        String[] splitted = summary.split( "::" );
        result = splitted[1];
        return result;
    }

    private String getMethotTypeString(DBusTraceMessageType messageType)
    {
        switch (messageType)
        {
            case DBUS_MSG_TYPE_METHOD_CALL :
                return REQUEST_STRING;
            case DBUS_MSG_TYPE_METHOD_RETURN :
                return RESPONSE_STRING;
            case DBUS_MSG_TYPE_SIGNAL :
                return BROADCAST_STRING;
            case DBUS_MSG_TYPE_ERROR :
                return ERROR_STRING;
            case DBUS_MSG_TYPE_INVALID :
                return INVALID_STRING;
            default :
                return null;
        }
    }

    private String getInterfaceNameFromDBusMessage(DBusEvtTraceMessage traceMessage)
    {
        return traceMessage.getHeader().getInterface();
    }

    private String getMemberName(DBusEvtTraceMessage traceMessage)
    {
        return traceMessage.getHeader().getMember();
    }

    private String getReceiver(DBusEvtTraceMessage traceMessage)
    {
        return traceMessage.getHeader().getReceiverProcessName();
    }

    private String getSender(DBusEvtTraceMessage traceMessage)
    {
        return traceMessage.getHeader().getSenderProcessName();
    }

    private void setRuntimeEventType(RuntimeEventType type)
    {
        if (runtimeEventType == RuntimeEventType.UNDEFINED)
        {
            runtimeEventType = type;
        }
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return selectedEvent.getRuntimeEventChannel();
    }

    @Override
    public Object getRuntimeEventValue()
    {
        return selectedEvent.getValue();
    }

    @Override
    public RuntimeEvent<?> getRuntimeEvent()
    {
        return selectedEvent;
    }

}
