/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.log4jtargetadapter.internal;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.adapter.log4j.Log4jTAProto;
import com.elektrobit.ebrace.targetdata.adapter.log4j.Log4jTAProto.LogData;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.extern.log4j.Log4j;

@Log4j
public class Log4jTargetAdapter implements TargetAdapter
{

    private final RuntimeEventAcceptor runtimeEventAcceptor;

    public Log4jTargetAdapter(RuntimeEventAcceptor runtimeEventAcceptor, TimestampProvider tsProvider,
            ComRelationAcceptor comRelationAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_LOG4J_PLUGIN ))
        {

            LogData message = null;

            try
            {
                message = Log4jTAProto.LogData.parseFrom( payload );
                Log4jRuntimeEventCreator.createRuntimeEvent( message, timestamp, runtimeEventAcceptor );

            }
            catch (InvalidProtocolBufferException e)
            {
                log.warn( "Log4j's-Message is corrupted!" );
            }
        }
    }

    @Override
    public void dispose()
    {
    }
}
