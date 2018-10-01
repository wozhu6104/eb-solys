/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.resources.model;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.BetaFeatureConfigurator;
import com.elektrobit.ebrace.resources.manager.ResourcesModelManagerImpl;

public class ResourceModelManagerResetTest
{
    private ResourcesModelManagerImpl resourcesModelManager = new ResourcesModelManagerImpl();

    @Before
    public void setup()
    {
        BetaFeatureConfigurator.setBetaActive( true );
        resourcesModelManager = new ResourcesModelManagerImpl();
        resourcesModelManager.bindPreferencesService( mock( PreferencesService.class ) );
    }

    @Test
    public void checkIfTraceFilesAreDeletedOnReset() throws Exception
    {
        resourcesModelManager.activate();
        resourcesModelManager.createFileModel( "MyFile", "MyPath" );
        resourcesModelManager.onReset();

        assertTrue( resourcesModelManager.getFiles().isEmpty() );
    }
}
