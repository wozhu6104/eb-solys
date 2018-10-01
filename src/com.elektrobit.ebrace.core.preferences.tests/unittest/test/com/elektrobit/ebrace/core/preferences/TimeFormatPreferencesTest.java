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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.preferences.impl.PreferencesServiceImpl;
import com.elektrobit.ebrace.core.preferences.impl.PropertiesStore;

public class TimeFormatPreferencesTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File tmpPreferenceFile;

    @Before
    public void setup()
    {
        try
        {
            tmpPreferenceFile = folder.newFile( "preferences.properties" );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void isDefaultValueCorrect() throws Exception
    {
        assertEquals( "HH:mm:ss.SSS",
                      new PreferencesServiceImpl( new PropertiesStore( tmpPreferenceFile ), () -> "default path" )
                              .getTimestampFormatPreferences() );
    }

    @After
    public void cleanup()
    {
        if (tmpPreferenceFile.exists())
        {
            tmpPreferenceFile.delete();
        }
    }

}
