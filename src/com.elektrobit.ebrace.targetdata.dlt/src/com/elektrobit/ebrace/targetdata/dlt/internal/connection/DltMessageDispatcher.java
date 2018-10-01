/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.connection;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetadapter.communicator.services.MessageDispatcher;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageProcessor;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStreamMessageServiceImpl;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

@Component
public class DltMessageDispatcher implements MessageDispatcher
{
    private TimeMarkerManager timeMarkerManager;
    private TimestampProvider timestampProvider;
    private ProtocolMessageDispatcher protocolMessageDispatcher;
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private DltMessageProcessor dltMessageProcessor;
    private DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator;

    @Activate
    public void activate()
    {
        dltMessageProcessor = new DltMessageProcessor( "todo file name",
                                                       Long.MAX_VALUE,
                                                       timestampProvider,
                                                       timeMarkerManager,
                                                       protocolMessageDispatcher,
                                                       runtimeEventAcceptor );
    }

    @Override
    public void forwardMessage(Object message, ConnectionModel connectionModel, DataSourceContext sourceContext)
    {
        dltMessageProcessor.processMessage( (DltMessage)message );
    }

    @Override
    public MessageReader<?> getMessageReader()
    {
        return new DltStreamMessageServiceImpl( dltChannelFromLogInfoCreator );
    }

    @Override
    public ConnectionType getConnectionType()
    {
        return new DltConnectionType();
    }

    @Reference
    public void bindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = dltChannelFromLogInfoCreator;
    }

    public void unbindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = null;
    }

    @Reference
    public void bindTimeMarkerManager(TimeMarkerManager timeMarker)
    {
        this.timeMarkerManager = timeMarker;
    }

    public void unbindTimeMarkerManager(TimeMarkerManager timeMarker)
    {
        this.timeMarkerManager = null;
    }

    @Reference
    public void bindTimestampProvider(TimestampProvider timestampProvider)
    {
        this.timestampProvider = timestampProvider;
    }

    public void unbindTimestampProvider(TimestampProvider timestampProvider)
    {
        this.timestampProvider = null;
    }

    @Reference
    public void bindProtocolMessageDispatcher(ProtocolMessageDispatcher protocolMessageDispatcher)
    {
        this.protocolMessageDispatcher = protocolMessageDispatcher;
    }

    public void unbindProtocolMessageDispatcher(ProtocolMessageDispatcher tsProvider)
    {
        this.protocolMessageDispatcher = null;
    }

    @Reference
    public void bind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }
}
