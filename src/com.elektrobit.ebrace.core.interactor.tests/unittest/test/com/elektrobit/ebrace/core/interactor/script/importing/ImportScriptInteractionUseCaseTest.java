/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.script.importing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.script.importing.ImportScriptInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.scriptimporter.api.ImportScriptStatusListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptImporterService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ImportScriptInteractionUseCaseTest extends UseCaseBaseTest
{

    private ImportScriptInteractionUseCase importScriptInteractionUseCase;
    private ScriptImporterService scriptImporter;

    @Before
    public void setup()
    {
        ImportScriptInteractionCallback callback = mock( ImportScriptInteractionCallback.class );
        scriptImporter = mock( ScriptImporterService.class );

        importScriptInteractionUseCase = new ImportScriptInteractionUseCaseImpl( callback, scriptImporter );
    }

    @Test
    public void callbackCalledOnSuccess() throws Exception
    {
        File scriptFile = new File( "MyScript.xtend" );

        importScriptInteractionUseCase.importUserScript( scriptFile );

        verify( scriptImporter ).importUserScript( scriptFile );

    }

    @Test
    public void serviceListenerRegistered() throws Exception
    {
        verify( scriptImporter ).addListener( (ImportScriptStatusListener)importScriptInteractionUseCase );
    }

    @Test
    public void serviceListenerUnregistered() throws Exception
    {
        importScriptInteractionUseCase.unregister();

        verify( scriptImporter ).removeListener( (ImportScriptStatusListener)importScriptInteractionUseCase );
    }

}
