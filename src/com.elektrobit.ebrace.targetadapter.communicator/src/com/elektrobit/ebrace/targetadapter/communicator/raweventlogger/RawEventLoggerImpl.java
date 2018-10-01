/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.raweventlogger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.genivi.targetadapter.MostSpy.protobuf.MostSpy;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.genivi.targetadapter.systemdstartupmonitor.protocoldefinitions.TargetAgentProtocolSystemDStartupMonitor.SystemdBootupMonApplicationMessage;
import com.elektrobit.ebrace.internal.DecodingErrorMessageClass.DecodingErrorMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.JsonAPI.JsonAPIMsg;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto;
import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusApplicationMessage;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.dltloginspector.TargetAgentProtocolDLTLogInspector.DLTLogInspectorMessage;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.LinuxAppStatsTAProto.AppStatistics;
import com.elektrobit.ebrace.targetdata.adapter.log4j.Log4jTAProto;
import com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.NetworkPacketSnifferTAProto.NetworkPacket;
import com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor.protobuf.TargetAgentProtocolTopResourceMonitor.TopResourceInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class RawEventLoggerImpl implements RawEventLogger
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private final Unit<ProtoMessageValue> channelUnit = Unit.createCustomUnit( "ProtoMessageValue",
                                                                               ProtoMessageValue.class );

    @Reference
    public void bind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;

    }

    public void unbind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;

    }

    @Override
    public void acceptMessage(Timestamp timestamp, MessageType type, byte[] payload, DataSourceContext sourceContext)
    {
        GeneratedMessage decodedMessage = getDecodedMessage( type, payload );
        if (decodedMessage != null)
        {
            postMessageAsEvent( timestamp, type, decodedMessage, sourceContext );
        }
    }

    private GeneratedMessage getDecodedMessage(MessageType type, byte[] payload)
    {
        try
        {
            return getDecodedMessageUnchecked( type, payload );
        }
        catch (InvalidProtocolBufferException e)
        {
            log.error( "Error when processing raw messages. Cannot parse message of type " + type + " payload "
                    + payload );
        }
        catch (MessageDecoderNotDefinedException e)
        {
            log.error( "Error when processing raw messages. Please define a decoder for type " + type );
        }

        String errorMessage = "Error when processing raw message, payload was " + payload;
        return DecodingErrorMessage.newBuilder().setDecodingError( errorMessage ).build();
    }

    private GeneratedMessage getDecodedMessageUnchecked(MessageType type, byte[] payload)
            throws InvalidProtocolBufferException, MessageDecoderNotDefinedException
    {
        switch (type)
        {
            case MSG_TYPE_DBUS :
                return DBusApplicationMessage.parseFrom( payload );
            case MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN :
                return DLTLogInspectorMessage.parseFrom( payload );
            case MSG_TYPE_LINUX_APP_STATS_PLUGIN :
                return AppStatistics.parseFrom( payload );
            case MSG_TYPE_LOG4J_PLUGIN :
                return Log4jTAProto.LogData.parseFrom( payload );
            case MSG_TYPE_NETWORKPACKETSNIFFER_PLUGIN :
                return NetworkPacket.parseFrom( payload );
            case MSG_TYPE_RESOURCE_MONITOR :
                return ResourceInfo.parseFrom( payload );
            case MSG_TYPE_SOCKET_READER_PLUGIN :
                return SocketReaderTAProto.SocketReaderMessage.parseFrom( payload );
            case MSG_TYPE_GENIVI_SYSTEMD_START_UP_MONITOR :
                return SystemdBootupMonApplicationMessage.parseFrom( payload );
            case MSG_TYPE_MOST_SPY_MONITOR_PLUGIN :
                return MostSpy.MostSpyApplicationMessage.parseFrom( payload );
            case MSG_TYPE_TOP_RESOURCE_MONITOR_PLUGIN :
                return TopResourceInfo.parseFrom( payload );
            case MSG_TYPE_JSONAPI_PLUGIN :
                return JsonAPIMsg.parseFrom( payload );
            default :
                throw new MessageDecoderNotDefinedException( "Error when logging raw message. No decoder for type "
                        + type + " found." );
        }
    }

    private void postMessageAsEvent(Timestamp timestamp, MessageType type, GeneratedMessage decodedMessage,
            DataSourceContext sourceContext)
    {
        String channelName = getChannelName( type );
        String summary = TextFormat.printToString( decodedMessage );
        summary = summary.replaceAll( "\n", "" );
        ProtoMessageValue valueObject = new ProtoMessageValue( summary, decodedMessage );
        RuntimeEventChannel<ProtoMessageValue> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( sourceContext,
                                                 channelName,
                                                 channelUnit,
                                                 "Unprocessed data from solys agent" );
        runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000, channel, null, valueObject );
    }

    private String getChannelName(MessageType type)
    {
        return "raw.targetagent." + type;

    }

}
