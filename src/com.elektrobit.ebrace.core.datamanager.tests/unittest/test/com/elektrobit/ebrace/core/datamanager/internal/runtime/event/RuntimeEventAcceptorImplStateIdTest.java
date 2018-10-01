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
import com.elektrobit.ebrace.core.datamanager.internal.listener.ChannelListenerNotifierImpl;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventAcceptorImpl;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.api.RuntimeEventNotifier;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import junit.framework.Assert;

public class RuntimeEventAcceptorImplStateIdTest
{
    private RuntimeEventChannel<String> channel;
    private RuntimeEventAcceptorImpl runtimeEventAcceptorImpl;
    private int stateIdBefore;

    @Before
    public void setup()
    {
        RuntimeEventNotifier runtimeEventNotifier = Mockito.mock( RuntimeEventNotifier.class );
        ListenerNotifier runtimeEventChannelLifecycleChangedNotifier = Mockito.mock( ListenerNotifier.class );
        CommandLineParser commandLineParser = Mockito.mock( CommandLineParser.class );

        RuntimeEventChannelManagerImpl runtimeEventChannelManager = new RuntimeEventChannelManagerImpl( runtimeEventChannelLifecycleChangedNotifier );
        runtimeEventChannelManager.bindCommandLineParser( commandLineParser );
        runtimeEventAcceptorImpl = new RuntimeEventAcceptorImpl( runtimeEventChannelManager,
                                                                 null,
                                                                 runtimeEventNotifier,
                                                                 new ChannelListenerNotifierImpl() );

        channel = runtimeEventAcceptorImpl.createRuntimeEventChannel( "TestChannel", Unit.TEXT, "" );

        stateIdBefore = runtimeEventAcceptorImpl.getStateId();
    }

    @Test
    public void hasStateIdChangedWithGetStateId()
    {
        runtimeEventAcceptorImpl.acceptEvent( 1000, channel, null, "Event" );

        int stateIdAfter = runtimeEventAcceptorImpl.getStateId();
        Assert.assertTrue( "State-ID of RuntimeEventAcceptorImpl before accept event and after not equal.",
                           stateIdBefore != stateIdAfter );
    }

    @Test
    public void hasStateIdChangedWithHasStateId()
    {
        runtimeEventAcceptorImpl.acceptEvent( 1000, channel, null, "Event" );

        Assert.assertTrue( "State-ID of RuntimeEventAcceptorImpl before accept event and after not equal.",
                           runtimeEventAcceptorImpl.hasStateIdChanged( stateIdBefore ) );
    }

    @Test
    public void hasStateIdChangedWithNull()
    {
        runtimeEventAcceptorImpl.acceptEvent( 1000, channel, null, "Event" );

        Assert.assertTrue( "State-ID of RuntimeEventAcceptorImpl before accept event and after not equal.",
                           runtimeEventAcceptorImpl.hasStateIdChanged( null ) );
    }

    @Test
    public void hasStateIdNotChangedWithHasStateId()
    {
        Assert.assertFalse( "State-ID of RuntimeEventAcceptorImpl did change, although no new event was posted.",
                            runtimeEventAcceptorImpl.hasStateIdChanged( stateIdBefore ) );
    }

}
