/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessInfoChangedListenerIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessState;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ReadProcessRegistryIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusEvtTraceMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusInstanceType;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusMessageHeader;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusTraceMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;

public class DBusDataEventDecoder implements ProcessInfoChangedListenerIF
{
    private final static Logger LOG = Logger.getLogger( DBusDataEventDecoder.class );
    private static String TRACE_PREFIX = "trace.";

    private static final List<String> DBUS_MEMBERS_WITHOUT_REPLY = Arrays
            .asList( new String[]{"AddMatch", "RemoveMatch", "GetNameOwner", "GetConnectionUnixProcessID",
                    "GetConnectionUnixUser", "Hello", "RequestName", "StartServiceByName"} );
    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final ComRelationAcceptor comRelationAcceptor;
    private final StructureAcceptor structureAcceptor;

    private DBusServiceTreeBuilder treeBuilder;
    private DBusComRelationBuilder comRelationBuilder;
    private boolean isValid;

    private final DBusMethodCallCache dBusMethodCallCache = new DBusMethodCallCache();

    private final ReadProcessRegistryIF processRegistry;

    private final Tree dbusCommunicationTree;
    private final DataSourceContext sourceContext;
    private final Unit<ProtoMessageValue> dbusUnit = Unit.createCustomUnit( "ProtoMessageValue",
                                                                            ProtoMessageValue.class );

    public DBusDataEventDecoder(StructureAcceptor structureAcceptor, ComRelationAcceptor comRelationAcceptor,
            RuntimeEventAcceptor runtimeEventAcceptor, ReadProcessRegistryIF processRegistry,
            Tree dbusCommunicationTree, DataSourceContext sourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.structureAcceptor = structureAcceptor;
        this.comRelationAcceptor = comRelationAcceptor;
        this.processRegistry = processRegistry;
        this.dbusCommunicationTree = dbusCommunicationTree;
        RangeCheckUtils.assertReferenceParameterNotNull( "DataSourceContext", sourceContext );
        this.sourceContext = sourceContext;

        isValid = true;

    }

    public void invalidate()
    {
        isValid = false;
        if (treeBuilder != null)
        {
            treeBuilder.invalidate();
        }
    }

    public void init()
    {
        if (isValid)
        {
            if (treeBuilder == null)
            {
                initDBusTree();
            }
        }
    }

    private void initDBusTree()
    {
        treeBuilder = new DBusServiceTreeBuilder( structureAcceptor,
                                                  processRegistry,
                                                  dbusCommunicationTree,
                                                  sourceContext );
        treeBuilder.addNodesForUnknownProcess();
        comRelationBuilder = new DBusComRelationBuilder( comRelationAcceptor, treeBuilder, dBusMethodCallCache );
    }

    private RuntimeEventChannel<ProtoMessageValue> getSessionBusChannel()
    {
        return runtimeEventAcceptor.createOrGetRuntimeEventChannel( sourceContext,
                                                                    TRACE_PREFIX + "dbus.sessionbus",
                                                                    dbusUnit,
                                                                    "" );
    }

    private RuntimeEventChannel<ProtoMessageValue> getSystemBusChannel()
    {
        return runtimeEventAcceptor.createOrGetRuntimeEventChannel( sourceContext,
                                                                    TRACE_PREFIX + "dbus.systembus",
                                                                    dbusUnit,
                                                                    "" );
    }

    public void newDBusApplicationMessageReceived(Timestamp timestamp, DBusApplicationMessage msg)
    {
        if (isValid)
        {
            if (msg.hasTraceMessage())
            {
                addMsgToCacheIfNeeded( msg.getTraceMessage() );
                processDBusMessage( msg.getTraceMessage(), timestamp );
                removeMsgFromCacheIfNeeded( msg.getTraceMessage() );
            }
        }
    }

