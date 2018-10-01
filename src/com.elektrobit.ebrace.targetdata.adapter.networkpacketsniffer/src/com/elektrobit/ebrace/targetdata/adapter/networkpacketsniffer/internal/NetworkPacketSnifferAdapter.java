/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.internal;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.NetworkPacketSnifferTAProto.MESSAGE_SOURCE;
import com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.NetworkPacketSnifferTAProto.NetworkPacket;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.extern.log4j.Log4j;

@Log4j
public class NetworkPacketSnifferAdapter implements TargetAdapter
{

    private final RuntimeEventAcceptor runtimeEventAcceptor;

    public NetworkPacketSnifferAdapter(RuntimeEventAcceptor runtimeEventAcceptor, StructureAcceptor structureAcceptor,
            ComRelationAcceptor comRelationAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_NETWORKPACKETSNIFFER_PLUGIN ))
        {
            NetworkPacket message = null;

            try
            {
                message = NetworkPacket.parseFrom( payload );
                if (message.getSource() == MESSAGE_SOURCE.MESSAGE_SOURCE_WEBSOCK)
                {
                    RuntimeEventChannel<String> channel = runtimeEventAcceptor
                            .createOrGetRuntimeEventChannel( "ipc.websockets", Unit.TEXT, "websockets data" );

                    String lines[] = message.getPduPayload().toString().split( "\\r?\\n" );

                    for (String line : lines)
                    {

                        runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                                channel,
                                                                null,
                                                                line );
                    }
                }
                else
                {
                    log.warn( message.getSource().toString() + "not handled" );
                }
            }

            catch (InvalidProtocolBufferException e)
            {
                log.warn( "NetworkPacket-Message is corrupted!" );
            }

        }
    }

    @Override
    public void dispose()
    {
    }
}
