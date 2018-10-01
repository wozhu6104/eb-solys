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

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptFileLoader;
import com.elektrobit.ebrace.common.thread.UninterruptibleCountDownLatch;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebsolys.script.external.Console;

public class ScriptFileLoaderTest
{
    private static final String PATH = "path";
    private ScriptFileLoader sutScriptFileLoader;
    private OpenFileInteractionUseCase mockedOpenFileUseCase;
    private UninterruptibleCountDownLatch mockedLatch;

    @Before
    public void setup()
    {
        Console mockedConsole = Mockito.mock( Console.class );
        mockedOpenFileUseCase = Mockito.mock( OpenFileInteractionUseCase.class );
        mockedLatch = Mockito.mock( UninterruptibleCountDownLatch.class );

        sutScriptFileLoader = new ScriptFileLoader( PATH, mockedConsole, mockedOpenFileUseCase, mockedLatch );
    }

    private void setupLoadingSuccess()
    {
        Mockito.doAnswer( (i) -> {
            sutScriptFileLoader.onFileLoadedSucessfully();
            return null;
        } ).when( mockedOpenFileUseCase ).openFile( PATH );
    }

    @Test
    public void testLoadFileResult() throws Exception
    {
        setupLoadingSuccess();
        Assert.assertTrue( sutScriptFileLoader.loadFile() );
    }

    @Test
    public void testLoadFileCallSequence() throws Exception
    {
        setupLoadingSuccess();
        sutScriptFileLoader.loadFile();
        InOrder executionInOrder = Mockito.inOrder( mockedOpenFileUseCase, mockedLatch );
        executionInOrder.verify( mockedOpenFileUseCase ).openFile( PATH );
        executionInOrder.verify( mockedLatch ).await();
        executionInOrder.verify( mockedOpenFileUseCase ).unregister();

        Mockito.verify( mockedLatch ).countDown();
    }

    @Test
    public void testOnFileLoadingFailed()
    {
        Mockito.doAnswer( (i) -> {
            sutScriptFileLoader.onFileLoadingFailed();
            return null;
        } ).when( mockedOpenFileUseCase ).openFile( PATH );

        Assert.assertFalse( sutScriptFileLoader.loadFile() );
        Mockito.verify( mockedLatch ).countDown();
    }

    @Test
    public void testOnFileAlreadyLoaded()
    {
        Mockito.doAnswer( (i) -> {
            sutScriptFileLoader.onFileAlreadyLoaded( PATH );
            return null;
        } ).when( mockedOpenFileUseCase ).openFile( PATH );

        Assert.assertFalse( sutScriptFileLoader.loadFile() );
        Mockito.verify( mockedLatch ).countDown();
    }

    @Test
    public void testOnFileEmpty()
    {
        Mockito.doAnswer( (i) -> {
            sutScriptFileLoader.onFileEmpty( PATH );
            return null;
        } ).when( mockedOpenFileUseCase ).openFile( PATH );

        Assert.assertFalse( sutScriptFileLoader.loadFile() );
        Mockito.verify( mockedLatch ).countDown();
    }
}
