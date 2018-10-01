/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl;

import java.util.concurrent.CountDownLatch;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.api.AutomationModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.callback.CallbackScriptRunPart;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common.GenericScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutionListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class FileCallbackModeRunner implements AutomationModeRunner, LoadFileProgressListener, ScriptExecutionListener
{

    private final LoadFileService loadFileService;
    private final GenericScriptRunner genericScriptRunner;
    private final ScriptExecutorService scriptExecutorService;
    private RaceScriptInfo runningScript;
    private final CountDownLatch waitForScriptExecutionLatch;

    public FileCallbackModeRunner(LoadFileService loadFileService, ScriptExecutorService scriptExecutorService)
    {
        this( loadFileService,
                scriptExecutorService,
                new GenericScriptRunner( new CallbackScriptRunPart( scriptExecutorService ) ),
                new CountDownLatch( 1 ) );
    }

    public FileCallbackModeRunner(LoadFileService loadFileService, ScriptExecutorService scriptExecutorService,
            GenericScriptRunner genericScriptRunner, CountDownLatch waitForScriptExecutionLatch)
    {
        this.scriptExecutorService = scriptExecutorService;
        this.loadFileService = loadFileService;
        this.genericScriptRunner = genericScriptRunner;
        this.waitForScriptExecutionLatch = waitForScriptExecutionLatch;
        loadFileService.registerFileProgressListener( this );
        scriptExecutorService.addScriptExecutionListener( this );
    }

    @Override
    public boolean paramsOk(String filePath, RaceScriptInfo script, String methodName)
    {
        return !script.getCallbackMethods().isEmpty() && genericScriptRunner.paramsOk( script, methodName );
    }

    @Override
    public boolean run(String filePath, RaceScriptInfo script, String methodName)
    {
        boolean resultOk = genericScriptRunner.run( script, methodName );
        if (resultOk)
        {
            this.runningScript = script;
            resultOk = loadFileService.loadFile( filePath );
            if (resultOk)
            {
                waitForScriptExecution();
            }
        }

        return resultOk;
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
    public void onLoadFileStarted(String pathToFile)
    {
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
    }

    @Override
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        if (runningScript != null && runningScript.isRunningAsCallbackScript())
        {
            scriptExecutorService.stopScript( runningScript );
        }
        loadFileService.unregisterFileProgressListener( this );
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

    @Override
    public void onScriptStarted(RaceScriptInfo script)
    {
    }

    @Override
    public void onScriptStopped(RaceScriptInfo stoppedScript)
    {
        if (runningScript != null && runningScript.equals( stoppedScript ))
        {
            waitForScriptExecutionLatch.countDown();
            this.runningScript = null;
        }
    }

}
