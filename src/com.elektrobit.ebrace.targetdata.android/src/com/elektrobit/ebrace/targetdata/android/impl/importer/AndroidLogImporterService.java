/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetdata.android.impl.common.AndroidLogParserFactory;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

@Component(service = Importer.class)
public class AndroidLogImporterService extends AbstractImporter
{
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger( AndroidLogImporterService.class );

    private UserMessageLogger userMessageLogger;

    private RuntimeEventAcceptor runtimeEventAcceptor;

    @Override
    public void processFileContent(File file) throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = "";
        AndroidLogLineParserAbstract parser = null;

        while (line != null)
        {
            line = reader.readLine();

            if (line != null && line.length() > 0)
            {

                // Find the right parser, since different input log versions are allowed
                if (parser == null)
                {
                    parser = AndroidLogParserFactory.createParser( line.trim(), runtimeEventAcceptor );
                }

                if (parser != null)
                {
                    parser.processLine( line, reader, null );
                }

            }
        }

        if (parser == null)
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR,
                                              "File content cannot be interpreted as android log format" );
        }

        reader.close();
    }

    @Override
    protected long getMaximumTraceFileSizeInMB()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public String getSupportedFileExtension()
    {
        return "log";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "Android Log Files";
    }

    @Reference
    public void bindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    public void unbindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

    @Reference
    public void bindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }
}
