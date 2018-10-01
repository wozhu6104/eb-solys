/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.resourcetree;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.resourcetree.ResourceTreeNotifyUseCaseImpl;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ResourceTreeNotifyUseCaseTest extends UseCaseBaseTest
{
    ResourcesModelManager mockedResourcesManager;
    List<ResourcesFolder> expectedData;
    ResourceTreeNotifyCallback mockedCallback;

    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        mockedResourcesManager = Mockito.mock( ResourcesModelManager.class );
        expectedData = Mockito.mock( List.class );
        Mockito.when( mockedResourcesManager.getRootFolders() ).thenReturn( expectedData );

        mockedCallback = Mockito.mock( ResourceTreeNotifyCallback.class );
    }

    @Test
    public void testInitialData()
    {
        ResourceTreeNotifyUseCaseImpl sut = new ResourceTreeNotifyUseCaseImpl( mockedCallback, mockedResourcesManager );

        Mockito.verify( mockedResourcesManager ).registerTreeListener( sut );
        Mockito.verify( mockedCallback ).onNewResourceTreeData( expectedData );
    }

    @Test
    public void testTreeChanged() throws Exception
    {
        ResourceTreeNotifyUseCaseImpl sut = new ResourceTreeNotifyUseCaseImpl( mockedCallback, mockedResourcesManager );

        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewResourceTreeData( expectedData );
        sut.onResourceTreeChanged();
        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
    }

    @Test
    public void testNewResourceCreated() throws Exception
    {
        ResourceTreeNotifyUseCaseImpl sut = new ResourceTreeNotifyUseCaseImpl( mockedCallback, mockedResourcesManager );

        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewResourceTreeData( expectedData );
        TableModel mockedTableModel = Mockito.mock( TableModel.class );
        sut.onResourceAdded( mockedTableModel );

        Mockito.verify( mockedCallback, Mockito.times( 2 ) ).onNewResourceTreeData( expectedData );
        Mockito.verify( mockedCallback ).revealResource( mockedTableModel );

    }

    @Test
    public void testUnregister() throws Exception
    {
        ResourceTreeNotifyUseCaseImpl sut = new ResourceTreeNotifyUseCaseImpl( mockedCallback, mockedResourcesManager );
        sut.unregister();
        sut.onResourceTreeChanged();
        sut.onResourceAdded( null );

        Mockito.verify( mockedResourcesManager ).unregisterTreeListener( sut );
        Mockito.verify( mockedCallback, Mockito.times( 1 ) ).onNewResourceTreeData( expectedData );
    }
}
