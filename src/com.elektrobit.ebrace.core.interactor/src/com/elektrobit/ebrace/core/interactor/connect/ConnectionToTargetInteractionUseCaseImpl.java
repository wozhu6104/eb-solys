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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

public class ConnectionToTargetInteractionUseCaseImpl implements ConnectionToTargetInteractionUseCase
{
    private ConnectionToTargetInteractionCallback callback;
    private final ConnectionService connectionService;

    public ConnectionToTargetInteractionUseCaseImpl(ConnectionToTargetInteractionCallback callback,
            ConnectionService connectionService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "connectionService", connectionService );
        this.callback = callback;
        this.connectionService = connectionService;
    }

    @Override
    public void disconnect(ConnectionModel connectionModel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "ConnectionModel", connectionModel );

        connectionService.disconnect( connectionModel );
        if (callback != null)
        {
            callback.onDisconnected();
        }
    }

    @Override
    public void connect(final ConnectionModel connectionModel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "connectionModel", connectionModel );
        UseCaseExecutor.schedule( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    boolean connectedToTarget = connectionService.connect( connectionModel );
                    if (connectedToTarget)
                    {
                        postOnConnectedToCallback();
                    }
                    else
                    {
                        postOnTargetNotReachableToCallback();
                    }
                }
            }
        } );
    }

    private void postOnConnectedToCallback()
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                callback.onConnected();
            }
        } );
    }

    private void postOnTargetNotReachableToCallback()
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                callback.onTargetNotReachable();
            }
        } );
    }

    @Override
    public void disconnectFromAllTargets()
    {
        connectionService.disconnectFromAllTargets();
        if (callback != null)
        {
            callback.onDisconnected();
        }
    }

    @Override
    public void unregister()
    {
        callback = null;
    }
}
