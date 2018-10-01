/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.eventdispatcher;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.targetadapter.communicator.adapterregistry.AdapterRegistry;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.ProtoMsgContainer;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.TargetAgentConnectionType;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.TargetAgentMessageReader;
import com.elektrobit.ebrace.targetadapter.communicator.raweventlogger.RawEventLogger;
import com.elektrobit.ebrace.targetadapter.communicator.services.MessageDispatcher;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;

@Component
public class EventDispatcherImpl implements ProtocolMessageDispatcher, MessageDispatcher, ClearChunkDataListener
{
    private boolean forward = true;
    private AdapterRegistry adapterRegistry = null;
    TimestampProvider timestampProvider = new GenericOSGIServiceTracker<TimestampProvider>( TimestampProvider.class )
            .getService(); // TODO use bind
    private ConnectionModel connectionModel;
    private RawEventLogger rawEventLogger;
    private CommandLineParser commandLineParser;

    @Activate
    protected void activate(ComponentContext componentContext)
    {
        adapterRegistry = new AdapterRegistry();
    }

    @Reference
    public void bindRawEventLogger(RawEventLogger rawEventLogger)
    {
        this.rawEventLogger = rawEventLogger;
    }

    public void unbindRawEventLogger(RawEventLogger rawEventLogger)
    {
        this.rawEventLogger = null;
    }

    @Reference
    public void bindCommandLineParser(CommandLineParser commandLineParser)
    {
        this.commandLineParser = commandLineParser;
    }

    public void unbindCommandLineParser(CommandLineParser commandLineParser)
    {
        this.commandLineParser = null;
    }

    @Override
    public void forwardMessage(Object message, ConnectionModel connectionModel, DataSourceContext sourceContext)
    {
        this.connectionModel = connectionModel;
        ProtoMsgContainer protoMsgContainer = (ProtoMsgContainer)message;
        newProtocolMessageReceived( protoMsgContainer, sourceContext );
    }

    private void newProtocolMessageReceived(ProtoMsgContainer protoMessage, DataSourceContext sourceContext)
    {
        Header header = protoMessage.getHeader();
        if (header.getType().equals( MessageType.MSG_TYPE_CHRONOGRAPH_CALIBRATION ))
        {
            chronographCalibrationTargetTimestampReceived( header.getTimestamp() );
            return;
        }

        TimestampCreator timestampCreator = timestampProvider.getTargetTimestampCreator( connectionModel.getName() );

        Timestamp timestamp = timestampCreator.create( header.getTimestamp() );

        newProtocolMessageReceived( timestamp,
                                    header.getType(),
                                    protoMessage.getContent(),
                                    timestampCreator,
                                    sourceContext );
    }

    private void chronographCalibrationTargetTimestampReceived(long timestamp)
    {
        timestampProvider.registerTargetTimebase( connectionModel.getName(), timestamp );
    }

    @Override
    public void newProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator, DataSourceContext sourceContext)
    {
        if (!forward)
        {
            return;
        }

        if (commandLineParser.hasArg( "-raw" ))
        {
            rawEventLogger.acceptMessage( timestamp, type, payload, sourceContext );
        }

        TargetAdapter adapter = adapterRegistry.getAdapter( sourceContext, type );
        if (adapter != null)
        {
            try
            {
                adapter.onProtocolMessageReceived( timestamp, type, payload, timestampCreator );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setForwardMessages(boolean forward)// TODO this should be set in a TargetConnection class, not here
    {
        this.forward = forward;
    }

    @Override
    public void onClearChunkData()
    {
        adapterRegistry.disposeAllAdapters();
    }

    @Override
    public ConnectionType getConnectionType()
    {
        return new TargetAgentConnectionType();
    }

    @Override
    public MessageReader<?> getMessageReader()
    {
        return new TargetAgentMessageReader();
    }
}
