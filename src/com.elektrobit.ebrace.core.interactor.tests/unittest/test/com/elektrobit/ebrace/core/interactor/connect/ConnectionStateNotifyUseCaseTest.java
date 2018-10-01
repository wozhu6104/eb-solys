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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.connect.ConnectionStateNotifyUseCaseImpl;
import com.elektrobit.ebrace.resources.api.model.connection.ConnectionModelImpl;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ConnectionStateNotifyUseCaseTest extends UseCaseBaseTest
{
    private static final String IP = "192.168.2.2";
    private static final int PORT = 1234;
    private ConnectionStateNotifyCallback connectionStateNotifyCallback;
    private ConnectionService connectionService;
    private ConnectionModel connectionModel;

    @Before
    public void setup()
    {
        connectionStateNotifyCallback = Mockito.mock( ConnectionStateNotifyCallback.class );
        connectionService = Mockito.mock( ConnectionService.class );
        connectionModel = new ConnectionModelImpl( "Connection", IP, PORT, false, null, null, null );
    }

    @Test
    public void onTargetConnectingTest()
    {
        ConnectionStateNotifyUseCaseImpl sut = new ConnectionStateNotifyUseCaseImpl( connectionStateNotifyCallback,
                                                                                     connectionService );

        ConnectionModel connectionInfo = new ConnectionModelImpl( "Connection", IP, PORT, false, null, null, null );
        sut.onTargetConnecting( connectionInfo, Collections.emptySet() );
        Mockito.verify( connectionStateNotifyCallback ).onTargetConnecting();
    }

    @Test
    public void initialStateConnectedTest()
    {
        Set<ConnectionModel> allActiveConnections = new HashSet<>( Arrays.asList( connectionModel ) );
        Mockito.when( connectionService.getAllActiveConnections() ).thenReturn( allActiveConnections );
        ConnectionStateNotifyUseCaseImpl sut = new ConnectionStateNotifyUseCaseImpl( connectionStateNotifyCallback,
                                                                                     connectionService );
        Mockito.verify( connectionStateNotifyCallback ).onTargetConnected();
        Assert.assertTrue( sut.isConnected() );
    }

    @Test
    public void initialStateDisconnectedTest()
    {
        Mockito.when( connectionService.getAllActiveConnections() ).thenReturn( Collections.emptySet() );
        ConnectionStateNotifyUseCaseImpl sut = new ConnectionStateNotifyUseCaseImpl( connectionStateNotifyCallback,
                                                                                     connectionService );
        Mockito.verify( connectionStateNotifyCallback ).onTargetDisconnected();
        Assert.assertFalse( sut.isConnected() );
    }

    @Test
    public void onTargetDisconnectedTest()
    {
        Mockito.when( connectionService.getAllActiveConnections() ).thenReturn( Collections.emptySet() );

        ConnectionStateNotifyUseCaseImpl sut = new ConnectionStateNotifyUseCaseImpl( connectionStateNotifyCallback,
                                                                                     connectionService );
        sut.onTargetDisconnected( null, null );
        Mockito.verify( connectionStateNotifyCallback, Mockito.times( 2 ) ).onTargetDisconnected();
        Assert.assertFalse( sut.isConnected() );
    }

    @Test
    public void onTargetConnectedTest()
    {
        Set<ConnectionModel> allActiveConnections = new HashSet<>( Arrays.asList( connectionModel ) );
        Mockito.when( connectionService.getAllActiveConnections() ).thenReturn( allActiveConnections );

        ConnectionStateNotifyUseCaseImpl sut = new ConnectionStateNotifyUseCaseImpl( connectionStateNotifyCallback,
                                                                                     connectionService );
        sut.onTargetConnected( null, null );
        Mockito.verify( connectionStateNotifyCallback, Mockito.times( 2 ) ).onTargetConnected();
        Assert.assertTrue( sut.isConnected() );
    }

    @Test
    public void unregisterTest()
    {
        Set<ConnectionModel> allActiveConnections = new HashSet<>( Arrays.asList( connectionModel ) );
        Mockito.when( connectionService.getAllActiveConnections() ).thenReturn( allActiveConnections );

        ConnectionStateNotifyUseCaseImpl sut = new ConnectionStateNotifyUseCaseImpl( connectionStateNotifyCallback,
                                                                                     connectionService );

        Mockito.verify( connectionStateNotifyCallback, Mockito.times( 1 ) ).onTargetConnected();
        sut.unregister();
        sut.onTargetConnected( null, null );
        Mockito.verify( connectionService ).removeConnectionStatusListener( sut );
        Mockito.verifyNoMoreInteractions( connectionStateNotifyCallback );
    }
}
