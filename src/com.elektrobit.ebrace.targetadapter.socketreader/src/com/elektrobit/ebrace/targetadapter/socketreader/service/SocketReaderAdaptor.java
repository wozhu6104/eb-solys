/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.socketreader.service;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

public class SocketReaderAdaptor implements TargetAdapter
{

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger( SocketReaderAdaptor.class );
    private final SocketReaderMessageParser socketReaderMessageParser;

    public SocketReaderAdaptor(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        socketReaderMessageParser = new SocketReaderMessageParser( runtimeEventAcceptor, dataSourceContext );
    }

    @Override
    public void onProtocolMessageReceived(final Timestamp timestamp, final MessageType type, final byte[] payload,
            TimestampCreator timestampCreator)
    {
        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_SOCKET_READER_PLUGIN ))
        {
            socketReaderMessageParser.parseMessage( timestamp, payload );
        }
    }

    @Override
    public void dispose()
    {
    }
}
