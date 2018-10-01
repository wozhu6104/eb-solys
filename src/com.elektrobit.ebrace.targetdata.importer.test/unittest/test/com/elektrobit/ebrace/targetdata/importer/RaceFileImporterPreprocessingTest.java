/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.importer.internal.racefile.RTimeRange;
import com.elektrobit.ebrace.targetdata.importer.internal.racefile.RaceDataChunkImporter;

public class RaceFileImporterPreprocessingTest
{
    private File dummyFile;

    @Test
    public void isChunkLoadingSupported() throws Exception
    {
        RaceDataChunkImporter raceFileImporter = new RaceDataChunkImporter();
        Assert.assertTrue( raceFileImporter.isChunkLoadingSupported() );
    }

    @Test
    public void getFileTimeRange() throws Exception
    {
        dummyFile = createDummyFile();

        RaceDataChunkImporter raceFileImporter = new RaceDataChunkImporter();
        TimestampProvider timestampProvider = Mockito.mock( TimestampProvider.class );
        Mockito.when( timestampProvider.getHostTimestampCreator() ).thenReturn( new MockedTimestampCreator() );
        raceFileImporter.bind( timestampProvider );

        RTimeRange fileTimeRange = raceFileImporter.getFileTimeRange( dummyFile );

        Assert.assertEquals( new Long( 1000000 ), fileTimeRange.getStartTime() );
        Assert.assertEquals( new Long( 10999000 ), fileTimeRange.getEndTime() );
    }

    @Test
    public void isLoadFromWorking() throws Exception
    {
        dummyFile = createDummyFile();

        RaceDataChunkImporter raceFileImporter = new RaceDataChunkImporter();
        raceFileImporter.bind( Mockito.mock( ProtocolMessageDispatcher.class ) );
        raceFileImporter.bind( Mockito.mock( UserInteractionPreferences.class ) );
        TimestampProvider timestampProvider = Mockito.mock( TimestampProvider.class );
        Mockito.when( timestampProvider.getHostTimestampCreator() ).thenReturn( new MockedTimestampCreator() );
        raceFileImporter.bind( timestampProvider );

        raceFileImporter.getFileTimeRange( dummyFile );
        raceFileImporter.importFrom( 3000000, null, dummyFile );

        Assert.assertEquals( new Long( 3000000 ), raceFileImporter.getChunkStartTime() );
        Assert.assertEquals( new Long( 10999000 ), raceFileImporter.getChunkEndTime() );
    }

    @Test
    public void loadDesiredChunkTimeLength() throws Exception
    {
        dummyFile = createDummyFile();

        RaceDataChunkImporter raceFileImporter = new RaceDataChunkImporter();
        raceFileImporter.bind( Mockito.mock( ProtocolMessageDispatcher.class ) );
        raceFileImporter.bind( Mockito.mock( UserInteractionPreferences.class ) );
        TimestampProvider timestampProvider = Mockito.mock( TimestampProvider.class );
        Mockito.when( timestampProvider.getHostTimestampCreator() ).thenReturn( new MockedTimestampCreator() );
        raceFileImporter.bind( timestampProvider );

        raceFileImporter.getFileTimeRange( dummyFile );
        raceFileImporter.importFrom( 3000000, 1000000L, dummyFile );

        Assert.assertEquals( new Long( 3000000 ), raceFileImporter.getChunkStartTime() );
        Assert.assertEquals( new Long( 4000000 ), raceFileImporter.getChunkEndTime() );
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
        if (dummyFile != null && dummyFile.exists())
            dummyFile.delete();
    }

}
