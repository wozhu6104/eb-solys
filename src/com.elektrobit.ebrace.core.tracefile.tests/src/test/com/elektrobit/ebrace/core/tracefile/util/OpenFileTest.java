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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.internal.LoadFileServiceImpl;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;

public class OpenFileTest
{
    private File dummyFile;
    private LoadFileProgressListener progressListener;
    private ResourcesModelManager mockedResourceManager;

    @Before
    public void setup()
    {
        dummyFile = createDummyFile();
        progressListener = Mockito.mock( LoadFileProgressListener.class );
    }

    @Test
    public void loadFileStartedCalledAfterOpenFile() throws Exception
    {
        Importer importer = Mockito.mock( Importer.class );

        LoadFileServiceImpl loadFileService = createLoadFileServiceSUT( progressListener, importer );

        loadFileService.openFile( dummyFile.getAbsolutePath() );

        Mockito.verify( progressListener ).onLoadFileStarted( dummyFile.getAbsolutePath() );

    }

    @Test
    public void testFailedLoadingInImporterCreatesNoFileEntry() throws Exception
    {
        Importer importer = Mockito.mock( Importer.class );
        Mockito.when( importer.isLoadingAtLeastPartiallySuccessful() ).thenReturn( false );

        LoadFileServiceImpl loadFileService = createLoadFileServiceSUT( progressListener, importer );

        loadFileService.openFile( dummyFile.getAbsolutePath() );

        Mockito.verify( mockedResourceManager, Mockito.times( 0 ) ).createFileModel( Mockito.anyString(),
                                                                                     Mockito.anyString() );
        Assert.assertTrue( loadFileService.isFileNotLoaded( dummyFile.getAbsolutePath() ) );
        Assert.assertTrue( loadFileService.isFileLoadingFailed( dummyFile.getAbsolutePath() ) );
    }

    @Test
    public void loadFileDoneCalledAfterOpenFile() throws Exception
    {
        Importer importer = Mockito.mock( Importer.class );
        Mockito.when( importer.isLoadingAtLeastPartiallySuccessful() ).thenReturn( true );

        LoadFileServiceImpl loadFileService = createLoadFileServiceSUT( progressListener, importer );

        loadFileService.openFile( dummyFile.getAbsolutePath() );

        Mockito.verify( progressListener ).onLoadFileDone( Mockito.anyLong(),
                                                           Mockito.anyLong(),
                                                           Mockito.anyLong(),
                                                           Mockito.anyLong() );

    }

    @Test
    public void isFileTooBig() throws Exception
    {
        Importer importer = Mockito.mock( Importer.class );
        Mockito.when( importer.isFileTooBig( dummyFile ) ).thenReturn( true );

        LoadFileServiceImpl loadFileService = createLoadFileServiceSUT( progressListener, importer );

        UserMessageLogger userMessageLogger = Mockito.mock( UserMessageLogger.class );
        loadFileService.bindUserMessageLogger( userMessageLogger );

        loadFileService.openFile( dummyFile.getAbsolutePath() );

        Mockito.verify( userMessageLogger ).logUserMessage( UserMessageLoggerTypes.ERROR,
                                                            "An error has occured while loading the file" );

    }

    private LoadFileServiceImpl createLoadFileServiceSUT(LoadFileProgressListener progressListener, Importer importer)
    {
        LoadFileServiceImpl loadFileService = new LoadFileServiceImpl();

        ImporterRegistry importerRegistry = Mockito.mock( ImporterRegistry.class );

        Mockito.when( importerRegistry.getImporterForFile( Mockito.any( File.class ) ) ).thenReturn( importer );

        loadFileService.bindImporterRegistry( importerRegistry );

        loadFileService.bindUserInteractionPreferences( Mockito.mock( UserInteractionPreferences.class ) );
        mockedResourceManager = Mockito.mock( ResourcesModelManager.class );
        loadFileService.bindResourcesModelManager( mockedResourceManager );

        loadFileService.registerFileProgressListener( progressListener );
        return loadFileService;
    }

    private File createDummyFile()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        for (int i = 0; i < 10000; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                     ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );
        }

        return builder.createFile( fileName );
    }

    @After
    public void cleanup()
    {
    }

}
