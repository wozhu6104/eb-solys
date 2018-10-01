/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.internal.channels.impl.RuntimeEventChannelManagerImpl;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventChannelManagerTest
{
    private RuntimeEventChannelManagerImpl runtimeEventChannelManagerImpl;
    private String channelName1;
    private String channelDescription1;

    private String channelName2;
    private String channelDescription2;

    @Before
    public void setup()
    {
        ListenerNotifier listenerNotifier = Mockito.mock( ListenerNotifier.class );
        CommandLineParser commandLineParser = Mockito.mock( CommandLineParser.class );
        runtimeEventChannelManagerImpl = new RuntimeEventChannelManagerImpl( listenerNotifier );
        runtimeEventChannelManagerImpl.bindCommandLineParser( commandLineParser );

        channelName1 = "TestChannel1";
        channelDescription1 = "My TestChannel1";

        channelName2 = "TestChannel2";
        channelDescription2 = "My TestChannel2";
    }

    @Test
    public void createRuntimeEventChannelsTest() throws Exception
    {

        RuntimeEventChannel<String> channel1 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        RuntimeEventChannel<Long> channel2 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName2, Unit.KILOBYTE, channelDescription2 );

        assertEqualsRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1, channel1 );
        assertEqualsRuntimeEventChannel( channelName2, Unit.KILOBYTE, channelDescription2, channel2 );
    }

    @Test
    public void createOrGetRuntimeEventChannelsTest() throws Exception
    {
        RuntimeEventChannel<String> channel1 = runtimeEventChannelManagerImpl
                .createOrGetRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        RuntimeEventChannel<String> channel2 = runtimeEventChannelManagerImpl
                .createOrGetRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        Assert.assertNotNull( channel1 );
        Assert.assertNotNull( channel2 );

        Assert.assertEquals( channel1, channel2 );
    }

    @Test
    public void createSameRuntimeEventChannelTwiceTest() throws Exception
    {
        RuntimeEventChannel<String> channel1 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        RuntimeEventChannel<String> channel2 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        assertEqualsRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1, channel1 );
        Assert.assertNull( "Creating a runtime event channel twice should return null.", channel2 );
    }

    @Test
    public void getRuntimeEventChannelsTest() throws Exception
    {

        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.COUNT, channelDescription1 );

        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName2, Unit.COUNT, channelDescription2 );

        Assert.assertEquals( 2, runtimeEventChannelManagerImpl.getRuntimeEventChannels().size() );
    }

    @Test
    public void getRuntimeEventChannelWithNameTest() throws Exception
    {
        RuntimeEventChannel<String> channel1 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        RuntimeEventChannel<Long> channel2 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName2, Unit.KILOBYTE, channelDescription2 );

        Assert.assertEquals( channel1, runtimeEventChannelManagerImpl.getRuntimeEventChannelWithName( channelName1 ) );
        Assert.assertEquals( channel2, runtimeEventChannelManagerImpl.getRuntimeEventChannelWithName( channelName2 ) );
    }

    @Test
    public void testGetRuntimeEventChannelsForUnit()
    {
        RuntimeEventChannel<Long> expectedChannel1 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.KILOBYTE, channelDescription1 );
        runtimeEventChannelManagerImpl.createRuntimeEventChannel( "not use channel", Unit.TEXT, channelDescription1 );
        RuntimeEventChannel<Long> expectedChannel2 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName2, Unit.KILOBYTE, channelDescription1 );

        List<RuntimeEventChannel<?>> foundChannels = runtimeEventChannelManagerImpl
                .getRuntimeEventChannelsForUnit( Unit.KILOBYTE );

        Assert.assertEquals( 2, foundChannels.size() );
        Assert.assertTrue( foundChannels.contains( expectedChannel1 ) );
        Assert.assertTrue( foundChannels.contains( expectedChannel2 ) );
    }

    @Test
    public void clearTest()
    {

        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.KILOBYTE, channelDescription1 );

        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName2, Unit.TEXT, channelDescription2 );

        runtimeEventChannelManagerImpl.clear();

        Assert.assertEquals( 0, runtimeEventChannelManagerImpl.getRuntimeEventChannels().size() );
    }

    @Test
    public void renameChannelTest() throws Exception
    {
        RuntimeEventChannel<String> channel1 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        String newName = "newName";
        runtimeEventChannelManagerImpl.renameRuntimeEventChannel( channel1, newName );

        RuntimeEventChannel<?> foundChannel = runtimeEventChannelManagerImpl.getRuntimeEventChannelWithName( newName );

        Assert.assertEquals( newName, channel1.getName() );
        Assert.assertEquals( channel1, foundChannel );
    }

    @Test
    public void testChannelPrefixDefaultOn() throws Exception
    {
        DataSourceContext dataContext = new DataSourceContext( SOURCE_TYPE.CONNECTION, "test.source." );
        RuntimeEventChannel<String> channel = runtimeEventChannelManagerImpl
                .createOrGetRuntimeEventChannel( dataContext, "channelName", Unit.TEXT, "" );

        Assert.assertEquals( "test.source.channelName", channel.getName() );
    }

    @Test
    public void testChannelPrefixOn() throws Exception
    {
        ListenerNotifier listenerNotifier = Mockito.mock( ListenerNotifier.class );
        CommandLineParser commandLineParser = Mockito.mock( CommandLineParser.class );
        Mockito.when( commandLineParser.hasArg( RuntimeEventChannelManagerImpl.SOURCE_PREFIX_ACTIVE_FLAG ) )
                .thenReturn( true );
        runtimeEventChannelManagerImpl = new RuntimeEventChannelManagerImpl( listenerNotifier );
        runtimeEventChannelManagerImpl.bindCommandLineParser( commandLineParser );

        DataSourceContext dataContext = new DataSourceContext( SOURCE_TYPE.CONNECTION, "test.source." );
        RuntimeEventChannel<String> channel = runtimeEventChannelManagerImpl
                .createOrGetRuntimeEventChannel( dataContext, "channelName", Unit.TEXT, "" );

        Assert.assertEquals( "test.source.channelName", channel.getName() );
    }

    private <T> void assertEqualsRuntimeEventChannel(String name, Unit<T> unit, String description,
            RuntimeEventChannel<T> channel)
    {
        Assert.assertEquals( name, channel.getName() );
        Assert.assertEquals( unit, channel.getUnit() );
        Assert.assertEquals( description, channel.getDescription() );
    }

}
