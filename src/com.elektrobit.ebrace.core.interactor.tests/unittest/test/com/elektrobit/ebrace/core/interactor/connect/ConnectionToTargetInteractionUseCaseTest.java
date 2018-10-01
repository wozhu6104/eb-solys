/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.connect;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.connect.ConnectionToTargetInteractionUseCaseImpl;
import com.elektrobit.ebrace.resources.api.model.connection.ConnectionModelImpl;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ConnectionToTargetInteractionUseCaseTest extends UseCaseBaseTest
{
    private static final String NAME = "Conection 1";
    private static final String IP = "192.168.2.2";
    private static final int PORT = 1234;

    private ConnectionService connectionService;
    private ConnectionToTargetInteractionCallback connectionToTargetInteractionCallback;
    private ConnectionToTargetInteractionUseCaseImpl sutConnectUseCase;
    private ConnectionModel connectionModel;

    @Before
    public void setup()
    {
        connectionService = Mockito.mock( ConnectionService.class );
        connectionToTargetInteractionCallback = Mockito.mock( ConnectionToTargetInteractionCallback.class );
        sutConnectUseCase = new ConnectionToTargetInteractionUseCaseImpl( connectionToTargetInteractionCallback,
                                                                          connectionService );
        connectionModel = new ConnectionModelImpl( NAME, IP, PORT, false, null, null, null );
    }

    @Test
    public void testConnectWithTargetNotReachable()
    {
        Mockito.when( connectionService.connect( connectionModel ) ).thenReturn( false );

        sutConnectUseCase.connect( connectionModel );

        Mockito.verify( connectionToTargetInteractionCallback, Mockito.times( 1 ) ).onTargetNotReachable();
        Mockito.verifyNoMoreInteractions( connectionToTargetInteractionCallback );
    }

    @Test
    public void testConnectWithTargetReachable()
    {
        Mockito.when( connectionService.connect( connectionModel ) ).thenReturn( true );

        sutConnectUseCase.connect( connectionModel );

        Mockito.verify( connectionToTargetInteractionCallback, Mockito.times( 1 ) ).onConnected();
        Mockito.verifyNoMoreInteractions( connectionToTargetInteractionCallback );
    }

    @Test
    public void testDisconnect()
    {
        sutConnectUseCase.disconnectFromAllTargets();

        Mockito.verify( connectionService ).disconnectFromAllTargets();
        Mockito.verify( connectionToTargetInteractionCallback, Mockito.times( 1 ) ).onDisconnected();
        Mockito.verifyNoMoreInteractions( connectionToTargetInteractionCallback );
    }

}
