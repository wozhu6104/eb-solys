/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.racefileimporter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.ResourceMonitorMessage;

public class RaceFileImporterPluginTest
{
    private static RuntimeEventAcceptor runtimeEventAcceptor;
    private static LoadFileService loadFileService;
    private final static String FILE_NAME = "race-test-file.bin";

    private String pathToRaceFile;

    @Before
    public void setup()
    {
        runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
        loadFileService = new GenericOSGIServiceTracker<LoadFileService>( LoadFileService.class ).getService();
    }

    @Test
    public void cpuChannelCreated() throws IOException
    {
        pathToRaceFile = createRaceTestFile( VersionHandler.getVersionToken() );

        loadFileService.loadFile( pathToRaceFile );

        Assert.assertNotNull( "Channel cpu.system should be created", getCpuChannel() );
    }

    private String createRaceTestFile(int version)
    {

        TargetDataFileBuilder builder = new TargetDataFileBuilder( version );
        for (int i = 0; i <= 1000; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                     ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );
        }

        return builder.createFile( FILE_NAME ).getAbsolutePath();
    }

    private RuntimeEventChannel<?> getCpuChannel()
    {
        return runtimeEventAcceptor.getRuntimeEventChannel( FILE_NAME + ".cpu.system", Double.class );
    }

    @Test
    public void numberOfEventsCorrect() throws IOException
    {
        pathToRaceFile = createRaceTestFile( VersionHandler.getVersionToken() );

        loadFileService.loadFile( pathToRaceFile );

        List<RuntimeEvent<?>> runtimeEventsOfRuntimeEventChannel = runtimeEventAcceptor
                .getRuntimeEventsOfRuntimeEventChannel( getCpuChannel() );

        Assert.assertEquals( 1000, runtimeEventsOfRuntimeEventChannel.size() );

    }

    @Test
    public void loadFileWithWrongVersion() throws IOException
    {
        final int wrongVersion = 0;
        pathToRaceFile = createRaceTestFile( wrongVersion );
        loadFileService.loadFile( pathToRaceFile );

        Assert.assertEquals( 0, runtimeEventAcceptor.getAllRuntimeEvents().size() );
    }

    @After
    public void cleanUp()
    {
        CoreServiceHelper.getResetNotifier().performReset();

        File raceFile = new File( pathToRaceFile );
        if (raceFile.exists())
            raceFile.delete();
    }

}
