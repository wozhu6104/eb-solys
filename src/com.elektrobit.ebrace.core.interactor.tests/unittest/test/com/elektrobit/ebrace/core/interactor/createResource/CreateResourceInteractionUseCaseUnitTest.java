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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.createResource.CreateResourceInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class CreateResourceInteractionUseCaseUnitTest extends UseCaseBaseTest
{
    private ResourcesModelManager mockedResourcesModelManager;
    private CreateResourceInteractionCallback mockedCreateResourceCallback;
    private CreateResourceInteractionUseCaseImpl createResourceInteractionUseCaseImpl;
    private UserMessageLogger mockedUserMessageLogger;

    @Before
    public void setProVersion()
    {
        ProVersion.getInstance().setActive( true );
    }

    @Before
    public void setUp()
    {
        mockedCreateResourceCallback = mock( CreateResourceInteractionCallback.class );
        mockedResourcesModelManager = mock( ResourcesModelManager.class );
        mockedUserMessageLogger = mock( UserMessageLogger.class );
        createResourceInteractionUseCaseImpl = new CreateResourceInteractionUseCaseImpl( mockedCreateResourceCallback,
                                                                                         mockedResourcesModelManager,
                                                                                         mockedUserMessageLogger );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateChart()
    {
        List<RuntimeEventChannel<?>> stringChannels = new ArrayList<RuntimeEventChannel<?>>();
        RuntimeEventChannel<String> channel = mock( RuntimeEventChannel.class );
        when( channel.getUnit() ).thenReturn( Unit.TEXT );
        stringChannels.add( channel );
        createResourceInteractionUseCaseImpl.createAndOpenChart( stringChannels );
        verify( mockedCreateResourceCallback, times( 1 ) ).onChartChannelsTypeMismatch();
    }

    @Test
    public void testCreateChartFromResource()
    {
        TableModel mockedTableModel = mock( TableModel.class );
        List<RuntimeEventChannel<?>> stringChannels = new ArrayList<RuntimeEventChannel<?>>();
        RuntimeEventChannel<String> channel = mockChannelWithUnit( Unit.TEXT );
        stringChannels.add( channel );
        mockedTableModel.setChannels( stringChannels );
        createResourceInteractionUseCaseImpl.createAndOpenChartFromResource( mockedTableModel );

        verify( mockedCreateResourceCallback, times( 1 ) ).onChartChannelsTypeMismatch();
    }

    private <T> RuntimeEventChannel<T> mockChannelWithUnit(Unit<T> unit)
    {
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<T> channel = mock( RuntimeEventChannel.class );
        when( channel.getUnit() ).thenReturn( unit );
        return channel;
    }

    @Test
    public void testCreateSnapshotWithoutPRO()
    {
        ProVersion.getInstance().setActive( false );
        SnapshotModel result = createResourceInteractionUseCaseImpl
                .createAndOpenSnapshot( Collections.<RuntimeEventChannel<?>> emptyList() );

        Assert.assertNull( result );
        verify( mockedCreateResourceCallback ).onProVersionNotAvailable();
    }

    @Test
    public void testCreateSnapshotFromResourceWithoutPRO()
    {
        ProVersion.getInstance().setActive( false );
        createResourceInteractionUseCaseImpl.createAndOpenSnapshotFromResource( null );

        verify( mockedCreateResourceCallback ).onProVersionNotAvailable();
    }

    @Test
    public void testCreateOrGetSnapshotWithoutPRO()
    {
        ProVersion.getInstance().setActive( false );
        SnapshotModel result = createResourceInteractionUseCaseImpl
                .createOrGetAndOpenSnapshot( Collections.<RuntimeEventChannel<?>> emptyList() );

        Assert.assertNull( result );
        verify( mockedCreateResourceCallback ).onProVersionNotAvailable();
    }
}
