/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal.raceusecaselogimporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import lombok.extern.log4j.Log4j;

@Log4j
@Component(service = Importer.class)
public class RACEUseCaseLogImporterService extends AbstractImporter
{
    private final SimpleDateFormat formatter = new SimpleDateFormat( "MM-dd-yyyy_HH:mm:ss.SSS" );
    private RuntimeEventAcceptor runtimeEventAcceptor;

    @Override
    public void processFileContent(File file) throws IOException
    {
        try (BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "UTF8" ) ))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith( "#" ))
                {
                    continue;
                }

                String[] split = line.split( "\\|" );

                if (split.length == 2)
                {
                    Long timeInMillis = null;
                    String timeInMillisFormatted = split[0];
                    try
                    {
                        timeInMillis = formatter.parse( timeInMillisFormatted ).getTime();
                    }
                    catch (ParseException e)
                    {
                        log.error( "Couldn't parse line. Ignoring line. Line was " + line + "." );
                        continue;
                    }
                    String message = split[1];

                    RuntimeEventChannel<String> channel = runtimeEventAcceptor
                            .createOrGetRuntimeEventChannel( "trace.race.usecase", Unit.TEXT, "" );

                    runtimeEventAcceptor.acceptEventMicros( timeInMillis * 1000, channel, null, message );
                }
                else
                {
                    log.error( "Couldn't parse line. Ignoring line. Line was " + line + "." );
                }
            }
        }
    }

    @Override
    protected long getMaximumTraceFileSizeInMB()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public String getSupportedFileExtension()
    {
        return "rusec";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "EB solys UseCase Logger";
    }

    @Reference
    public void bind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }
}