    private void addMsgToCacheIfNeeded(DBusEvtTraceMessage msg)
    {
        DBusTraceMessageType msgType = msg.getHeader().getType();
        if (msgType.equals( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_CALL )
                && dbusReturnMessageExpected( msg.getHeader() ))
        {
            dBusMethodCallCache.addCallToCache( msg.getHeader() );
        }
    }

    private boolean dbusReturnMessageExpected(DBusMessageHeader header)
    {
        if (header.getInterface().equals( "org.freedesktop.DBus" ))
        {
            String member = header.getMember();
            return !DBUS_MEMBERS_WITHOUT_REPLY.contains( member );
        }
        return true;
    }

    private void removeMsgFromCacheIfNeeded(DBusEvtTraceMessage msg)
    {
        DBusTraceMessageType msgType = msg.getHeader().getType();
        if (msgType.equals( DBusTraceMessageType.DBUS_MSG_TYPE_METHOD_RETURN ))
        {
            dBusMethodCallCache.removeCallByReturnMsg( msg.getHeader() );
        }
    }

    private boolean processDBusMessage(DBusEvtTraceMessage msg, Timestamp timestamp)
    {
        ComRelation comRelation = comRelationBuilder.createComRelationByDBusTraceMessage( msg );
        if (comRelation != null)
        {
            fireDBusMessageRuntimeEvent( msg, timestamp, comRelation );
            return true;
        }
        return false;
    }

    private void fireDBusMessageRuntimeEvent(DBusEvtTraceMessage msg, Timestamp timestamp, ComRelation comRelation)
    {
        ModelElement rteModelElement = (comRelation != null) ? comRelation : ModelElement.NULL_MODEL_ELEMENT;
        if (msg.hasInstance())
        {
            ValueObjectParseResult valueObjectParseResult = parseValueObjectFromMessage( msg );

            RuntimeEventChannel<ProtoMessageValue> channel = null;

            if (msg.getInstance() == DBusInstanceType.DBUS_INSTANCE_SESSION_BUS)
            {
                channel = getSessionBusChannel();
            }
            else if (msg.getInstance() == DBusInstanceType.DBUS_INSTANCE_SYSTEM_BUS)
            {
                channel = getSystemBusChannel();
            }
            else
            {
                LOG.warn( "Ignoring dbus message" + msg );
            }

            if (channel != null)
            {
                RuntimeEvent<ProtoMessageValue> event = runtimeEventAcceptor
                        .acceptEvent( timestamp.getTimeInMillis(),
                                      channel,
                                      rteModelElement,
                                      valueObjectParseResult.getValueObject() );

                if (!valueObjectParseResult.isCorrect())
                {
                    runtimeEventAcceptor.setTag( event,
                                                 valueObjectParseResult.getRuntimeEventTag(),
                                                 valueObjectParseResult.getTagMessage() );
                }
            }
        }
    }

    private ValueObjectParseResult parseValueObjectFromMessage(DBusEvtTraceMessage msg)
    {
        ValueObjectParseResult result = null;

        DBusMessageHeader updatedHeader = addProcessNamesToHeader( msg.getHeader() );
        DBusEvtTraceMessage updatedTraceMessage = updateHeaderInTraceMessage( msg, updatedHeader );
        switch (updatedHeader.getType())
        {
            case DBUS_MSG_TYPE_METHOD_CALL :
                result = createValueObjectForCallMsg( updatedTraceMessage );
                break;
            case DBUS_MSG_TYPE_METHOD_RETURN :
                result = createValueObjectForReturnMsg( updatedTraceMessage );
                break;
            case DBUS_MSG_TYPE_SIGNAL :
                result = createValueObjectForSignalMsg( updatedTraceMessage );
            default :
                break;
        }

        return result;
    }

    private DBusEvtTraceMessage updateHeaderInTraceMessage(DBusEvtTraceMessage msg, DBusMessageHeader header)
    {
        return DBusEvtTraceMessage.newBuilder( msg ).setHeader( header ).build();
    }

