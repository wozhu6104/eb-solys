/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.SConnectionImpl;
import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.thread.UninterruptibleCountDownLatch;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebsolys.script.external.SConnection;
import com.google.common.annotations.VisibleForTesting;

public class ScriptTargetConnector implements ConnectionToTargetInteractionCallback
{
    private final ConnectionModel connectionModel;
    private final ConnectionToTargetInteractionUseCase connectUseCase;
    private final UninterruptibleCountDownLatch connectionLatch;
    private Boolean connected = null;

    public ScriptTargetConnector(SConnection connection)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "connection", connection );
        SConnectionImpl connectionImpl = (SConnectionImpl)connection;
        connectionModel = connectionImpl.getConnectionModel();
        connectUseCase = UseCaseFactoryInstance.get().makeConnectionToTargetInteractionUseCase( this );
        connectionLatch = new UninterruptibleCountDownLatch( 1 );
    }

    @VisibleForTesting
    public ScriptTargetConnector(ConnectionModel mockedConnectionModel, UninterruptibleCountDownLatch mockedLatch,
            ConnectionToTargetInteractionUseCase mockedConnectUseCase)
    {
        connectionModel = mockedConnectionModel;
        connectionLatch = mockedLatch;
        connectUseCase = mockedConnectUseCase;
    }

    public boolean connect()
    {
        connectUseCase.connect( connectionModel );
        waitForConnect();
        unregister();
        return connected;
    }

    private void waitForConnect()
    {
        connectionLatch.await();
    }

    private void unregister()
    {
        connectUseCase.unregister();
    }

    @Override
    public void onConnected()
    {
        connected = true;
        connectionLatch.countDown();
    }

    @Override
    public void onTargetNotReachable()
    {
        connected = false;
        connectionLatch.countDown();
    }

    @Override
    public void onDisconnected()
    {
    }
}
