/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.preferences;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.preferences.impl.PreferencesServiceImpl;
import com.elektrobit.ebrace.core.preferences.impl.PropertiesStore;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;

public class ScriptFolderPathPreferencesTest
{
    private static final String DEFAULT_PATH = "default path";
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private PreferencesServiceImpl preferencesService;
    private File tmpPreferenceFile;

    @Before
    public void setup() throws Exception
    {
        tmpPreferenceFile = tempFolder.newFile( "preferences.properties" );
        preferencesService = new PreferencesServiceImpl( new PropertiesStore( tmpPreferenceFile ), () -> DEFAULT_PATH );
    }

    @Test
    public void initialPathValue() throws Exception
    {
        Assert.assertEquals( DEFAULT_PATH, preferencesService.getScriptFolderPath() );
    }

    @Test
    public void setGet() throws Exception
    {
        String newPath = "new Path";
        preferencesService.setScriptFolderPath( newPath );
        String loadedPath = preferencesService.getScriptFolderPath();

        Assert.assertEquals( newPath, loadedPath );
    }

    @Test
    public void notifyListener() throws Exception
    {
        PreferencesListener mockedListener = Mockito.mock( PreferencesListener.class );
        preferencesService.registerPreferencesListener( mockedListener );

        String path = "path";
        preferencesService.setScriptFolderPath( path );
        Mockito.verify( mockedListener ).onScriptFolderPathChanged( path );
        Mockito.verifyNoMoreInteractions( mockedListener );
    }

    @Test
    public void getDefaultPath() throws Exception
    {
        Assert.assertEquals( DEFAULT_PATH, preferencesService.getDefaultScriptFolderPath() );
    }

    @Test
    public void getResetPath() throws Exception
    {
        preferencesService.setScriptFolderPath( "another path" );
        preferencesService.setScriptFolderPathToDefault();
        Assert.assertEquals( DEFAULT_PATH, preferencesService.getDefaultScriptFolderPath() );
    }
}