    private DBusMessageHeader addProcessNamesToHeader(DBusMessageHeader header)
    {
        DBusMessageHeader.Builder builder = DBusMessageHeader.newBuilder( header );

        int receiverPid = header.getReceiverPid();
        builder.setReceiverProcessName( formatProcessInfo( receiverPid ) );

        int senderPid = header.getSenderPid();
        builder.setSenderProcessName( formatProcessInfo( senderPid ) );

        return builder.build();
    }

    private String formatProcessInfo(int pid)
    {
        StringBuilder processInfoString = new StringBuilder();
        ProcessInfo processInfo = processRegistry.getProcessInfo( pid, sourceContext );
        if (processInfo != null)
        {
            processInfoString.append( processInfo.getName() + ":" + pid );
        }
        else
        {
            processInfoString.append( pid );
        }
        return processInfoString.toString();
    }

    private ValueObjectParseResult createValueObjectForSignalMsg(DBusEvtTraceMessage msg)
    {
        ValueObjectParseResult result = new ValueObjectParseResult();

        String summary = "Signal " + msg.getHeader().getPath() + " " + msg.getHeader().getInterface() + "::"
                + msg.getHeader().getMember();

        result.setCorrect( true );
        result.setValueObject( new ProtoMessageValue( summary, msg ) );

        return result;
    }

    private ValueObjectParseResult createValueObjectForCallMsg(DBusEvtTraceMessage msg)
    {
        ValueObjectParseResult result = new ValueObjectParseResult();

        String summary = "-> " + msg.getHeader().getInterface() + "::" + msg.getHeader().getMember();

        result.setCorrect( true );
        result.setValueObject( new ProtoMessageValue( summary, msg ) );

        return result;
    }

    private ValueObjectParseResult createValueObjectForReturnMsg(DBusEvtTraceMessage msg)
    {
        ValueObjectParseResult result = new ValueObjectParseResult();

        DBusMethodCallCacheObj callCacheObj = dBusMethodCallCache.findCallForReturn( msg.getHeader() );

        String summary = null;

        if (callCacheObj != null)
        {
            summary = "<- " + callCacheObj.getInterfaceName() + "::" + callCacheObj.getMember();
            result.setCorrect( true );
        }
        else
        {
            summary = "<- service " + msg.getHeader().getSender() + "  CORRESPONDING_CALL_NOT_AVAILABLE" + "::"
                    + "NO_MEMBER    ";

            result.setCorrect( false );
            result.setRuntimeEventTag( RuntimeEventTag.WARNING );
            result.setTagMessage( "CORRESPONDING_CALL_NOT_AVAILABLE" );
        }

        result.setValueObject( new ProtoMessageValue( summary, msg ) );
        return result;
    }

    @Override
    public void processInfoChanged(DataSourceContext dataSourceContext)
    {
        if (dataSourceContext.equals( sourceContext ))
        {
            updateProcessStates();
        }
    }

    private void updateProcessStates()
    {
        Set<Integer> notRunningPIDs = new HashSet<Integer>();

        for (Integer nextPID : processRegistry.getAllProcessPIDs( sourceContext ))
        {
            if (!processRegistry.getActiveProcessPIDs( sourceContext ).contains( nextPID ))
            {
                notRunningPIDs.add( nextPID );
            }
        }

        for (Integer nextActivePID : processRegistry.getActiveProcessPIDs( sourceContext ))
        {
            if (treeBuilder.dbusProcessExists( nextActivePID ))
            {
                DBusServiceTreeProcessNode dBusProcessNode = treeBuilder.getDBusProcessNode( nextActivePID );
                dBusProcessNode.setProcessState( ProcessState.RUNNING.toString() );
            }
        }

        for (Integer nextInactivePID : notRunningPIDs)
        {
            if (treeBuilder.dbusProcessExists( nextInactivePID ))
            {
                DBusServiceTreeProcessNode dBusProcessNode = treeBuilder.getDBusProcessNode( nextInactivePID );
                dBusProcessNode.setProcessState( ProcessState.DEAD.toString() );
            }
        }
    }
}
