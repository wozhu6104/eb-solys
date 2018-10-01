/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.usecase.reset.tests;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.profiling.PerformanceHelper;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultBuilder;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultFileWriter;
import com.elektrobit.ebrace.dev.test.util.memory.CyclicMemoryChecker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.api.LinuxAppStatsMessage;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusRequestResponseMessages;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusSignalMessage;
import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.ResourceMonitorMessage;
import test.com.elektrobit.ebrace.targetdata.dlt.DltMessages;

public class LoadAndResetMultipleFilesUseCaseTest extends UseCaseBaseTest implements OpenFileInteractionCallback
{
    private String fileToBeDeletedAfterTest = null;
    private String pathToFile;
    private ResetNotifier resetNotifier;
    private Runnable testCode;
    private OpenFileInteractionUseCase interActionUC;
    private KPIResultBuilder kpiResultBuilder;
    private PerformanceHelper performanceHelper;
    private CyclicMemoryChecker cyclicMemoryChecker;

    @Before
    public void setup()
    {
        resetNotifier = CoreServiceHelper.getResetNotifier();
        pathToFile = create35MBFileWithResourceDBusDLTData();
        interActionUC = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
        performanceHelper = new PerformanceHelper( 50 );
        cyclicMemoryChecker = new CyclicMemoryChecker( true );
    }

    @Test
    public void IsMemoryStableAfterLoad35MBFileAndDoAReset() throws Exception
    {
        testCode = new Runnable()
        {

            @Override
            public void run()
            {
                performanceHelper.start();
                interActionUC.openFile( pathToFile );
                performanceHelper.stop();
                resetNotifier.performReset();
            }
        };

        Assert.assertTrue( cyclicMemoryChecker.isHeapSizeStable( testCode ) );
    }

    private String create35MBFileWithResourceDBusDLTData()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        for (int i = 0; i < 10000; i++)
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
        interActionUC.unregister();
        if (fileToBeDeletedAfterTest != null)
        {
            new File( fileToBeDeletedAfterTest ).delete();
            fileToBeDeletedAfterTest = null;
        }

        writeKPIResults();
    }

    private void writeKPIResults()
    {
        kpiResultBuilder = new KPIResultBuilder();
        kpiResultBuilder.addDate( new Date() );
        kpiResultBuilder.addMetaData( "test_suite", "TS1" );
        kpiResultBuilder.addMetaData( "test_case", "LoadAndResetMultipleFilesUseCaseTest" );
        kpiResultBuilder.addMetaData( "test_person", "Automated" );
        kpiResultBuilder.addMeasuredItem( "load_file_min", "" + performanceHelper.min() / 1000, "s" );
        kpiResultBuilder.addMeasuredItem( "load_file_avg", "" + performanceHelper.avg() / 1000, "s" );
        kpiResultBuilder.addMeasuredItem( "load_file_max", "" + performanceHelper.max() / 1000, "s" );
        kpiResultBuilder.addMeasuredItem( "used_heap_after_clear_data_min",
                                          "" + String.format( Locale.US,
                                                              "%.2f",
                                                              cyclicMemoryChecker
                                                                      .getStatistics()
                                                                      .min()
                                                                      / 1000000 ),
                                          "MB" );
        kpiResultBuilder.addMeasuredItem( "used_heap_after_clear_data_avg",
                                          "" + String.format( Locale.US,
                                                              "%.2f",
                                                              cyclicMemoryChecker
                                                                      .getStatistics()
                                                                      .avg()
                                                                      / 1000000 ),
                                          "MB" );
        kpiResultBuilder.addMeasuredItem( "used_heap_after_clear_data_max",
                                          "" + String.format( Locale.US,
                                                              "%.2f",
                                                              cyclicMemoryChecker
                                                                      .getStatistics()
                                                                      .max()
                                                                      / 1000000 ),
                                          "MB" );
        KPIResultFileWriter.writeToFile( "kpi-results/use-case-tests/LoadAndResetMultipleFilesUseCaseTest.json",
                                         kpiResultBuilder.build() );
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
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
