/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global;

import java.util.concurrent.CountDownLatch;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common.GenericScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutionListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;

public class GlobalScriptRunner implements ScriptExecutionListener
{
    private final CountDownLatch waitForScriptExecutionLatch;
    private final ScriptExecutorService scriptExecutorService;
    private final GenericScriptRunner genericScriptRunner;

    public GlobalScriptRunner(ScriptExecutorService scriptExecutorService)
    {
        this( scriptExecutorService,
                new GenericScriptRunner( new GlobalScriptRunPart( scriptExecutorService ) ),
                new CountDownLatch( 1 ) );
    }

    public GlobalScriptRunner(ScriptExecutorService scriptExecutorService, GenericScriptRunner genericScriptRunner,
            CountDownLatch waitForScriptExecutionLatch)
    {
        this.scriptExecutorService = scriptExecutorService;
        this.genericScriptRunner = genericScriptRunner;
        this.waitForScriptExecutionLatch = waitForScriptExecutionLatch;
        scriptExecutorService.addScriptExecutionListener( this );
    }

    public boolean paramsOk(RaceScriptInfo script, String methodName)
    {
        return genericScriptRunner.paramsOk( script, methodName );
    }

    public boolean run(RaceScriptInfo script, String methodName)
    {
        boolean result = genericScriptRunner.run( script, methodName );
        waitForScriptExecution();
        scriptExecutorService.removeScriptExecutionListener( this );
        return result;
    }

    private void waitForScriptExecution()
    {
        try
        {
            waitForScriptExecutionLatch.await();
        }
        catch (InterruptedException e)
        {
        }
    }

    @Override
    public void onScriptStarted(RaceScriptInfo script)
    {
    }

    @Override
    public void onScriptStopped(RaceScriptInfo script)
    {
        waitForScriptExecutionLatch.countDown();
    }

}
