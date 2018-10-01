/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common.GenericScriptRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global.GlobalScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;

public class GlobalScriptRunnerTest
{
    private GenericScriptRunner genericScriptRunner;
    private RaceScriptInfo script;
    private String methodName;
    private GlobalScriptRunner globalScriptRunner;
    private CountDownLatch waitForScriptExecutionLatch;

    @Before
    public void setup()
    {
        genericScriptRunner = mock( GenericScriptRunner.class );
        script = mock( RaceScriptInfo.class );
        methodName = "execute";

        waitForScriptExecutionLatch = mock( CountDownLatch.class );
        globalScriptRunner = new GlobalScriptRunner( mock( ScriptExecutorService.class ),
                                                     genericScriptRunner,
                                                     waitForScriptExecutionLatch );
    }

    @Test
    public void paramsOkIfGenericScriptRunnerParamsOk() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertTrue( globalScriptRunner.paramsOk( script, methodName ) );
    }

    @Test
    public void paramsNotOkIfGenericScriptRunnerParamsNotOk() throws Exception
    {
        when( genericScriptRunner.paramsOk( script, methodName ) ).thenReturn( false );

        assertFalse( globalScriptRunner.paramsOk( script, methodName ) );
    }

    @Test
    public void isWaitedForScriptExecution() throws Exception
    {
        globalScriptRunner.run( script, methodName );

        verify( waitForScriptExecutionLatch ).await();
    }

    @Test
    public void isWaitForScriptExecutionReleasedOnScriptStopped() throws Exception
    {
        globalScriptRunner.onScriptStopped( script );

        verify( waitForScriptExecutionLatch ).countDown();
    }

}
