/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.resources.api.model;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.model.TableModelImpl;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class BaseResourceModelTest
{
    private ResourceModel sutResourceModel;
    private RuntimeEventChannel<?> channel1;
    private RuntimeEventChannel<?> channel2;
    private RuntimeEventChannel<?> channel3;
    private ResourceChangedNotifier mockedNotifier;

    @Before
    public void setup()
    {

        ResourcesFolder mockedParentFolder = Mockito.mock( ResourcesFolder.class );
        mockedNotifier = Mockito.mock( ResourceChangedNotifier.class );
        sutResourceModel = new TableModelImpl( "table name", mockedParentFolder, mockedNotifier );

        channel1 = mockRuntimeEventChannel( "channel1", Unit.KILOBYTE );
        channel2 = mockRuntimeEventChannel( "channel2", Unit.KILOBYTE );
        channel3 = mockRuntimeEventChannel( "channel3", Unit.KILOBYTE );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private RuntimeEventChannel<?> mockRuntimeEventChannel(String name, Unit unit)
    {
        RuntimeEventChannel<?> mockedChannel = Mockito.mock( RuntimeEventChannel.class );
        Mockito.when( mockedChannel.getName() ).thenReturn( name );
        Mockito.when( mockedChannel.getUnit() ).thenReturn( unit );
        return mockedChannel;
    }

    @Test
    public void testSetChannelsNotify() throws Exception
    {
        sutResourceModel.setChannels( Arrays.asList( channel1 ) );
        Mockito.verify( mockedNotifier ).notifyResourceChannelsChanged( sutResourceModel );
        Mockito.verifyNoMoreInteractions( mockedNotifier );
        Assert.assertEquals( Arrays.asList( channel1 ), sutResourceModel.getChannels() );
    }

    @Test
    public void testSetDisabledChannelsNotify() throws Exception
    {
        sutResourceModel.setChannels( Arrays.asList( channel1, channel2 ) );
        sutResourceModel.setDisabledChannels( Arrays.asList( channel1 ) );
        Mockito.verify( mockedNotifier, Mockito.times( 2 ) ).notifyResourceChannelsChanged( sutResourceModel );
        Mockito.verifyNoMoreInteractions( mockedNotifier );
        Assert.assertEquals( Arrays.asList( channel2 ), sutResourceModel.getEnabledChannels() );
    }

    @Test
    public void testDisabledChannelsDeleted() throws Exception
    {
        sutResourceModel.setChannels( Arrays.asList( channel1, channel2, channel3 ) );
        sutResourceModel.setDisabledChannels( Arrays.asList( channel1, channel2 ) );
        sutResourceModel.setChannels( Arrays.asList( channel1, channel3 ) );

        Assert.assertEquals( Arrays.asList( channel1 ), sutResourceModel.getDisabledChannels() );
        Assert.assertEquals( Arrays.asList( channel3 ), sutResourceModel.getEnabledChannels() );
    }

    @Test
    public void testSelectedChannelsDeleted() throws Exception
    {
        sutResourceModel.setChannels( Arrays.asList( channel1, channel2 ) );
        sutResourceModel.setSelectedChannels( Arrays.asList( channel1, channel2 ) );
        sutResourceModel.setChannels( Arrays.asList( channel1 ) );

        Mockito.verify( mockedNotifier, Mockito.times( 2 ) ).notifyResourceChannelsChanged( sutResourceModel );
        Mockito.verify( mockedNotifier, Mockito.times( 1 ) ).notifySelectedChannelsChanged( sutResourceModel );

        Assert.assertEquals( Arrays.asList( channel1 ), sutResourceModel.getSelectedChannels() );
    }

    @Test
    public void testSameSelectedChannelsListenerNotNotified() throws Exception
    {
        sutResourceModel.setChannels( Arrays.asList( channel1, channel2 ) );
        sutResourceModel.setSelectedChannels( Arrays.asList( channel1, channel2 ) );
        sutResourceModel.setSelectedChannels( Arrays.asList( channel1, channel2 ) );

        Mockito.verify( mockedNotifier, Mockito.times( 1 ) ).notifySelectedChannelsChanged( sutResourceModel );
    }
}
