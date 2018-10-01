/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.WriteProcessRegistryIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.impl.ResMonDataEventDecoder;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

public class ResourceMonitorController implements TargetAdapter
{
    private static final Logger LOG = Logger.getLogger( ResourceMonitorController.class );

    private ResMonDataEventDecoder eventDecoder;

    private final StructureAcceptor structureAcceptor;
    private final WriteProcessRegistryIF processRegistry;
    private final RuntimeEventAcceptor runtimeEventAcceptor;

    private final DataSourceContext dataSourceContext;

    public ResourceMonitorController(WriteProcessRegistryIF processRegistry, StructureAcceptor structureAcceptor,
            RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {
        this.processRegistry = processRegistry;
        this.structureAcceptor = structureAcceptor;
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.dataSourceContext = dataSourceContext;
        setupEventDecoder();
    }

    private void setupEventDecoder()
    {
        eventDecoder = new ResMonDataEventDecoder( processRegistry,
                                                   structureAcceptor,
                                                   runtimeEventAcceptor,
                                                   dataSourceContext );
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {
        if (type == MessageType.MSG_TYPE_RESOURCE_MONITOR)
        {
            eventDecoder.initIfNot();
            try
            {
                ResourceInfo resInfo = ResourceInfo.parseFrom( payload );
                eventDecoder.newResMonApplicationMessageReceived( timestamp, resInfo );
            }
            catch (InvalidProtocolBufferException e)
            {
                LOG.warn( " ResourceMonitor's-Message is corrupted! " );
            }
        }
    }

    @Override
    public void dispose()
    {
    }
}
