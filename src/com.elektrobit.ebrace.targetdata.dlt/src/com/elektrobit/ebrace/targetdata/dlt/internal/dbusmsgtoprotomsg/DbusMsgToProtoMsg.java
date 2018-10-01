/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.dbusmsgtoprotomsg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.freedesktop.dbus.Message;
import org.freedesktop.dbus.MessageReader;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.UInt64;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayload;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayloadCompositeItem;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessagePayloadItem;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusParamType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltSegmentedNetworkMessage;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

import lombok.extern.log4j.Log4j;

@Log4j
public class DbusMsgToProtoMsg
{

    private final TimestampProvider tsProvider;
    private final ProtocolMessageDispatcher protocolMessageDispatcher;
    private final String filename;

    public DbusMsgToProtoMsg(TimestampProvider tsProvider, ProtocolMessageDispatcher protocolMessageDispatcher,
            String filename)
    {
        this.tsProvider = tsProvider;
        this.protocolMessageDispatcher = protocolMessageDispatcher;
        this.filename = filename;
    }

    public DBusApplicationMessage dbusMsgToProtoMsg(Message message) throws DBusException
    {
        DBusMessageHeader.Builder traceMessageHeader = DBusMessageHeader.newBuilder();
        DBusMessagePayload.Builder traceMessagePayload = DBusMessagePayload.newBuilder();
        populateHeader( message, traceMessageHeader );
        DBusEvtTraceMessage.Builder traceMessage = DBusEvtTraceMessage.newBuilder().setHeader( traceMessageHeader )
                .setInstance( DBusInstanceType.DBUS_INSTANCE_SESSION_BUS );

        if (populatePayload( message, traceMessagePayload ))
        {
            traceMessage.setPayload( traceMessagePayload );
        }
        else
        {
            return null;
        }

        DBusApplicationMessage.Builder applicationMsg = DBusApplicationMessage.newBuilder()
                .setTraceMessage( traceMessage );
        return applicationMsg.build();
    }

    private void populateHeader(Message message, DBusMessageHeader.Builder header)
    {
        setDestination( message, header );

        setPath( message, header );

        setInterface( message, header );

        setSender( message, header );

        setSerial( message, header );

        setSignature( message, header );

        setReplySerial( message, header );

        setMember( message, header );

        setType( message, header );
    }

    private void setType(Message message, DBusMessageHeader.Builder header)
    {
        String msgType = message.getClass().getSimpleName();
        if (msgType != null && !msgType.isEmpty())
        {
            if (msgType.equals( "DBusSignal" ))
            {
                header.setType( DBusTraceMessageType.DBUS_MSG_TYPE_SIGNAL );
            }
            else if (msgType.equals( "MethodCall" ))
            {
                header.setType( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL );
            }
            else if (msgType.equals( "MethodReturn" ))
            {
                header.setType( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN );
            }
            else
            {
                header.setType( DBusTraceMessageType.DBUS_MSG_TYPE_INVALID );
            }
        }
    }

    private void setMember(Message message, DBusMessageHeader.Builder header)
    {
        String msgName = message.getName();
        if (msgName != null && !msgName.isEmpty())
        {
            header.setMember( msgName );
        }
    }

    private void setReplySerial(Message message, DBusMessageHeader.Builder header)
    {
        long replySerial = message.getReplySerial();

        header.setReplySerial( (int)replySerial );
    }

    private void setSignature(Message message, DBusMessageHeader.Builder header)
    {
        String msgSignature = message.getSig();

        if (msgSignature != null && !msgSignature.isEmpty())
        {
            header.setMethodSignature( msgSignature );
        }
    }

    private void setSerial(Message message, DBusMessageHeader.Builder header)
    {
        long serial = message.getSerial();

        header.setSerial( (int)serial );
    }

    private void setSender(Message message, DBusMessageHeader.Builder header)
    {
        String msgSource = message.getSource();
        if (msgSource != null && !msgSource.isEmpty())
        {
            header.setSender( msgSource );
        }
    }

    private void setInterface(Message message, DBusMessageHeader.Builder header)
    {
        String msgInterface = message.getInterface();
        if (msgInterface != null && !msgInterface.isEmpty())
        {
            header.setInterface( msgInterface );
        }
    }

    private void setPath(Message message, DBusMessageHeader.Builder header)
    {
        String path = message.getPath();
        if (path != null && !path.isEmpty())
        {
            header.setPath( path );
        }
    }

    private void setDestination(Message message, DBusMessageHeader.Builder header)
    {
        String destination = message.getDestination();
        if (destination != null && !destination.isEmpty())
        {
            header.setReceiver( destination );
        }
    }

