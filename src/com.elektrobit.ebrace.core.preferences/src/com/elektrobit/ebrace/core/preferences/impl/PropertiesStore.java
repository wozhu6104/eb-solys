/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class PropertiesStore
{
    private final static Logger LOG = Logger.getLogger( PreferencesServiceImpl.class );

    private final static String PREFERENCES_FILE_NAME = "ebsolys.properties";
    private final static String PREFERENCES_FILE_ERROR = "Could not load or find preferences file!";

    private final File propertiesFile;
    private final Properties properties = new Properties();

    public PropertiesStore()
    {
        this( new File( Platform.getInstanceLocation().getURL().getFile() + File.separator + PREFERENCES_FILE_NAME ) );
    }

    public PropertiesStore(File propertiesFile)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "propertiesFile", propertiesFile );
        this.propertiesFile = propertiesFile;

        createPropertiesFileIfNotExists();
        readPropertiesFile();
    }

    private void createPropertiesFileIfNotExists()
    {
        if (!propertiesFile.exists())
        {
            try
            {
                // Should only be needed for plug-in tests
                propertiesFile.getParentFile().mkdirs();
                propertiesFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void readPropertiesFile()
    {
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream( propertiesFile );
            properties.load( fileInputStream );
        }
        catch (FileNotFoundException e)
        {
            // Couldn't happen because it's created before.
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fileInputStream != null)
            {
                try
                {
                    fileInputStream.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    private void storePropertiesToFile(String description)
    {
        createPropertiesFileIfNotExists();

        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream( propertiesFile );
            properties.store( fileOutputStream, description );
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LOG.warn( PREFERENCES_FILE_ERROR );
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    public void storePreferenceForKey(String key, Object value, String description)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "key", key );
        RangeCheckUtils.assertReferenceParameterNotNull( "value", value );

        properties.put( key, value.toString() );
        storePropertiesToFile( description );
    }

    public String getPreferenceValue(String key)
    {
        return properties.getProperty( key );
    }
}
