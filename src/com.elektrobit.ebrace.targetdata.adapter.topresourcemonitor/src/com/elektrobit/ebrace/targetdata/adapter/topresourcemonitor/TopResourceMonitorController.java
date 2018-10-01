/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor.impl.TopResMonDataEventDecoder;
import com.elektrobit.ebrace.targetdata.adapter.topresourcemonitor.protobuf.TargetAgentProtocolTopResourceMonitor.TopResourceInfo;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

public class TopResourceMonitorController implements TargetAdapter
{
    private static final Logger LOG = Logger.getLogger( TopResourceMonitorController.class );

    private final RuntimeEventAcceptor runtimeEventAcceptor;
    private final DataSourceContext dataSourceContext;

    private TopResMonDataEventDecoder eventDecoder;

    public TopResourceMonitorController(RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
        setupEventDecoder();
    }

    private void setupEventDecoder()
    {
        eventDecoder = new TopResMonDataEventDecoder( runtimeEventAcceptor, dataSourceContext );
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type == MessageType.MSG_TYPE_TOP_RESOURCE_MONITOR_PLUGIN)
        {
            try
            {
                TopResourceInfo topRessourceInfo = TopResourceInfo.parseFrom( payload );
                eventDecoder.newResMonApplicationMessageReceived( timestamp, topRessourceInfo );
            }
            catch (InvalidProtocolBufferException e)
            {
                LOG.warn( " Top Resource Monitor  Message is corrupted! " );
            }
        }
    }

    @Override
    public void dispose()
    {
    }
}
