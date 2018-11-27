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
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

public class RuntimeEventTagTest
{
    private RuntimeEventAcceptorImpl runtimeEventAcceptor;

    @Before
    public void setup()
    {
        RuntimeEventChannelManager channelManager = new RuntimeEventChannelManagerImpl( Mockito
                .mock( ListenerNotifier.class ) );

        runtimeEventAcceptor = new RuntimeEventAcceptorImpl( channelManager,
                                                             null,
                                                             Mockito.mock( RuntimeEventNotifier.class ),
                                                             new ChannelListenerNotifierImpl(),
                                                             Mockito.mock( EventHookRegistry.class ) );

        RuntimeEventChannel<String> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( "Channel", Unit.TEXT, "" );
        runtimeEventAcceptor.acceptEventMicros( 100000, channel, null, "" );
    }

    @Test
    public void notificationAfterAddingTag()
    {
        int stateIdBefore = runtimeEventAcceptor.getStateId();
        runtimeEventAcceptor.setTag( runtimeEventAcceptor.getAllRuntimeEvents().get( 0 ), RuntimeEventTag.ERROR, "" );

        Assert.assertTrue( "Expecting that stateId changed, after setting a tag.",
                           stateIdBefore != runtimeEventAcceptor.getStateId() );
    }

    @Test
    public void setTagWasSuccessfull()
    {
        RuntimeEvent<?> runtimeEvent = runtimeEventAcceptor
                .setTag( runtimeEventAcceptor.getAllRuntimeEvents().get( 0 ), RuntimeEventTag.ERROR, "My Description" );

        Assert.assertTrue( "Expecting that RuntimeEvent is tagged return true.", runtimeEvent.isTagged() );
        Assert.assertEquals( RuntimeEventTag.ERROR, runtimeEvent.getTag() );
        Assert.assertEquals( "My Description", runtimeEvent.getTagDescription() );
    }

    @Test
    public void clearTagWasSuccessfull()
    {
        RuntimeEvent<?> runtimeEventWithTag = runtimeEventAcceptor
                .setTag( runtimeEventAcceptor.getAllRuntimeEvents().get( 0 ), RuntimeEventTag.ERROR, "My Description" );
        RuntimeEvent<?> runtimeEventWithoutTag = runtimeEventAcceptor.clearTag( runtimeEventWithTag );

        Assert.assertFalse( "Expecting that RuntimeEvent is tagged return false.", runtimeEventWithoutTag.isTagged() );
        Assert.assertNull( "Expecting no tag anymore.", runtimeEventWithoutTag.getTag() );
        Assert.assertTrue( "Expecting no tag description anymore.",
                           runtimeEventWithoutTag.getTagDescription().isEmpty() );
    }

    @Test
    public void notificationAfterRemoveTag()
    {
        runtimeEventAcceptor.setTag( runtimeEventAcceptor.getAllRuntimeEvents().get( 0 ), RuntimeEventTag.ERROR, "" );

        int stateIdBefore = runtimeEventAcceptor.getStateId();
        runtimeEventAcceptor.clearTag( runtimeEventAcceptor.getAllRuntimeEvents().get( 0 ) );

        Assert.assertTrue( "Expecting that stateId changed, after clearing a tag.",
                           runtimeEventAcceptor.hasStateIdChanged( stateIdBefore ) );
    }

}
