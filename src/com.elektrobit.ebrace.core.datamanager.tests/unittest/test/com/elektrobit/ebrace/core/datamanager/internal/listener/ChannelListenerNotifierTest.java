/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datamanager.internal.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.internal.listener.ChannelListenerNotifierImpl;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelListenerNotifierTest
{
    private static int DEFAULT_UPDATE_INTERVAL = 0;

    private ChannelListenerNotifier channelListenerNotifier;

    @Before
    public void setUp()
    {
        channelListenerNotifier = new ChannelListenerNotifierImpl( DEFAULT_UPDATE_INTERVAL );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callRegiterWithNullsThrowsException()
    {
        channelListenerNotifier.registerListener( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callUnregiterWithNullsThrowsException()
    {
        channelListenerNotifier.unregisterListener( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void callNotifyChannelChangedWithNullsThrowsException()
    {
        channelListenerNotifier.notifyChannelChanged( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNonPositiveUpdateInterval()
    {
        int updateInterval = -1;
        RuntimeEventChannel<?> channel = mock( RuntimeEventChannel.class );

        channelListenerNotifier = new ChannelListenerNotifierImpl( updateInterval );
        channelListenerNotifier.notifyChannelChanged( channel );
    }

    @Test
    public void registerWithEmptyChannelListThrowsNoException()
    {
        ChannelsContentChangedListener listener = mock( ChannelsContentChangedListener.class );
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();

        channelListenerNotifier.registerListener( listener, channels );
    }

    @Test
    public void verifyListenerIsCalledExactlyOnceOnNotify()
    {
        RuntimeEventChannel<?> channel = mock( RuntimeEventChannel.class );
        ChannelsContentChangedListener listener = mock( ChannelsContentChangedListener.class );
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( channel );

        channelListenerNotifier.registerListener( listener, channels );
        channelListenerNotifier.notifyChannelChanged( channel );
        verify( listener, times( 1 ) ).onChannelsContentChanged();
    }

    @Test
    public void verifyListenerIsNotCalledAfterUnregister()
    {
        RuntimeEventChannel<?> channel = mock( RuntimeEventChannel.class );
        ChannelsContentChangedListener listener = mock( ChannelsContentChangedListener.class );
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( channel );

        channelListenerNotifier.registerListener( listener, channels );
        channelListenerNotifier.unregisterListener( listener );
        channelListenerNotifier.notifyChannelChanged( channel );
        verify( listener, times( 0 ) ).onChannelsContentChanged();
    }

    @Test
    public void verifyListenerIsNotCalledForEventOnOtherChannel()
    {
        RuntimeEventChannel<?> channel1 = mock( RuntimeEventChannel.class );
        RuntimeEventChannel<?> channel2 = mock( RuntimeEventChannel.class );
        ChannelsContentChangedListener listener = mock( ChannelsContentChangedListener.class );
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( channel1 );

        channelListenerNotifier.registerListener( listener, channels );
        channelListenerNotifier.notifyChannelChanged( channel2 );
        verify( listener, times( 0 ) ).onChannelsContentChanged();
    }

    @Test
    public void verifyMultipleListenerAreCalled()
    {
        RuntimeEventChannel<?> channel = mock( RuntimeEventChannel.class );
        ChannelsContentChangedListener listener1 = mock( ChannelsContentChangedListener.class );
        ChannelsContentChangedListener listener2 = mock( ChannelsContentChangedListener.class );
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( channel );

        channelListenerNotifier.registerListener( listener1, channels );
        channelListenerNotifier.registerListener( listener2, channels );
        channelListenerNotifier.notifyChannelChanged( channel );
        verify( listener1, times( 1 ) ).onChannelsContentChanged();
        verify( listener2, times( 1 ) ).onChannelsContentChanged();
    }

}
