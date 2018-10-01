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
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.model.TableModelImpl;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusRequestResponseMessages;
import test.com.elektrobit.ebrace.genivi.targetadapter.dbus.test.DBusSignalMessage;
import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.ResourceMonitorMessage;
import test.com.elektrobit.ebrace.targetdata.dlt.DltMessages;

public class ClearAllDataUseCaseTest extends UseCaseBaseTest
        implements
            ClearAllDataInteractionCallback,
            AllChannelsNotifyCallback,
            StructureNotifyCallback,
            RuntimeEventTableDataNotifyCallback,
            ResourceChangedNotifier
{
    private List<RuntimeEventChannel<?>> allChannels;
    private boolean resetWasDone = false;
    private ClearAllDataInteractionUseCase resetInteractionUseCase;
    private List<Tree> allTrees;
    private List<?> tableEvents;

    @Before
    public void setup()
    {
        OpenFileInteractionCallback loadFileInteractionCallback = Mockito.mock( OpenFileInteractionCallback.class );

        OpenFileInteractionUseCase loadFileInteractionUseCase = UseCaseFactoryInstance.get()
                .makeLoadFileInteractionUseCase( loadFileInteractionCallback );
        loadFileInteractionUseCase.openFile( createRaceFileWithResourceDBusDLTData() );

        resetInteractionUseCase = UseCaseFactoryInstance.get().makeClearAllDataInteractionUseCase( this );
    }

    @Test
    public void checkIfResetWasPerformed()
    {
        resetInteractionUseCase.reset();

        Assert.assertTrue( "Expecting reset was done after a reset call.", resetWasDone );
    }

    @Test
    public void checkIfChannelsWereDeleted()
    {
        UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );

        ClearAllDataInteractionUseCase resetInteractionUseCase = UseCaseFactoryInstance.get()
                .makeClearAllDataInteractionUseCase( this );
        resetInteractionUseCase.reset();

        Assert.assertTrue( "Expecting empty channels list after a reset.", allChannels.isEmpty() );
    }

    @Test
    public void checkIfStructureWereDeleted()
    {

        UseCaseFactoryInstance.get().makeStructureNotifyUseCase( this );
        ClearAllDataInteractionUseCase resetInteractionUseCase = UseCaseFactoryInstance.get()
                .makeClearAllDataInteractionUseCase( this );
        resetInteractionUseCase.reset();

        Assert.assertTrue( "Expecting empty tree list after a reset.", allTrees.isEmpty() );
    }

    @Test
    public void checkIfEventsWereDeleted() throws InterruptedException
    {
        // TODO: We need a use-case for that
        CoreServiceHelper.getUserInteractionPerferences().setIsLiveMode( true );

        UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );

        TableModel tableModel = new TableModelImpl( "MyTable", null, this );
        tableModel.setChannels( this.allChannels );

        UseCaseFactoryInstance.get()
                .makeRuntimeEventTableDataNotifyUseCase( this, new ArrayList<RowFormatter>(), tableModel );

        ClearAllDataInteractionUseCase resetInteractionUseCase = UseCaseFactoryInstance.get()
                .makeClearAllDataInteractionUseCase( this );
        resetInteractionUseCase.reset();

        // Wait for update
        executePlannedRepeatedTasks();

        Assert.assertTrue( "Expecting empty event list after a reset.", tableEvents.isEmpty() );
    }

    private String createRaceFileWithResourceDBusDLTData()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        for (int i = 0; i < 100; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN,
                                     DltMessages.getDltDummyMessage().toByteArray() );
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
                                     MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                     ResourceMonitorMessage.resourceMonitorDummyMessage().toByteArray() );
        }

        File file = builder.createFile( fileName );
        return file.getAbsolutePath();
    }

    @Override
    public void onResetDone()
    {
        resetWasDone = true;
    }

    @Override
    public void onAllChannelsChanged(List<RuntimeEventChannel<?>> allChannels)
    {
        this.allChannels = allChannels;
    }

    @Override
    public void onStructureChanged(List<Tree> allTrees, TreeNodesCheckState nodesCheckState)
    {
        this.allTrees = allTrees;
    }

    @Override
    public void notifyResourceChannelsChanged(ResourceModel resoruceModel)
    {
        // not needed in this test
    }

    @Override
    public void onTableInputCollected(TableData filterResultData, boolean tableEnd)
    {
        this.tableEvents = filterResultData.getItemsToBeDisplayed();
    }

    @Override
    public void onFilteringStarted()
    {
        // not needed in this test
    }

    @Override
    public void notifyResourceRenamed(ResourceModel resourceModel)
    {
        // not needed in this test
    }

    @Override
    public void onTimeMarkerRenamed(TimeMarker timeMarker)
    {
        // not needed in this test
    }

    @Override
    public void onJumpToTimeMarker(TimeMarker timeMarker)
    {
        // not needed in this test
    }

    @Override
    public void notifyResourceStateChanged(ResourceModel resourceModel)
    {
        // not needed in this test
    }

    @Override
    public void notifySelectedChannelsChanged(ResourceModel resourceModel)
    {
        // not needed in this test
    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        // TODO Auto-generated method stub

    }

}
