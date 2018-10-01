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

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.resourcetree.ModelNameNotifyUseCaseImpl;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.resources.model.TableModelImpl;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class ModelNameNotifyUseCaseImplTest extends UseCaseBaseTest
{
    private ModelNameNotifyUseCaseImpl sutModelNameNotifyUseCase;
    private ModelNameNotifyCallback mockedCallback;
    private TableModel tableModel;
    private ResourcesModelManager mockedResourcesManager;
    private TableModel tableModel2;

    @Before
    public void setup()
    {
        mockedCallback = Mockito.mock( ModelNameNotifyCallback.class );
        mockedResourcesManager = Mockito.mock( ResourcesModelManager.class );
        tableModel = new TableModelImpl( "Table1", null, Mockito.mock( ResourceChangedNotifier.class ) );
        tableModel2 = new TableModelImpl( "Table2", null, Mockito.mock( ResourceChangedNotifier.class ) );
        sutModelNameNotifyUseCase = new ModelNameNotifyUseCaseImpl( mockedCallback, mockedResourcesManager );

        List<ResourceModel> tableModel1inList = Arrays.asList( new ResourceModel[]{tableModel} );
        Mockito.when( mockedResourcesManager.getResources() ).thenReturn( tableModel1inList );
    }

    @Test
    public void testRegister() throws Exception
    {
        sutModelNameNotifyUseCase.register( tableModel );
        Mockito.verify( mockedResourcesManager ).registerResourceListener( sutModelNameNotifyUseCase );
    }

    @Test
    public void testInitialName() throws Exception
    {
        sutModelNameNotifyUseCase.register( tableModel );
        Mockito.verify( mockedCallback ).onNewResourceName( "Table1" );
    }

    @Test
    public void testNameUpdate() throws Exception
    {
        sutModelNameNotifyUseCase.register( tableModel );

        tableModel.setName( "Table2" );
        sutModelNameNotifyUseCase.onResourceRenamed( tableModel );
        Mockito.verify( mockedCallback ).onNewResourceName( "Table2" );
    }

    @Test
    public void testAnotherModelChanged() throws Exception
    {
        sutModelNameNotifyUseCase.register( tableModel );
        sutModelNameNotifyUseCase.onResourceModelChannelsChanged( tableModel2 );

        Mockito.verify( mockedCallback ).onNewResourceName( "Table1" );
        Mockito.verifyNoMoreInteractions( mockedCallback );
    }

    @Test
    public void testUnregister() throws Exception
    {
        sutModelNameNotifyUseCase.register( tableModel );
        sutModelNameNotifyUseCase.unregister();
        Mockito.verify( mockedResourcesManager ).unregisterResourceListener( sutModelNameNotifyUseCase );
    }

    @Test
    public void testModelAlreadyDeleted() throws Exception
    {
        sutModelNameNotifyUseCase.register( tableModel2 );
        Mockito.verify( mockedCallback ).onResourceDeleted();
    }
}
