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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.preferences.impl.PropertiesStore;

public class PropertiesStoreTest
{
    private static final String PREFERENCES_PROPERTIES = "preferences.properties";
    private static final String KEY_STRING = "key";
    private static final String VALUE_STRING = "value";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File preferenceFile;

    @Before
    public void setup() throws IOException
    {
        preferenceFile = folder.newFile( PREFERENCES_PROPERTIES );
    }

    @Test(expected = IllegalArgumentException.class)
    public void initWithNull()
    {
        new PropertiesStore( null );
    }

    @Test
    public void initWithFile()
    {
        new PropertiesStore( preferenceFile );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callStoreWithNulls()
    {
        PropertiesStore propertiesStore = new PropertiesStore( preferenceFile );
        propertiesStore.storePreferenceForKey( null, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callStoreWithNullInKey()
    {
        PropertiesStore propertiesStore = new PropertiesStore( preferenceFile );
        propertiesStore.storePreferenceForKey( null, VALUE_STRING, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callStoreWithNullInValue()
    {
        PropertiesStore propertiesStore = new PropertiesStore( preferenceFile );
        propertiesStore.storePreferenceForKey( KEY_STRING, null, null );
    }

    @Test
    public void verifyReadDeliversWrittenValue()
    {
        PropertiesStore propertiesStore = new PropertiesStore( preferenceFile );

        String value = propertiesStore.getPreferenceValue( KEY_STRING );
        assertNull( value );

        propertiesStore.storePreferenceForKey( KEY_STRING, VALUE_STRING, null );
        value = propertiesStore.getPreferenceValue( KEY_STRING );
        assertEquals( VALUE_STRING, value );
    }

    @Test
    public void verifyStoredEntryIsWrittenToFile()
    {
        PropertiesStore propertiesStore = new PropertiesStore( preferenceFile );
        assertEquals( 0, preferenceFile.length() );
        propertiesStore.storePreferenceForKey( KEY_STRING, VALUE_STRING, null );
        assertNotEquals( 0, preferenceFile.length() );
    }

    @After
    public void cleanup()
    {
        if (preferenceFile.exists())
        {
            preferenceFile.delete();
        }
    }

}
