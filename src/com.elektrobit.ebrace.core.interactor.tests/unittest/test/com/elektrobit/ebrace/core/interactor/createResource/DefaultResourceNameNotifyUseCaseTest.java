/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.createResource;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.createresource.DefaultResourceNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.createResource.DefaultResourceNameNotifyUseCaseImpl;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class DefaultResourceNameNotifyUseCaseTest
{
    private ResourcesModelManager mockedResourcesModelManager;
    private DefaultResourceNameNotifyUseCase sut;

    @Before
    public void setUp()
    {
        mockedResourcesModelManager = Mockito.mock( ResourcesModelManager.class );
        sut = new DefaultResourceNameNotifyUseCaseImpl( mockedResourcesModelManager );
    }

    @Test
    public void testGetNextPossibleConnectionNameNoConnections() throws Exception
    {
        String result = sut.getNextPossibleConnectionName();
        Assert.assertEquals( "Target 1", result );
    }

    @Test
    public void testGetNextPossibleConnectionName1Present() throws Exception
    {
        ResourceModel mockedConnection = mockConnectionModel( "Target 1" );
        Mockito.when( mockedResourcesModelManager.getConnections() ).thenReturn( Arrays.asList( mockedConnection ) );
        String result = sut.getNextPossibleConnectionName();
        Assert.assertEquals( "Target 2", result );
    }

    private ConnectionModel mockConnectionModel(String name)
    {
        ConnectionModel mockedConnection = Mockito.mock( ConnectionModel.class );
        Mockito.when( mockedConnection.getName() ).thenReturn( name );
        return mockedConnection;
    }
}
