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

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.importerregistry.internal.api.ImporterCollector;

@Component(immediate = true, enabled = true)
public class ImporterRegistryServiceImpl implements ImporterRegistry
{
    private final ImporterRegistryImpl importerRegistryImpl;

    public ImporterRegistryServiceImpl()
    {
        importerRegistryImpl = new ImporterRegistryImpl( new ImporterCollector()
        {

            @Override
            public List<Importer> getImporters()
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
        } );
    }

    @Override
    public Importer getImporterForFile(File file)
    {
        return importerRegistryImpl.getImporterForFile( file );
    }

    @Override
    public Importer getImporterForFileExtension(String extension)
    {
        return importerRegistryImpl.getImporterForFileExtension( extension );
    }

    @Override
    public List<List<String>> getSupportedFileTypesAndExtensions()
    {
        return importerRegistryImpl.getSupportedFileTypesAndExtensions();
    }
}
