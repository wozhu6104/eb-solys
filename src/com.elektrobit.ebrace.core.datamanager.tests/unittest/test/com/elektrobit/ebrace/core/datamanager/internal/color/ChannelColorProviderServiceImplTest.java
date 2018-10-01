/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.color;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.internal.channels.impl.RuntimeEventChannelManagerImpl;
import com.elektrobit.ebrace.core.datamanager.internal.color.ChannelColorProviderServiceImpl;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class ChannelColorProviderServiceImplTest
{
    private ChannelColorProviderService channelColorProviderService;

    @Before
    public void setUp()
    {
        channelColorProviderService = new ChannelColorProviderServiceImpl();

        PreferencesService preferencesService = mock( PreferencesService.class );
        ((ChannelColorProviderServiceImpl)channelColorProviderService).bind( preferencesService );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callGetColorForChannelWithNull()
    {
        channelColorProviderService.getColorForChannel( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callHasColorWithNull()
    {
        channelColorProviderService.hasColor( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callSetColorForChannelWithNull()
    {
        channelColorProviderService.setColorForChannel( null, 0, 0, 0 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callCreateAndGetColorForChannelWithNull()
    {
        channelColorProviderService.createAndGetColorForChannel( null );
    }

    @Test
    public void testSetAndGetColorForChannel()
    {
        RuntimeEventChannel<String> channel = createChannel();
        channelColorProviderService.setColorForChannel( channel, 255, 0, 0 );

        assertEquals( new SColor( 255, 0, 0 ), channelColorProviderService.getColorForChannel( channel ) );
    }

    @Test
    public void testHasColorFalse()
    {
        RuntimeEventChannel<String> channel = createChannel();

        assertEquals( false, channelColorProviderService.hasColor( channel ) );
    }

    @Test
    public void testHasColorTrue()
    {
        RuntimeEventChannel<String> channel = createChannel();

        channelColorProviderService.setColorForChannel( channel, 255, 0, 255 );

        assertEquals( true, channelColorProviderService.hasColor( channel ) );
    }

    private RuntimeEventChannel<String> createChannel()
    {
        CommandLineParser commandLineParser = Mockito.mock( CommandLineParser.class );
        RuntimeEventChannelManagerImpl runtimeEventChannelManagerImpl = new RuntimeEventChannelManagerImpl( mock( ListenerNotifier.class ) );
        runtimeEventChannelManagerImpl.bindCommandLineParser( commandLineParser );

        String channelName1 = "TestChannel1";
        String channelDescription1 = "My TestChannel1";

        return runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );
    }
}
