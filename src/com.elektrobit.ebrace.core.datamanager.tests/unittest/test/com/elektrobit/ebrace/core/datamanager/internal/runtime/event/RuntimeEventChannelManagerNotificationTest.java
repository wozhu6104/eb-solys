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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.internal.channels.impl.RuntimeEventChannelManagerImpl;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventChannelManagerNotificationTest
{

    private RuntimeEventChannelManagerImpl runtimeEventChannelManagerImpl;
    private String channelName1;
    private String channelDescription1;

    private ListenerNotifier listenerNotifier;

    @Before
    public void setup()
    {
        listenerNotifier = Mockito.mock( ListenerNotifier.class );
        CommandLineParser commandLineParser = Mockito.mock( CommandLineParser.class );

        runtimeEventChannelManagerImpl = new RuntimeEventChannelManagerImpl( listenerNotifier );
        runtimeEventChannelManagerImpl.bindCommandLineParser( commandLineParser );

        channelName1 = "TestChannel1";
        channelDescription1 = "My TestChannel1";
    }

    @Test
    public void isListenerNotifiedAboutNewChannel() throws Exception
    {
        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        Mockito.verify( listenerNotifier ).notifyListeners();
    }

    @Test
    public void isListenerNotNotifiedAboutNewChannel() throws Exception
    {
        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );
        Mockito.verify( listenerNotifier, Mockito.times( 1 ) ).notifyListeners();
        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );
        Mockito.verifyNoMoreInteractions( listenerNotifier );
    }

    @Test
    public void isListenerNotifiedAboutRemovedChannel() throws Exception
    {
        RuntimeEventChannel<String> channel1 = runtimeEventChannelManagerImpl
                .createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        Mockito.verify( listenerNotifier, Mockito.times( 1 ) ).notifyListeners();

        runtimeEventChannelManagerImpl.removeRuntimeEventChannel( channel1 );

        Mockito.verify( listenerNotifier, Mockito.times( 2 ) ).notifyListeners();
    }

    @Test
    public void clearTest() throws Exception
    {
        runtimeEventChannelManagerImpl.clear();

        Mockito.verify( listenerNotifier, Mockito.times( 1 ) ).notifyListeners();

        runtimeEventChannelManagerImpl.createRuntimeEventChannel( channelName1, Unit.TEXT, channelDescription1 );

        Mockito.verify( listenerNotifier, Mockito.times( 2 ) ).notifyListeners();
    }

}
