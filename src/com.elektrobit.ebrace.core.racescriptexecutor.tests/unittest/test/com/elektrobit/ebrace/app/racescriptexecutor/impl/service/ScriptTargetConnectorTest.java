/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptTargetConnector;
import com.elektrobit.ebrace.common.thread.UninterruptibleCountDownLatch;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;

public class ScriptTargetConnectorTest
{
    private ConnectionToTargetInteractionUseCase mockedConnectToTargetUseCase;
    private UninterruptibleCountDownLatch mockedLatch;
    private ScriptTargetConnector sutScriptTargetConnector;
    private ConnectionModel mockedConnectionModel;

    @Before
    public void setup()
    {
        mockedConnectToTargetUseCase = Mockito.mock( ConnectionToTargetInteractionUseCase.class );
        mockedLatch = Mockito.mock( UninterruptibleCountDownLatch.class );
        mockedConnectionModel = Mockito.mock( ConnectionModel.class );

        sutScriptTargetConnector = new ScriptTargetConnector( mockedConnectionModel,
                                                              mockedLatch,
                                                              mockedConnectToTargetUseCase );
    }

    private void setupConnectSuccessful()
    {
        Mockito.doAnswer( (i) -> {
            sutScriptTargetConnector.onConnected();
            return null;
        } ).when( mockedLatch ).await();
    }

    @Test
    public void testConnectResult() throws Exception
    {
        setupConnectSuccessful();

        boolean connected = sutScriptTargetConnector.connect();
        Assert.assertTrue( connected );
    }

    @Test
    public void testConnectCallsOrder() throws Exception
    {
        setupConnectSuccessful();

        sutScriptTargetConnector.connect();

        InOrder connectInOrder = Mockito.inOrder( mockedConnectToTargetUseCase, mockedLatch );

        connectInOrder.verify( mockedConnectToTargetUseCase ).connect( mockedConnectionModel );
        connectInOrder.verify( mockedLatch ).await();
        connectInOrder.verify( mockedConnectToTargetUseCase ).unregister();
    }

    @Test
    public void testTargetNotReachable()
    {
        Mockito.doAnswer( (i) -> {
            sutScriptTargetConnector.onTargetNotReachable();
            return null;
        } ).when( mockedLatch ).await();

        boolean connected = sutScriptTargetConnector.connect();
        Assert.assertFalse( connected );
    }
}
