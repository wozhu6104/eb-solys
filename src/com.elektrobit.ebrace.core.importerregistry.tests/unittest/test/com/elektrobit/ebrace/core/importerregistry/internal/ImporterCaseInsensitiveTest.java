/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.importerregistry.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.internal.impl.ImporterRegistryImpl;

public class ImporterCaseInsensitiveTest
{
    private Importer importer;
    private ImporterRegistryImpl importerRegistry;

    @Before
    public void setup()
    {
        importer = mock( Importer.class );
        when( importer.getSupportedFileExtension() ).thenReturn( "dlt" );
        importerRegistry = new ImporterRegistryImpl( () -> Arrays.asList( importer ) );
    }

    @Test
    public void importerFoundByExtension() throws Exception
    {
        assertEquals( importer, importerRegistry.getImporterForFileExtension( "dlt" ) );
    }

    @Test
    public void importerFoundByExtensionCaseInsensitive() throws Exception
    {
        assertEquals( importer, importerRegistry.getImporterForFileExtension( "DLT" ) );
    }
}
