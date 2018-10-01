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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptTargetDisconnector;
import com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels.SConnectionImpl;
import com.elektrobit.ebrace.common.thread.UninterruptibleCountDownLatch;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;

public class ScriptTargetDisconnectorTest
{
    private ConnectionToTargetInteractionUseCase mockedConnectToTargetUseCase;
    private UninterruptibleCountDownLatch mockedLatch;
    private ConnectionModel mockedConnectionModel;
    private SConnectionImpl mockedSConnection;
    private ScriptTargetDisconnector sutScriptTargetDisconnector;

    @Before
    public void setup()
    {
        mockedConnectToTargetUseCase = Mockito.mock( ConnectionToTargetInteractionUseCase.class );
        mockedLatch = Mockito.mock( UninterruptibleCountDownLatch.class );
        mockedConnectionModel = Mockito.mock( ConnectionModel.class );
        mockedSConnection = Mockito.mock( SConnectionImpl.class );
        Mockito.when( mockedSConnection.getConnectionModel() ).thenReturn( mockedConnectionModel );

        sutScriptTargetDisconnector = new ScriptTargetDisconnector( mockedConnectToTargetUseCase, mockedLatch );
    }

    @Test
    public void testDisconnectOrder() throws Exception
    {
        Mockito.doAnswer( (i) -> {
            sutScriptTargetDisconnector.onDisconnected();
            return null;
        } ).when( mockedLatch ).await();

        sutScriptTargetDisconnector.disconnectFromTarget( mockedSConnection );

        InOrder connectInOrder = Mockito.inOrder( mockedConnectToTargetUseCase, mockedLatch );

        connectInOrder.verify( mockedConnectToTargetUseCase ).disconnect( mockedConnectionModel );
        connectInOrder.verify( mockedLatch ).await();
        connectInOrder.verify( mockedConnectToTargetUseCase ).unregister();
    }

    @Test
    public void testDisconnectFromAllOrder() throws Exception
    {
        Mockito.doAnswer( (i) -> {
            sutScriptTargetDisconnector.onDisconnected();
            return null;
        } ).when( mockedLatch ).await();

        sutScriptTargetDisconnector.disconnectFromAllTargets();

        InOrder connectInOrder = Mockito.inOrder( mockedConnectToTargetUseCase, mockedLatch );

        connectInOrder.verify( mockedConnectToTargetUseCase ).disconnectFromAllTargets();
        connectInOrder.verify( mockedLatch ).await();
        connectInOrder.verify( mockedConnectToTargetUseCase ).unregister();
    }
}
