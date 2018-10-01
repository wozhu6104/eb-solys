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
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService.MetaDataKeys;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;
import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.ResourceMonitorMessage;

public class SystemCPUValuesNotifyUseCaseTest extends UseCaseBaseTest implements SystemCPUValuesNotifyCallback
{

    private String protoInputFile;
    private OpenFileInteractionUseCase loadFileInteractionUseCase;
    private SystemCPUValuesNotifyUseCase systemCPUValuesNotifyUseCase;
    private List<RTargetHeaderCPUValue> systemCPUValues;

    @Test
    public void systemValuesCorrect() throws Exception
    {
        protoInputFile = createProtoFile();

        loadFileInteractionUseCase = UseCaseFactoryInstance.get()
                .makeLoadFileInteractionUseCase( Mockito.mock( OpenFileInteractionCallback.class ) );
        systemCPUValuesNotifyUseCase = UseCaseFactoryInstance.get().makeSystemCPUValuesNotifyUseCase( this );
        loadFileInteractionUseCase.openFile( protoInputFile );

        Assert.assertEquals( Arrays.asList( new RTargetHeaderCPUValue( 2000000, 1.0 ),
                                            new RTargetHeaderCPUValue( 2500000, 4.0 ) ),
                             systemCPUValues );

    }

    private String createProtoFile()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        builder.addProtoMessage( 1000,
                                 MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                 MetaDataKeys.SYSTEM_CPU_VALUES.toString(),
                                 "100",
                                 ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );

        builder.addProtoMessage( 2000,
                                 MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                 MetaDataKeys.SYSTEM_CPU_VALUES.toString(),
                                 "110",
                                 ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );

        builder.addProtoMessage( 2500,
                                 MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                 MetaDataKeys.SYSTEM_CPU_VALUES.toString(),
                                 "130",
                                 ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );

        File file = builder.createFile( fileName );
        return file.getAbsolutePath();
    }

    @After
    public void cleanup()
    {
        loadFileInteractionUseCase.unregister();
        systemCPUValuesNotifyUseCase.unregister();

        File testFile = new File( protoInputFile );
        if (testFile.exists())
        {
            testFile.delete();
        }

    }

    @Override
    public void onSystemCPUValuesUpdated(List<RTargetHeaderCPUValue> systemCPUValues)
    {
        this.systemCPUValues = systemCPUValues;
    }

}
