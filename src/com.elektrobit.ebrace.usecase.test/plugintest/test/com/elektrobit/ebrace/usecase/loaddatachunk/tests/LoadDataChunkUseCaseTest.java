/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.loaddatachunk.tests;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.api.LinuxAppStatsMessage;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusRequestResponseMessages;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusSignalMessage;
import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.ResourceMonitorMessage;
import test.com.elektrobit.ebrace.targetdata.dlt.DltMessages;

public class LoadDataChunkUseCaseTest extends UseCaseBaseTest
        implements
            // LoadDataChunkInteractionCallback,
            OpenFileInteractionCallback,
            LoadFileProgressNotifyCallback
{

    private String dummyFilePath;
    private long fileStartTime;
    private long fileEndTime;
    private long chunkStartTime;
    private long chunkEndTime;

    @Before
    public void setup()
    {
        dummyFilePath = createDummyFile();
    }

    @Test
    public void isDataChunkLoading() throws Exception
    {
        UseCaseFactoryInstance.get().makeLoadFileProgressNotifyUseCase( this, dummyFilePath );

        OpenFileInteractionUseCase loadFileInteractionUseCase = UseCaseFactoryInstance.get()
                .makeLoadFileInteractionUseCase( this );
        loadFileInteractionUseCase.openFile( dummyFilePath );

        Assert.assertEquals( 1000000, fileStartTime );
        Assert.assertEquals( 1000000, chunkStartTime );

        Assert.assertEquals( 100999000, chunkEndTime );
        Assert.assertEquals( 100999000, fileEndTime );

    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
    }

    private String createDummyFile()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        for (int i = 0; i < 100000; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN,
                                     DltMessages.getDltDummyMessage().toByteArray() );
            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                     ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );
            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_DBUS,
                                     DBusSignalMessage.dbusSignalDummy().toByteArray() );
            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_DBUS,
                                     DBusRequestResponseMessages.dbusRequestDummy().toByteArray() );
            builder.addProtoMessage( timestamp + 1,
                                     MessageType.MSG_TYPE_DBUS,
                                     DBusRequestResponseMessages.dbusResponseDummy().toByteArray() );
            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_LINUX_APP_STATS_PLUGIN,
                                     LinuxAppStatsMessage.getProcessDummyMessage().toByteArray() );
            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_LINUX_APP_STATS_PLUGIN,
                                     LinuxAppStatsMessage.getThreadDummyMessage().toByteArray() );
        }

        File file = builder.createFile( fileName );
        return file.getAbsolutePath();
    }

    @After
    public void cleanup()
    {
        File testFile = new File( dummyFilePath );
        if (testFile.exists())
        {
            testFile.delete();
        }

    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

    @Override
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        this.fileStartTime = fileStartTime;
        this.fileEndTime = fileEndTime;
        this.chunkStartTime = chunkStartTime;
        this.chunkEndTime = chunkEndTime;
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
    {
    }

    @Override
    public void onFileLoadedSucessfully()
    {
    }

    @Override
    public void onFileLoadingFailed()
    {
    }

    @Override
    public void onFileAlreadyLoaded(String pathToFile)
    {
    }

    @Override
    public void onFileEmpty(String pathToFile)
    {
    }

    @Override
    public void onFileNotFound(String pathToFile)
    {
    }

}
