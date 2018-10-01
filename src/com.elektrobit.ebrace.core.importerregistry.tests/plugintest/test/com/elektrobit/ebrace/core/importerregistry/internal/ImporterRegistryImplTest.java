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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;

public class ImporterRegistryImplTest
{
    private static final String RACE_FILE_EXTENSION = "bin";
    private static final String RACE_FILE_NAME = "EB solys File";

    @Test
    public void testGetFileExtensions()
    {
        ImporterRegistry importerRegistrySUT = new GenericOSGIServiceTracker<ImporterRegistry>( ImporterRegistry.class )
                .getService();
        List<List<String>> typesAndExtensions = importerRegistrySUT.getSupportedFileTypesAndExtensions();
        List<String> types = typesAndExtensions.get( 0 );
        List<String> extensions = typesAndExtensions.get( 1 );

        Assert.assertTrue( types.contains( RACE_FILE_NAME ) );
        Assert.assertTrue( extensions.contains( RACE_FILE_EXTENSION ) );

        int indexOfType = types.indexOf( RACE_FILE_NAME );
        int indexOfExtension = extensions.indexOf( RACE_FILE_EXTENSION );
        Assert.assertEquals( indexOfType, indexOfExtension );

        int typesSize = types.size();
        int extensionsSize = extensions.size();
        Assert.assertEquals( typesSize, extensionsSize );
    }
}
