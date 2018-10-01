/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.tracefile.util;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.tracefile.internal.LoadFileServiceImpl;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;

public class LoadFileFromTest
{
    private File dummyFile;
    private LoadFileServiceImpl loadFileService;
    private Importer importer;

    @Before
    public void setup()
    {
        dummyFile = createDummyFile();

        loadFileService = new LoadFileServiceImpl();
        importer = Mockito.mock( Importer.class );
        ResourcesModelManager resourcesModelManager = Mockito.mock( ResourcesModelManager.class );
        Mockito.when( resourcesModelManager.isCallbackScriptRunning() ).thenReturn( true );
        Mockito.when( importer.isChunkLoadingSupported() ).thenReturn( true );
        ImporterRegistry importerRegistry = Mockito.mock( ImporterRegistry.class );
        Mockito.when( importerRegistry.getImporterForFile( dummyFile ) ).thenReturn( importer );
        loadFileService.bindImporterRegistry( importerRegistry );
        loadFileService.bindResourcesModelManager( resourcesModelManager );
        loadFileService.bindUserInteractionPreferences( Mockito.mock( UserInteractionPreferences.class ) );
    }

    @Test
    public void isImporterCalled() throws Exception
    {
        loadFileService.loadFile( dummyFile.getAbsolutePath() );

        Mockito.verify( importer ).importFrom( 0, null, dummyFile );
    }

    private File createDummyFile()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        for (int i = 0; i < 10000; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp, MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN, new byte[0] );
        }

        return builder.createFile( fileName );
    }

    @After
    public void cleanup()
    {
        if (dummyFile.exists())
        {
            dummyFile.delete();
        }
    }

}
