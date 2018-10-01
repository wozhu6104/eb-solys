/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.dropinsloader.impl;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.dropinsloader.api.DropinsLoaderService;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;

import lombok.extern.log4j.Log4j;

@Log4j
@Component(immediate = true)
public class DropinsLoaderServiceImpl implements DropinsLoaderService
{
    private final static String DEFAULT_PATH_TO_IMPORTER_FOLDER = Platform.getLocation().toOSString() + File.separator
            + ".." + File.separator + "dropins";
    private UserMessageLogger userMessageLogger;

    @Reference(unbind = "unbind")
    public void bind(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    @Activate
    public void start(ComponentContext context)
    {
        File dropinsFolder = new File( DEFAULT_PATH_TO_IMPORTER_FOLDER );

        if (!dropinsFolder.exists())
        {
            dropinsFolder.mkdir();
        }

        if (dropinsFolder.isDirectory())
        {
            for (File nextFile : dropinsFolder.listFiles())
            {
                if (isJarFile( nextFile ))
                {
                    startDropinsBundle( context, nextFile );
                }
            }
        }

    }

    private boolean isJarFile(File nextFile)
    {
        return !nextFile.isDirectory() && nextFile.getName().endsWith( ".jar" );
    }

    private void startDropinsBundle(ComponentContext context, File jarFile)
    {
        try
        {
            Bundle bundle = context.getBundleContext().installBundle( "file:///" + jarFile.getAbsolutePath() );
            bundle.start();
        }
        catch (BundleException e)
        {
            logException( jarFile, e );
        }
    }

    private void logException(File jarFile, BundleException e)
    {
        log.info( "Couldn't load user importer " + jarFile.getAbsolutePath() + ".", e );
        if (userMessageLogger != null)
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR,
                                              "Couldn't load user importer " + jarFile.getAbsolutePath() + "." );
        }
    }

    public void unbind(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }
}
