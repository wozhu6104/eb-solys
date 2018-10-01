/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.resourcetree;

import java.util.List;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.file.FileModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResouceTreeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.resources.api.ResourceModelManagerConstants;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ResourceTreeNotifyUseCaseTest extends UseCaseBaseTest
{
    private final GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );

    @Test
    public void testInitialTreeData()
    {
        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );

        List<ResourcesFolder> expectedData = resourceManagerTracker.getService().getRootFolders();
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewResourceTreeData( expectedData );
    }

    @Test
    public void testSetNameUpdate()
    {
        TableModel createdTable = resourceManagerTracker.getService().createTable( "Test table" );

        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );

        List<ResourcesFolder> expectedData = resourceManagerTracker.getService().getRootFolders();
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewResourceTreeData( expectedData );

        createdTable.setName( "new name" );
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
    }

    @Test
    public void testNewResourceTreeUpdate()
    {
        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );

        TableModel createdTable = resourceManagerTracker.getService().createTable( "Test table" );
        List<ResourcesFolder> expectedData = resourceManagerTracker.getService().getRootFolders();
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).revealResource( createdTable );
    }

    @Test
    public void testCreateChart() throws Exception
    {
        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );

        ChartModel createdChart = resourceManagerTracker.getService().createChart( "chart1", ChartTypes.LINE_CHART );
        List<ResourceModel> charts = resourceManagerTracker.getService().getCharts();
        Assert.assertTrue( charts.contains( createdChart ) );

        List<ResourcesFolder> expectedData = resourceManagerTracker.getService().getRootFolders();
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).revealResource( createdChart );
    }

    @Test
    public void testCreateFileModel() throws Exception
    {
        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );

        FileModel createdFileModel = resourceManagerTracker.getService().createFileModel( "name", "path" );

        for (ResourcesFolder resourceFolder : resourceManagerTracker.getService().getRootFolders())
        {
            if (resourceFolder.getName().equals( ResourceModelManagerConstants.FILES_FOLDER_NAME ))
            {
                Assert.assertTrue( resourceFolder.getChildren().contains( createdFileModel ) );
            }
        }

        List<ResourcesFolder> expectedData = resourceManagerTracker.getService().getRootFolders();
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).revealResource( createdFileModel );
    }

    @Test
    public void testCreateSnapshot() throws Exception
    {
        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );

        SnapshotModel createdSnapshot = resourceManagerTracker.getService().createSnapshot( "map" );

        List<ResourceModel> snapshots = resourceManagerTracker.getService().getSnapshots();
        Assert.assertTrue( snapshots.contains( createdSnapshot ) );

        List<ResourcesFolder> expectedData = resourceManagerTracker.getService().getRootFolders();
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).revealResource( createdSnapshot );
    }

    @Test
    public void testUnregister()
    {
        ResourceTreeNotifyCallback mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
        ResouceTreeNotifyUseCase sut = UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( mockedCallback );
        sut.unregister();

        resourceManagerTracker.getService().createTable( "Test table" );

        Mockito.verify( mockedCallback, Mockito.times( 1 ) )
                .onNewResourceTreeData( Matchers.anyListOf( ResourcesFolder.class ) );
        Mockito.verifyNoMoreInteractions( mockedCallback );
    }

}
