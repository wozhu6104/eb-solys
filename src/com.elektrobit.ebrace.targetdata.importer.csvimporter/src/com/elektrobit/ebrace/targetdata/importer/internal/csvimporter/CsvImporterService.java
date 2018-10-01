/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal.csvimporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebsolys.core.targetdata.api.importer.JsonEventHandler;

@Component(service = Importer.class)
public class CsvImporterService extends AbstractImporter
{
    private static final Logger LOG = Logger.getLogger( CsvImporterService.class );
    private JsonEventHandler jsonEventHandler = null;

    @Override
    public void processFileContent(File file) throws IOException
    {
        LOG.info( "importing CSV file: " + file.getAbsolutePath() );
        CsvToJsonTransformer transformer = new CsvToJsonTransformer();
        FileInputStream fileReader = new FileInputStream( file );
        InputStreamReader isr = new InputStreamReader( fileReader, "Cp1252" );
        BufferedReader br = new BufferedReader( isr );
        String line = br.readLine();
        transformer.acquireMetaData( line, "." );
        while ((line = br.readLine()) != null)
        {
            jsonEventHandler.handle( transformer.transformEvent( line ) );
        }
        br.close();
    }

    @Override
    protected long getMaximumTraceFileSizeInMB()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public String getSupportedFileExtension()
    {
        return "csv";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "Generic CSV File";
    }

    @Reference
    public void bindJsonService(JsonEventHandler runtimeEventAcceptor)
    {
        this.jsonEventHandler = runtimeEventAcceptor;
    }

    public void unbindJsonService(JsonEventHandler runtimeEventAcceptor)
    {
        this.jsonEventHandler = null;
    }
}
