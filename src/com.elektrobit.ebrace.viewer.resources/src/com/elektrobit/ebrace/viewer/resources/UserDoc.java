/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import com.elektrobit.ebrace.common.utils.FileHelper;

import lombok.extern.log4j.Log4j;

@Log4j
public class UserDoc
{

    private static final String USERDOC_PLUGIN_ID = "com.elektrobit.ebsolys.userdoc";
    private static final String PATH_TO_INDEX_FILE = "/target/generated-docs/index.html";

    public UserDoc()
    {
    }

    public String getDocURL()
    {
        URL localUserDoc = null;
        try
        {
            localUserDoc = FileHelper.locateFileInBundle( USERDOC_PLUGIN_ID, PATH_TO_INDEX_FILE ).toURL();

            if (localUserDocExists( localUserDoc ))
            {
                final String localUserDocPath = localUserDoc.toExternalForm();
                return localUserDocPath;
            }
            else
            {
                log.info( "No local user documentation found, because file not exists: " + localUserDoc );
            }
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            log.warn( "This should never happen, because userdoc URL should always have correct syntax!" );
        }

        return "NOT-FOUND";
    }

    private boolean localUserDocExists(URL localUserDoc) throws URISyntaxException
    {
        return new File( localUserDoc.toURI() ).exists();
    }

}
