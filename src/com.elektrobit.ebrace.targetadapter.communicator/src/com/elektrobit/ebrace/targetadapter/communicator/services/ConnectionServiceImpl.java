/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionStatusListener;
import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;
import com.elektrobit.ebrace.targetadapter.communicator.api.SendMessageToTargetService;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnection;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnectionDownListener;
import com.elektrobit.ebrace.targetadapter.communicator.api.TargetConnectionFactory;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.google.common.collect.HashBiMap;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class ConnectionServiceImpl
        implements
            ConnectionService,
            TargetConnectionDownListener,
            SendMessageToTargetService,
            UserInteractionPreferencesListener,
            ResetListener
{
    private final Map<ConnectionType, TargetConnectionFactory> connectionTypeToFactoryMap = new HashMap<>();
    private final HashBiMap<ConnectionModel, TargetConnection> activeConnections = HashBiMap.create();
    private final GenericListenerCaller<ConnectionStatusListener> connectionListeners = new GenericListenerCaller<>();
    private boolean everConnected = false;

    private TargetConnection createNewConnectionInstance(ConnectionModel connectionModel)
    {
        ConnectionType connectionType = connectionModel.getConnectionType();
        TargetConnectionFactory factory = connectionTypeToFactoryMap.get( connectionType );
        return factory.createNewConnection( connectionModel );
    }

    @Override
    public Set<ConnectionType> getAllConnectionTypes()
    {
        return connectionTypeToFactoryMap.keySet();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindTargetConnectionFactory(TargetConnectionFactory targetConnectionFactory)
    {
        ConnectionType connectionType = targetConnectionFactory.getConnectionType();
        connectionTypeToFactoryMap.put( connectionType, targetConnectionFactory );
    }

    public void unbindTargetConnectionFactory(TargetConnectionFactory targetConnectionFactory)
    {
        ConnectionType connectionType = targetConnectionFactory.getConnectionType();
        connectionTypeToFactoryMap.remove( connectionType );
    }

    @Override
    public boolean connect(ConnectionModel connectionModel)
    {
        if (activeConnections.containsKey( connectionModel ))
        {
            throw new IllegalArgumentException( "Connection is already connected, cannot connect again." );
        }

        TargetConnection newConnection = createNewConnectionInstance( connectionModel );
        notifyConnecting( connectionModel );
        boolean connected = newConnection.connect();
        if (connected)
        {
            everConnected = true;
            activeConnections.put( connectionModel, newConnection );
            connectionModel.setConnected( true );
            newConnection.setTargetConnectionDownListener( this );
            notifyConnected( connectionModel );
        }
        else
        {
            notifyDisconnected( connectionModel );
        }
        return connected;
    }

    private void notifyConnecting(ConnectionModel connectionModel)
    {
        connectionListeners
                .notifyListeners( (l) -> l.onTargetConnecting( connectionModel, activeConnections.keySet() ) );
    }

    private void notifyConnected(ConnectionModel connectionModel)
    {
        connectionListeners
                .notifyListeners( (l) -> l.onTargetConnected( connectionModel, activeConnections.keySet() ) );
    }

    @Override
    public void disconnect(ConnectionModel connectionModel)
    {
        TargetConnection activeConnection = activeConnections.get( connectionModel );
        if (activeConnection != null)
        {
            activeConnection.disconnect();
        }
        else
        {
            log.warn( "Cannot disconnect: no active connection found for model " + connectionModel );
        }
    }

    @Override
    public void onConnectionDown(TargetConnection targetConnection)
    {
        ConnectionModel connectionModel = activeConnections.inverse().get( targetConnection );
        connectionModel.setConnected( false );
        activeConnections.remove( connectionModel );
        targetConnection.unsetTargetConnectionDownListener( this );
        notifyDisconnected( connectionModel );
    }

    private void notifyDisconnected(ConnectionModel connectionModel)
    {
        connectionListeners
                .notifyListeners( (l) -> l.onTargetDisconnected( connectionModel, activeConnections.keySet() ) );
    }

    @Override
    public boolean sendMessage(ConnectionModel connectionModel, OutgoingMessage msg)
    {
        TargetConnection connectionNew = activeConnections.get( connectionModel );
        if (connectionNew != null)
        {
            return connectionNew.sendMessage( msg );
        }
        else
        {
            log.warn( "Cannot send message via connection because it is down " + connectionModel );
            return false;
        }
    }

    @Override
    public void addConnectionStatusListener(ConnectionStatusListener ConnectionStatusListenerNEW)
    {
        connectionListeners.add( ConnectionStatusListenerNEW );
    }

    @Override
    public void removeConnectionStatusListener(ConnectionStatusListener ConnectionStatusListenerNEW)
    {
        connectionListeners.remove( ConnectionStatusListenerNEW );
    }

    @Override
    public Set<ConnectionModel> getAllActiveConnections()
    {
        return new HashSet<ConnectionModel>( activeConnections.keySet() );
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        for (TargetConnection nextConnection : activeConnections.values())
        {
            if (isLiveMode)
            {
                nextConnection.resumeReading();
            }
            else
            {
                nextConnection.pauseReading();
            }
        }
    }

    @Override
    public void onReset()
    {
        disconnectFromAllTargets();
        everConnected = false;
    }

    @Override
    public void disconnectFromAllTargets()
    {
        Set<ConnectionModel> allActiveConnections = getAllActiveConnections();
        for (ConnectionModel activeConnection : allActiveConnections)
        {
            disconnect( activeConnection );
        }
    }

    @Override
    public boolean everConnected()
    {
        return everConnected;
    }

    @Override
    public void onNewDataRate(TargetConnection targetConnection, float datarate)
    {
        ConnectionModel connectionInfo = activeConnections.inverse().get( targetConnection );
        connectionListeners.notifyListeners( (l) -> l.onNewDataRateInKB( connectionInfo, datarate ) );
    }

}
