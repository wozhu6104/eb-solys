/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.importerregistry.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.importerregistry.internal.api.ImporterCollector;

public class ImporterRegistryImpl implements ImporterRegistry
{
    private final ImporterCollector importerCollector;

    public ImporterRegistryImpl(ImporterCollector importerCollector)
    {
        this.importerCollector = importerCollector;
    }

    @Override
    public Importer getImporterForFile(File file)
    {
        return getImporterForPath( file.getPath() );
    }

    private Importer getImporterForPath(String path)
    {
        return getImporterForFileExtension( getFileExtension( path ) );
    }

    @Override
    public Importer getImporterForFileExtension(String extension)
    {
        List<Importer> importers = getAllImporters();
        for (Importer importer : importers)
        {
            String thisExtension = importer.getSupportedFileExtension();
            if (extension.equalsIgnoreCase( thisExtension ))
            {
                return importer;
            }
        }

        throw new RuntimeException( "No importer found for file extension: ." + extension );
    }

    private List<Importer> getAllImporters()
    {
        if (importerCollector == null)
        {
            GenericOSGIServiceTracker<Importer> serviceTracker = new GenericOSGIServiceTracker<Importer>( Importer.class );
            Map<Object, Properties> servicesMap = serviceTracker.getServices( Importer.class.getName() );
            Set<Object> importersObj = servicesMap.keySet();
            List<Importer> importers = new ArrayList<Importer>();
            for (Object importerObj : importersObj)
            {
                importers.add( (Importer)importerObj );
            }
            return importers;
        }
        else
        {
            return importerCollector.getImporters();
        }
    }

    private String getFileExtension(String path)
    {
        int lastIndexOfPoint = path.lastIndexOf( '.' );
        int lastIndexOfFileSeperator = Math.max( path.lastIndexOf( '/' ), path.lastIndexOf( '\\' ) );

        return (lastIndexOfPoint > lastIndexOfFileSeperator) ? path.substring( lastIndexOfPoint + 1 ) : "";
    }

    @Override
    public List<List<String>> getSupportedFileTypesAndExtensions()
    {
        List<String> fileNames = new ArrayList<String>();
        List<String> extensions = new ArrayList<String>();
        List<Importer> importers = getAllImporters();
        for (Importer importer : importers)
        {
            extensions.add( importer.getSupportedFileExtension() );
            fileNames.add( importer.getSupportedFileTypeName() );
        }
        List<List<String>> namesAndExtensions = new ArrayList<List<String>>();
        namesAndExtensions.add( fileNames );
        namesAndExtensions.add( extensions );
        return namesAndExtensions;
    }

}
