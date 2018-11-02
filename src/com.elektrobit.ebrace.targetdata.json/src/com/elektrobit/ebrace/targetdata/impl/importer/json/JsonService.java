/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.importer.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;

@Component(service = Importer.class)
public class JsonService extends AbstractImporter
{
    private static final Logger LOG = Logger.getLogger( JsonService.class );
    private JsonEventHandler jsonEventHandler;

    @Override
    public void processFileContent(File file) throws IOException
    {
        LOG.trace( "Importing " + file.getAbsolutePath() );

        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = "";
        while ((line = reader.readLine()) != null)
        {
            jsonEventHandler.handle( line );
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
        return "jlf";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "File with JSON lines";
    }

    @Reference
    public void bindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = jsonEventHandler;
    }

    public void unbindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = null;
    }
}