    private boolean populatePayload(Message message, DBusMessagePayload.Builder payload)
    {
        boolean retVal = true;
        Object[] args = null;
        try
        {
            args = message.getParameters();

        }
        catch (DBusException ex)
        {
            retVal = false;
            ex.printStackTrace();
        }
        catch (StringIndexOutOfBoundsException ex)
        {
            retVal = false;
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            ex.printStackTrace();
            retVal = false;
        }
        catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
            retVal = false;
        }
        catch (ExceptionInInitializerError | NoClassDefFoundError ex)
        {
            ex.printStackTrace();
            retVal = false;
        }
        if (null != args && 0 != args.length)
        {
            for (Object item : args)
            {
                DBusMessagePayloadItem.Builder itemBuilder = DBusMessagePayloadItem.newBuilder();
                if (parsePayloadItem( item, itemBuilder ))
                {
                    payload.addParam( itemBuilder );

                }

            }
        }

        return retVal;
    }

    private boolean parsePayloadItem(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;

        if (storeIntegralType( param, item ) || storeFpNumber( param, item ) || storeString( param, item )
                || storeStruct( param, item ) || storeMap( param, item ) || storeVariant( param, item )
                || storeVector( param, item ))
        {
            return retVal;
        }
        else
        {
            retVal = false;
        }

        return retVal;

    }

    private boolean storeIntegralType(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;

        if (param instanceof Byte)
        {
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_BYTE, 0 );
        }
        else if (param instanceof Boolean)
        {
            int boolCorespondent = ((Boolean)param) ? 1 : 0;
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_BOOLEAN, boolCorespondent );
        }
        else if (param instanceof Short)
        {
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_INT16, ((short)param) );
        }
        else if (param instanceof UInt16)
        {
            UInt16 aux = (UInt16)param;
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_INT16, aux.intValue() );
        }
        else if (param instanceof Integer)
        {
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_INT32, (int)param );
        }
        else if (param instanceof UInt32)
        {
            UInt32 aux = (UInt32)param;
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_INT32, aux.intValue() );
        }
        else if (param instanceof UInt64)
        {
            UInt64 aux = (UInt64)param;
            storeItem( item, DBusParamType.DBUS_MSG_PARAM_TYPE_INT32, aux.intValue() );
        }
        else if (param instanceof Long)
        {
            Long aux = (Long)param;
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_INT64 );
            item.setIntVal( aux.longValue() );
        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    private boolean storeFpNumber(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;
        if (param instanceof Double)
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_DOUBLE );
            item.setDoubleVal( (double)param );
        }
        else if (param instanceof Float)
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_DOUBLE );
            item.setDoubleVal( (double)param );
        }
        else
        {
            retVal = false;
        }

        return retVal;
    }

    private boolean storeString(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;
        if (param instanceof String)
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRING );
            item.setStrVal( (String)param );
        }
        else if (param instanceof Path)
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_OBJ_PATH );
            item.setStrVal( (String)param );
        }
        else if (param.getClass().getCanonicalName().equals( "org.freedesktop.dbus.ObjectPath" ))
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_OBJ_PATH );
            item.setStrVal( param.toString() );
        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    private boolean storeMap(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;
        if (param instanceof Map)
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_DICT_ENTRY );

            @SuppressWarnings("unchecked")
            Map<Object, Object> tempMap = (Map<Object, Object>)param;

            for (Map.Entry<Object, Object> entry : tempMap.entrySet())
            {
                DBusMessagePayloadItem.Builder keyBuilder = DBusMessagePayloadItem.newBuilder();
                DBusMessagePayloadItem.Builder valueBuilder = DBusMessagePayloadItem.newBuilder();

                DBusMessagePayloadCompositeItem.Builder compositeVal = DBusMessagePayloadCompositeItem.newBuilder();
                parsePayloadItem( entry.getKey(), keyBuilder );
                parsePayloadItem( entry.getValue(), valueBuilder );
                compositeVal.addParam( keyBuilder );
                compositeVal.addParam( valueBuilder );
                item.setCompositeVal( compositeVal );
            }

        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    private boolean storeStruct(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = false;
        param = retrieveStructure( param );
        if (param != null)
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_STRUCT );
            DBusMessagePayloadCompositeItem.Builder compositeVal = DBusMessagePayloadCompositeItem.newBuilder();
            int length = Array.getLength( param );
            for (int i = 0; i < length; i++)
            {
                Object arrayElement = Array.get( param, i );
                DBusMessagePayloadItem.Builder itemBuilder = DBusMessagePayloadItem.newBuilder();
                if (parsePayloadItem( arrayElement, itemBuilder ))
                {
                    compositeVal.addParam( itemBuilder );
                }
            }
            item.setCompositeVal( compositeVal );
            retVal = true;
        }
        return retVal;
    }

    private Object retrieveStructure(Object param)
    {
        final String structID = "STRUCTDATATYPEFLAG";
        if (!(param instanceof Object[]))
        {
            return null;
        }
        Object[] tempVector = (Object[])param;

        if (!(tempVector.length > 0))
        {
            return null;
        }

        Object element = tempVector[tempVector.length - 1];
        if (element instanceof String && ((String)element).equals( structID ))
        {
            tempVector = ArrayUtils.removeElement( tempVector, structID );
            return tempVector;
        }

        return null;

    }

    private boolean storeVariant(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;
        if (param instanceof Variant)
        {

            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_VARIANT );

            @SuppressWarnings("rawtypes")
            Variant tempVAriant = (Variant)param;
            DBusMessagePayloadCompositeItem.Builder compositeVal = DBusMessagePayloadCompositeItem.newBuilder();
            DBusMessagePayloadItem.Builder keyBuilder = DBusMessagePayloadItem.newBuilder();
            parsePayloadItem( tempVAriant.getValue(), keyBuilder );

            compositeVal.addParam( keyBuilder );
            item.setCompositeVal( compositeVal );

        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    private void storeItem(DBusMessagePayloadItem.Builder item, DBusParamType dbusType, int value)
    {
        item.setType( dbusType );
        item.setIntVal( value );
    }

    private boolean storeVector(Object param, DBusMessagePayloadItem.Builder item)
    {
        boolean retVal = true;
        if (param instanceof Vector)
        {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Vector<Object> tempVector = (Vector)param;
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_ARRAY );
            Iterator<Object> it = tempVector.iterator();
            DBusMessagePayloadCompositeItem.Builder compositeVal = DBusMessagePayloadCompositeItem.newBuilder();

            while (it.hasNext())
            {
                DBusMessagePayloadItem.Builder itemBuilder = DBusMessagePayloadItem.newBuilder();
                if (parsePayloadItem( it.next(), itemBuilder ))
                {
                    compositeVal.addParam( itemBuilder );
                }

            }
            item.setCompositeVal( compositeVal );
        }
        else if (param instanceof int[] || param instanceof short[] || param instanceof long[]
                || param instanceof byte[] || param instanceof Object[])
        {
            item.setType( DBusParamType.DBUS_MSG_PARAM_TYPE_ARRAY );
            DBusMessagePayloadCompositeItem.Builder compositeVal = DBusMessagePayloadCompositeItem.newBuilder();
            int length = Array.getLength( param );
            for (int i = 0; i < length; i++)
            {
                Object arrayElement = Array.get( param, i );
                DBusMessagePayloadItem.Builder itemBuilder = DBusMessagePayloadItem.newBuilder();
                if (parsePayloadItem( arrayElement, itemBuilder ))
                {
                    compositeVal.addParam( itemBuilder );
                }
            }
            item.setCompositeVal( compositeVal );
        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    public boolean parseDbusMessage(DltMessage dltMsg)
    {
        boolean retVal = true;
        String inputString = null;
        try
        {
            inputString = retrieveMessageAsString( dltMsg );
            if (inputString != null)
            {
                InputStream stream = convertDltMsgToInputStream( inputString );
                if (stream != null)
                {
                    Message message = new MessageReader( stream ).readMessage();
                    if (null != message)
                    {
                        retVal = retVal && dispatchMsgToDbusAdaptor( dltMsg, message );
                    }
                    else
                    {
                        retVal = false;
                    }
                }
                else
                {
                    log.error( "converted input stream is null" );
                    retVal = false;
                }
            }
        }
        catch (StringIndexOutOfBoundsException | DBusException | IOException | NegativeArraySizeException e)
        {
            e.printStackTrace();
            log.error( "inputString: " + inputString );
            retVal = false;
        }

        return retVal;
    }

    private String retrieveMessageAsString(DltMessage dltMsg)
    {
        String inputString;
        if (DltSegmentedNetworkMessage.isNetworkMessage( dltMsg ))
        {
            inputString = DltSegmentedNetworkMessage.handleSegmentedMessage( dltMsg );
        }
        else
        {
            inputString = dltMsg.getPayload().get( dltMsg.getPayload().size() - 1 );
        }
        return inputString;
    }

    private InputStream convertDltMsgToInputStream(String flatString)
    {
        flatString = flatString.replaceAll( ",", "" ).replaceAll( " ", "" );
        byte[] byteArray = HexStringHelper.hexStringToByteArray( flatString );
        InputStream stream = new ByteArrayInputStream( byteArray );
        if (byteArray.length > 0)
        {
            return stream;
        }
        else
        {
            return null;
        }

    }

    private boolean dispatchMsgToDbusAdaptor(DltMessage dltMsg, Message message) throws DBusException
    {
        boolean retVal = true;

        TimestampCreator hostTimestampCreator = tsProvider.getHostTimestampCreator();

        Timestamp timestamp = hostTimestampCreator.create( dltMsg.getStandardHeader().getTimeStamp() );

        DBusApplicationMessage msg = dbusMsgToProtoMsg( message );

        DataSourceContext dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, filename + "." );

        if (null != msg)
        {
            protocolMessageDispatcher.newProtocolMessageReceived( timestamp,
                                                                  MessageType.MSG_TYPE_DBUS,
                                                                  msg.toByteArray(),
                                                                  hostTimestampCreator,
                                                                  dataSourceContext );
        }
        else
        {
            retVal = false;
        }

        return retVal;
    }

}
