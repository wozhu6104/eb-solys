/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.ConnectionModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common.GenericScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

public class ConnectionModeRunnerTest
{
    private ConnectionModeRunner connectionModeRunner;
    private String methodName;
    private ConnectionService connectionService;
    private RaceScriptInfo script;
    private GenericScriptRunner genericScriptRunner;
    private ResourcesModelManager resourcesModelManager;
    private CountDownLatch waitForDisconnectingLatch;
    private CountDownLatch waitForScriptExecutionLatch;
    private ScriptExecutorService scriptExecutorService;

    @Before
    public void setup()
    {
        connectionService = mock( ConnectionService.class );
        genericScriptRunner = mock( GenericScriptRunner.class );

        resourcesModelManager = mock( ResourcesModelManager.class );

        ConnectionType connectionType1 = mockConnectionType( "TA", "bin" );
        ConnectionType connectionType2 = mockConnectionType( "DLT", "dlt" );
        Mockito.when( resourcesModelManager.getAllConnectionTypes() )
                .thenReturn( Arrays.asList( connectionType1, connectionType2 ) );

        waitForDisconnectingLatch = mock( CountDownLatch.class );
        waitForScriptExecutionLatch = mock( CountDownLatch.class );
        scriptExecutorService = mock( ScriptExecutorService.class );
        connectionModeRunner = new ConnectionModeRunner( resourcesModelManager,
                                                         connectionService,
                                                         genericScriptRunner,
                                                         waitForDisconnectingLatch,
                                                         waitForScriptExecutionLatch,
                                                         scriptExecutorService );

        script = mock( RaceScriptInfo.class );
        methodName = "execute";
    }

    private ConnectionType mockConnectionType(String name, String extension)
    {
        ConnectionType mockedConnectionType = Mockito.mock( ConnectionType.class );
        Mockito.when( mockedConnectionType.getName() ).thenReturn( name );
        Mockito.when( mockedConnectionType.getExtension() ).thenReturn( extension );
        return mockedConnectionType;
    }

    @Test
    public void paramsOkIfHostNameAndPortFromConnectionServiceOk() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertTrue( connectionModeRunner.paramsOk( "192.168.2.2:1234", script, methodName ) );
    }

    @Test
    public void paramsNotOkHostNamePortFormatWrong() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertFalse( connectionModeRunner.paramsOk( "192.168.2.2", script, methodName ) );
    }

    @Test
    public void paramsNotOkIfParamsWindowsPath() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertFalse( connectionModeRunner.paramsOk( "E:\\My\\Path\\To\\AFILE.bin", script, methodName ) );
    }

    @Test
    public void paramsNotOkIfConnectionServicePortParamNotOk() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertFalse( connectionModeRunner.paramsOk( "192.168.2.2:a1234", script, methodName ) );
    }

    @Test
    public void paramsNotOkIfGenericScriptParamsNotOk() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( false );

        assertFalse( connectionModeRunner.paramsOk( "192.168.2.2:1234", script, methodName ) );
    }

    @Test
    public void waitingForDisconnectWhenScriptRunningAndConnectionEstablished() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );
        when( connectionService.connect( Mockito.anyObject() ) ).thenReturn( true );

        connectionModeRunner.run( "192.168.2.2:1234", script, methodName );

        verify( waitForDisconnectingLatch ).await();
    }

    @Test
    public void waitingForScriptStopWhenScriptRunningAndConnectionEstablished() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );
        when( connectionService.connect( Mockito.anyObject() ) ).thenReturn( true );

        connectionModeRunner.run( "192.168.2.2:1234", script, methodName );

        verify( waitForScriptExecutionLatch ).await();
    }

    @Test
    public void waitingForNotDisconnectWhenScriptNotRunning() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( false );
        when( connectionService.connect( Mockito.anyObject() ) ).thenReturn( true );

        connectionModeRunner.run( "192.168.2.2:1234", script, methodName );

        verify( waitForDisconnectingLatch, times( 0 ) ).await();
        verify( waitForScriptExecutionLatch, times( 0 ) ).await();
    }

    @Test
    public void waitingForNotDisconnectWhenConnectionNotEsatblished() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );
        when( connectionService.connect( Mockito.anyObject() ) ).thenReturn( false );

        connectionModeRunner.run( "192.168.2.2:1234", script, methodName );

        verify( waitForDisconnectingLatch, times( 0 ) ).await();
        verify( waitForScriptExecutionLatch, times( 0 ) ).await();
    }

    @Test
    public void waitinglatchReleasedOnTargetDisconnecting() throws Exception
    {
        connectionModeRunner.onTargetDisconnected( null, null );

        verify( waitForDisconnectingLatch ).countDown();
    }

    @Test
    public void waitinglatchReleasedOnScriptStop() throws Exception
    {
        connectionModeRunner.onScriptStopped( script );

        verify( waitForScriptExecutionLatch ).countDown();
    }

}
