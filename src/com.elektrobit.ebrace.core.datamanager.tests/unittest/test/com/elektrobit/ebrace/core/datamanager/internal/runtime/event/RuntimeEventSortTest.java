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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.datamanager.internal.channels.impl.RuntimeEventChannelManagerImpl;
import com.elektrobit.ebrace.core.datamanager.internal.listener.ChannelListenerNotifierImpl;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventAcceptorImpl;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.api.RuntimeEventNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

public class RuntimeEventSortTest
{
    private RuntimeEventAcceptorImpl acceptor;

    @Before
    public void setup()
    {
        RuntimeEventChannelManager channelManager = new RuntimeEventChannelManagerImpl( Mockito
                .mock( ListenerNotifier.class ) );

        RuntimeEventNotifier runtimeEventNotifier = Mockito.mock( RuntimeEventNotifier.class );

        acceptor = new RuntimeEventAcceptorImpl( channelManager,
                                                 null,
                                                 runtimeEventNotifier,
                                                 new ChannelListenerNotifierImpl(),
                                                 Mockito.mock( EventHookRegistry.class ) );
        RuntimeEventChannel<Long> channel = acceptor.createOrGetRuntimeEventChannel( "test", Unit.COUNT, "" );
        acceptor.acceptEventMicros( 1000000, channel, null, 2L );
        acceptor.acceptEventMicros( 999999, channel, null, 1L );
    }

    @Test
    public void sortingDone()
    {
        Assert.assertEquals( 999999, acceptor.getFirstRuntimeEvent().getTimestamp() );
        Assert.assertEquals( 1000000, acceptor.getLatestRuntimeEvent().getTimestamp() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void resultListNotChangable()
    {
        acceptor.getAllRuntimeEvents().add( Mockito.mock( RuntimeEvent.class ) );
    }

}
