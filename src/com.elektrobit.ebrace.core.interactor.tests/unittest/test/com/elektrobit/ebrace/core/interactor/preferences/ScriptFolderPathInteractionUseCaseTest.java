/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.preferences;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.preferences.ScriptFolderPathInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ScriptFolderPathInteractionUseCaseTest extends UseCaseBaseTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private ScriptFolderPathInteractionUseCaseImpl sutScriptFolderInteractionUseCase;
    private PreferencesService mockedPreferences;
    private File tempScriptFolder;

    @Before
    public void setup() throws Exception
    {
        mockedPreferences = Mockito.mock( PreferencesService.class );
        sutScriptFolderInteractionUseCase = new ScriptFolderPathInteractionUseCaseImpl( mockedPreferences );
        tempScriptFolder = folder.newFolder( "scriptFolder" );

    }

    @Test
    public void setValidPath() throws Exception
    {
        String path = tempScriptFolder.getPath();
        sutScriptFolderInteractionUseCase.setScriptFolderPath( path );
        Mockito.verify( mockedPreferences ).setScriptFolderPath( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidPath() throws Exception
    {
        String path = tempScriptFolder.getPath() + "ABC";
        sutScriptFolderInteractionUseCase.setScriptFolderPath( path );
    }

    @Test
    public void setScriptFolderPathToDefault() throws Exception
    {
        sutScriptFolderInteractionUseCase.setScriptFolderPathToDefault();
        Mockito.verify( mockedPreferences ).setScriptFolderPathToDefault();
    }

    @Test
    public void getScriptFolderPath() throws Exception
    {
        String pathFromPreferences = "saved path";
        Mockito.when( mockedPreferences.getDefaultScriptFolderPath() ).thenReturn( pathFromPreferences );
        String returnedPath = sutScriptFolderInteractionUseCase.getScriptFolderDefaultPath();
        Assert.assertEquals( pathFromPreferences, returnedPath );
    }
}
