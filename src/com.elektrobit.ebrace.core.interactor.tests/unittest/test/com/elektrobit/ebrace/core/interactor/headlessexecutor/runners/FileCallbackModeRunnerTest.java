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
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.FileCallbackModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common.GenericScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class FileCallbackModeRunnerTest
{
    private GenericScriptRunner genericScriptRunner;
    private RaceScriptInfo script;
    private FileCallbackModeRunner fileCallbackModeRunner;
    private String methodName;
    private LoadFileService loadFileService;
    private String filePath;
    private ScriptExecutorService scriptExecutor;
    private CountDownLatch waitForScriptExecutionLatch;

    @Before
    public void setup()
    {
        genericScriptRunner = mock( GenericScriptRunner.class );
        loadFileService = mock( LoadFileService.class );
        scriptExecutor = mock( ScriptExecutorService.class );
        waitForScriptExecutionLatch = mock( CountDownLatch.class );
        fileCallbackModeRunner = new FileCallbackModeRunner( loadFileService,
                                                             scriptExecutor,
                                                             genericScriptRunner,
                                                             waitForScriptExecutionLatch );

        filePath = "filePath";
        script = mock( RaceScriptInfo.class );
        methodName = "execute";

    }

    @Test
    public void paramsNotOkIfNoCallbackMethodExists() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );
        when( script.getCallbackMethods() ).thenReturn( Collections.emptyList() );

        assertFalse( "Expecting params not ok, if no callback methods exists.",
                     fileCallbackModeRunner.paramsOk( "", script, methodName ) );
    }

    @Test
    public void paramsNotOkIfGenericScriptRunnerParamsNotOk() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( false );
        when( script.getCallbackMethods() )
                .thenReturn( Arrays.asList( new RaceScriptMethod( methodName, "noDescription", "Script", null, "" ) ) );

        assertFalse( "Expecting params not ok, if genericScriptRunner params not ok.",
                     fileCallbackModeRunner.paramsOk( "", script, methodName ) );
    }

    @Test
    public void paramsOkIfGenericScriptRunnerParamsOkAndCallbackMethodAvailable() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );
        when( script.getCallbackMethods() )
                .thenReturn( Arrays.asList( new RaceScriptMethod( methodName, "noDescription", "Script", null, "" ) ) );

        assertTrue( "Expecting params ok, if callback methods exists and genericScriptRunner params ok.",
                    fileCallbackModeRunner.paramsOk( "", script, methodName ) );
    }

    @Test
    public void genericScriptRunnerIsCalled() throws Exception
    {
        fileCallbackModeRunner.run( "", script, methodName );

        verify( genericScriptRunner ).run( script, methodName );
    }

    @Test
    public void fileLoadingOnlyStartedIfScriptRunOk() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );

        fileCallbackModeRunner.run( filePath, script, methodName );

        verify( loadFileService ).loadFile( filePath );
    }

    @Test
    public void fileLoadingNotStartedIfScriptRunNotOk() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( false );

        fileCallbackModeRunner.run( filePath, script, methodName );

        verify( loadFileService, times( 0 ) ).loadFile( filePath );
    }

    @Test
    public void scriptStoppedOnLoadFileDone() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );
        when( script.isRunningAsCallbackScript() ).thenReturn( true );

        fileCallbackModeRunner.run( filePath, script, methodName );
        fileCallbackModeRunner.onLoadFileDone( 0, 0, 0, 0 );

        verify( scriptExecutor ).stopScript( script );
    }

    @Test
    public void waitForScriptExecutionAfterLoadFileDone() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );
        when( loadFileService.loadFile( filePath ) ).thenReturn( true );

        fileCallbackModeRunner.run( filePath, script, methodName );

        verify( waitForScriptExecutionLatch ).await();
    }

    @Test
    public void waitForScriptExecutionLatchReleasedOnScriptStopped() throws Exception
    {
        when( genericScriptRunner.run( script, methodName ) ).thenReturn( true );
        when( script.isRunningAsCallbackScript() ).thenReturn( true );

        fileCallbackModeRunner.run( filePath, script, methodName );
        fileCallbackModeRunner.onScriptStopped( script );

        verify( waitForScriptExecutionLatch ).countDown();
    }

}
