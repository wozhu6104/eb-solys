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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.thread.UninterruptibleCountDownLatch;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebsolys.script.external.Console;
import com.google.common.annotations.VisibleForTesting;

public class ScriptFileLoader implements OpenFileInteractionCallback
{
    private final String path;
    private final Console scriptConsole;
    private final OpenFileInteractionUseCase loadFileInteractionUseCase;
    private final UninterruptibleCountDownLatch fileLoadLatch;
    private volatile boolean resultOk = true;

    public ScriptFileLoader(String path, Console scriptConsole)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "path", path );
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptConsole", scriptConsole );
        this.path = path;
        this.scriptConsole = scriptConsole;
        loadFileInteractionUseCase = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
        fileLoadLatch = new UninterruptibleCountDownLatch( 1 );
    }

    @VisibleForTesting
    public ScriptFileLoader(String path, Console scriptConsole, OpenFileInteractionUseCase testUseCase,
            UninterruptibleCountDownLatch testLatch)
    {
        this.path = path;
        this.scriptConsole = scriptConsole;
        fileLoadLatch = testLatch;
        loadFileInteractionUseCase = testUseCase;
    }

    public boolean loadFile()
    {
        loadFileInteractionUseCase.openFile( path );
        waitForFileLoadDone();
        unregister();
        return resultOk;
    }

    private void waitForFileLoadDone()
    {
        fileLoadLatch.await();
    }

    private void unregister()
    {
        loadFileInteractionUseCase.unregister();
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
        scriptConsole.println( "ERROR: File is too big and cannot be loaded " + pathToFile );
        fileLoadLatch.countDown();
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
    {
    }

    @Override
    public void onFileLoadedSucessfully()
    {
        fileLoadLatch.countDown();
    }

    @Override
    public void onFileLoadingFailed()
    {
        resultOk = false;
        fileLoadLatch.countDown();
    }

    @Override
    public void onFileAlreadyLoaded(String pathToFile)
    {
        resultOk = false;
        fileLoadLatch.countDown();
    }

    @Override
    public void onFileEmpty(String pathToFile)
    {
        resultOk = false;
        fileLoadLatch.countDown();
    }

    @Override
    public void onFileNotFound(String pathToFile)
    {
        resultOk = false;
        fileLoadLatch.countDown();
    }
}
