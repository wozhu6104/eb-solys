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

import lombok.extern.log4j.Log4j;

@Log4j
public class UserDoc
{

    private static final String PATH_TO_USERDOC_START_PAGE = "userdoc/intro/index.html";

    private static final String LATEST_USER_DOC = "http://er01545p:8989/userContent/ci/latest-stable/"
            + PATH_TO_USERDOC_START_PAGE;

    private URL installationPath = null;

    public UserDoc(URL installationPath)
    {
        this.installationPath = installationPath;
    }

    public String getDocURL()
    {
        URL localUserDoc = null;
        try
        {
            final String pathToUserdoc = installationPath.toExternalForm() + PATH_TO_USERDOC_START_PAGE;
            localUserDoc = new URL( pathToUserdoc );
            if (localUserDocExists( localUserDoc ))
            {
                final String localUserDocPath = localUserDoc.toExternalForm();
                return localUserDocPath;
            }
            else
            {
                log.info( "No local user documentation found, because file not exists: " + pathToUserdoc );
            }
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            log.warn( "This should never happen, because userdoc URL should always have correct syntax!" );
        }

        return LATEST_USER_DOC;
    }

    private boolean localUserDocExists(URL localUserDoc) throws URISyntaxException
    {
        return new File( localUserDoc.toURI() ).exists();
    }
}
