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

public class ScriptTargetDisconnector implements ConnectionToTargetInteractionCallback
{
    private ConnectionModel connectionModel = null;
    private final ConnectionToTargetInteractionUseCase connectUseCase;
    private final UninterruptibleCountDownLatch disconnectLatch;

    public ScriptTargetDisconnector()
    {
        connectUseCase = UseCaseFactoryInstance.get().makeConnectionToTargetInteractionUseCase( this );
        disconnectLatch = new UninterruptibleCountDownLatch( 1 );
    }

    @VisibleForTesting
    public ScriptTargetDisconnector(ConnectionToTargetInteractionUseCase mockedUseCase,
            UninterruptibleCountDownLatch mockedLatch)
    {
        connectUseCase = mockedUseCase;
        disconnectLatch = mockedLatch;
    }

    public void disconnectFromTarget(SConnection connection)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "connection", connection );
        SConnectionImpl connectionImpl = (SConnectionImpl)connection;
        connectionModel = connectionImpl.getConnectionModel();

        connectUseCase.disconnect( connectionModel );
        waitForDisconnect();
        unregister();
    }

    public void disconnectFromAllTargets()
    {
        connectUseCase.disconnectFromAllTargets();
        waitForDisconnect();
        unregister();
    }

    private void waitForDisconnect()
    {
        disconnectLatch.await();
    }

    private void unregister()
    {
        connectUseCase.unregister();
    }

    @Override
    public void onDisconnected()
    {
        disconnectLatch.countDown();
    }

    @Override
    public void onConnected()
    {
    }

    @Override
    public void onTargetNotReachable()
    {
    }
}
