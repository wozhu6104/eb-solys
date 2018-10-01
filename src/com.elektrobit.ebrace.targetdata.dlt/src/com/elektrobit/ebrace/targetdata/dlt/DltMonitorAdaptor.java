/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorMessage;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorMessageId;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorTraceMessage;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTMessageType;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.protobuf.InvalidProtocolBufferException;

public class DltMonitorAdaptor implements TargetAdapter
{

    private static final Logger LOG = Logger.getLogger( DltMonitorAdaptor.class );

    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final DataSourceContext dataSourceContext;

    public DltMonitorAdaptor(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN ))
        {
            try
            {
                DLTLogInspectorMessage parsedMessage = DLTLogInspectorMessage.parseFrom( payload );

                if (parsedMessage.getId() == DLTLogInspectorMessageId.LOG_ANALYZER_MESSAGE_TRACE
                        && parsedMessage.hasTraceMessage())
                {
                    DLTLogInspectorTraceMessage traceMessage = parsedMessage.getTraceMessage();
                    traceMessage.getChannel();
                    RuntimeEventChannel<String> channel = getChannelForMessage( traceMessage );

                    String value = "[" + traceMessage.getTimestamp() + "]" + traceMessage.getChannel() + "."
                            + traceMessage.getContext() + ": " + traceMessage.getData();

                    this.runtimeEventAcceptor.acceptEvent( timestamp.getTimeInMillis(),
                                                           channel,
                                                           ModelElement.NULL_MODEL_ELEMENT,
                                                           value );
                }
                else
                {
                    LOG.warn( "Invalid DLT Message found." );
                }
            }
            catch (InvalidProtocolBufferException e)
            {
                LOG.warn( "DLT's-Message is corrupted!" );
            }
        }
    }

    private RuntimeEventChannel<String> getChannelForMessage(DLTLogInspectorTraceMessage traceMessage)
    {
        String mainChannel = getMainChannelName( traceMessage );
        String subChannelName = getSubChannelName( traceMessage );
        return getChannelForLogLevelName( mainChannel, subChannelName );
    }

    private String getMainChannelName(DLTLogInspectorTraceMessage traceMessage)
    {
        DLTMessageType messageType = traceMessage.getMessageType();
        switch (messageType)
        {
            case DLT_TYPE_LOG :
                return "log";
            case DLT_TYPE_APP_TRACE :
                return "app_trace";
            case DLT_TYPE_NW_TRACE :
                return "nw_trace";
            case DLT_TYPE_CONTROL :
                return "control";
            default :
                throw new IllegalArgumentException( "Unexpected type of DBusMessageType " + messageType );
        }
    }

    private String getSubChannelName(DLTLogInspectorTraceMessage traceMessage)
    {
        if (traceMessage.hasLogInfo())
        {
            return getChannelNameFromEnum( traceMessage.getLogInfo().name(), "DLT_LOG_" );
        }
        if (traceMessage.hasTraceInfo())
        {
            return getChannelNameFromEnum( traceMessage.getTraceInfo().name(), "DLT_TRACE_" );
        }
        if (traceMessage.hasBusInfo())
        {
            return getChannelNameFromEnum( traceMessage.getBusInfo().name(), "DLT_NW_TRACE_" );
        }
        if (traceMessage.hasControlInfo())
        {
            return getChannelNameFromEnum( traceMessage.getControlInfo().name(), "DLT_CONTROL_" );
        }
        LOG.warn( "DLT message has no subchannel" );
        return null;
    }

    private String getChannelNameFromEnum(String enumName, String prefixToRemove)
    {
        String withoutDltPrefix = enumName.replace( prefixToRemove, "" );
        return withoutDltPrefix.toLowerCase();
    }

    private RuntimeEventChannel<String> getChannelForLogLevelName(String mainChannelName, String subChannelName)
    {
        if (subChannelName != null)
        {
            return runtimeEventAcceptor.createOrGetRuntimeEventChannel( dataSourceContext,
                                                                        "trace.dlt." + mainChannelName + "."
                                                                                + subChannelName,
                                                                        Unit.TEXT,
                                                                        "DLT messages for channel: " + mainChannelName
                                                                                + " and subchannel " + subChannelName );
        }
        else
        {
            return runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( dataSourceContext,
                                                     "trace.dlt." + mainChannelName,
                                                     Unit.TEXT,
                                                     "DLT messages for channel: " + mainChannelName );
        }
    }

    @Override
    public void dispose()
    {
    }
}
