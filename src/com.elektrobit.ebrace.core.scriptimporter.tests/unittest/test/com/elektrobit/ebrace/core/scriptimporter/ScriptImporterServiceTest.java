/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.scriptimporter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.scriptimporter.api.ImportScriptStatusListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptImporterService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.core.scriptimporter.impl.importer.ScriptImporterImpl;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class ScriptImporterServiceTest
{
    private static final String PATH_TO_SCRIPT_FILES = "files/";
    private final ResourcesModelManager resourcesModelManager = mock( ResourcesModelManager.class );
    private final ScriptProjectBuilderService scriptProjectBuilderImpl = mock( ScriptProjectBuilderService.class );
    private final ImportScriptStatusListener statusListener = mock( ImportScriptStatusListener.class );
    private final RaceScriptLoader raceScriptLoader = mock( RaceScriptLoader.class );
    private final ScriptImporterService scriptImporterService = new ScriptImporterImpl( resourcesModelManager,
                                                                                        raceScriptLoader,
                                                                                        scriptProjectBuilderImpl );

    @Before
    public void setup()
    {
        scriptImporterService.addListener( statusListener );
    }

    @Test
    public void importFailsIfFileNotExists() throws Exception
    {
        File xtendScript = new File( PATH_TO_SCRIPT_FILES + "ScriptNotExists.xtend" );

        scriptImporterService.importUserScript( xtendScript );

        verify( statusListener ).onScriptImportFailed();
    }

    @Test
    public void scriptAlreadyThere() throws Exception
    {
        when( resourcesModelManager.scriptAlreadyExists( "MyScript" ) ).thenReturn( true );

        File xtendScript = new File( PATH_TO_SCRIPT_FILES + "MyScript.xtend" );

        scriptImporterService.importUserScript( xtendScript );

        verify( statusListener ).onScriptAlreadyExists();
    }

    @After
    public void cleanup()
    {
        scriptImporterService.removeListener( statusListener );
    }

}
