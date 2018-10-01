/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

@Log4j
public class VersionHelper
{
    private static final String ABOUT_MAPPINGS_VERSION_KEY = "2";
    private static final String ABOUT_MAPPINGS_VARIANT_KEY = "3";

    private final Properties properties = new Properties();

    public VersionHelper(String pluginWithAboutMappingsID)
    {
        this( getAboutFileURI( pluginWithAboutMappingsID ) );
    }

    public VersionHelper(URI mappingsFileURI)
    {
        String path = mappingsFileURI.getPath();
        try
        {
            properties.load( new FileInputStream( path ) );
        }
        catch (IOException e)
        {
            log.error( e );
        }
    }

    public String getName()
    {
        String version = getVersionFromFile();
        return version == null ? "" : version;
    }

    public String getVariant()
    {
        String variant = properties.getProperty( ABOUT_MAPPINGS_VARIANT_KEY );
        return variant;
    }

    private String getVersionFromFile()
    {
        String version = properties.getProperty( ABOUT_MAPPINGS_VERSION_KEY );
        return version;
    }

    private static URI getAboutFileURI(String pluginWithAboutMappingsID)
    {
        return FileHelper.locateFileInBundle( pluginWithAboutMappingsID, "about.mappings" );
    }
}
