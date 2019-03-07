/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.connect;

import java.util.Set;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionStatusListener;

public class ConnectionStateNotifyUseCaseImpl implements ConnectionStateNotifyUseCase, ConnectionStatusListener
{
    private ConnectionStateNotifyCallback callback;
    private final ConnectionService connectionService;

    public ConnectionStateNotifyUseCaseImpl(ConnectionStateNotifyCallback callback, ConnectionService connectionService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "connectionService", connectionService );
        this.callback = callback;
        this.connectionService = connectionService;
        connectionService.addConnectionStatusListener( this );
        postCurrentStatus();
    }

    private void postCurrentStatus()
    {
        final boolean connected = isConnected();

        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback == null)
                {
                    return;
                }

                if (connected)
                {
                    callback.onTargetConnected();
                }
                else
                {
                    callback.onTargetDisconnected();
                }
            }
        } );
    }

    @Override
    public void unregister()
    {
        callback = null;
        connectionService.removeConnectionStatusListener( this );
    }

    @Override
    public void onTargetDisconnected(ConnectionModel disconnected, Set<ConnectionModel> activeConnections)
    {
        postCurrentStatus();
    }

    @Override
    public void onTargetConnecting(ConnectionModel connecting, Set<ConnectionModel> activeConnections)
    {
        postOnTargetConnectingToCallback();
    }

    @Override
    public void onTargetConnected(ConnectionModel connected, Set<ConnectionModel> activeConnections)
    {
        postCurrentStatus();
    }

    private void postOnTargetConnectingToCallback()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onTargetConnecting();
                }
            }
        } );
    }

    @Override
    public boolean isConnected()
    {
        return !connectionService.getAllActiveConnections().isEmpty();
    }

    @Override
    public void onNewDataRateInKB(ConnectionModel connectionInfo, float datarate)
    {
    }
}
