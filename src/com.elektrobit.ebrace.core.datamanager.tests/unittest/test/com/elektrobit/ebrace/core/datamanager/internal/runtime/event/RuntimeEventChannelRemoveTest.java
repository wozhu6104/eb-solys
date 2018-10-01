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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.internal.ModelElementPoolImpl;
import com.elektrobit.ebrace.core.datamanager.internal.channels.impl.RuntimeEventChannelManagerImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.comrelation.ComRelationAcceptorImpl;
import com.elektrobit.ebrace.core.datamanager.internal.model.structure.StructureAcceptorImpl;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventAcceptorImpl;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.api.RuntimeEventNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventChannelRemoveTest
{

    private final ModelElementPoolImpl modelElementPool = new ModelElementPoolImpl();

    private final RuntimeEventChannelManagerImpl channelManager = new RuntimeEventChannelManagerImpl( Mockito
            .mock( ListenerNotifier.class ) );

    private final RuntimeEventAcceptorImpl eventAcceptor = new RuntimeEventAcceptorImpl( channelManager,

                                                                                         new ModelElementPoolImpl(),
                                                                                         Mockito.mock( RuntimeEventNotifier.class ),
                                                                                         Mockito.mock( ChannelListenerNotifier.class ) );

    private final StructureAcceptorImpl structureAcceptor = new StructureAcceptorImpl();

    private final ComRelationAcceptorImpl comRelationAcceptor = new ComRelationAcceptorImpl();

    private RuntimeEventChannel<String> removableChannel = null;
    private RuntimeEventChannel<Long> remainingChannel = null;

    @Before
    public void setup()
    {
        removableChannel = channelManager.createOrGetRuntimeEventChannel( "Removable", Unit.TEXT, "" );
        eventAcceptor.acceptEventMicros( 1000, removableChannel, null, "remove me!" );
    }

    @Test
    public void removeChannel()
    {
        eventAcceptor.removeRuntimeEventChannel( removableChannel );
        assertFalse( "Channel should not exist", channelManager.checkIfChannelWithNameExists( "Removable" ) );
        assertTrue( "Events should be empty", eventAcceptor.getAllRuntimeEvents().isEmpty() );

        removableChannel = channelManager.createOrGetRuntimeEventChannel( "Removable", Unit.TEXT, "" );
        eventAcceptor.acceptEventMicros( 1000, removableChannel, null, "remove me!" );
        remainingChannel = channelManager.createOrGetRuntimeEventChannel( "Remaining", Unit.COUNT, "" );
        eventAcceptor.acceptEventMicros( 1000, remainingChannel, null, 7l );

        structureAcceptor.setModelElementPool( modelElementPool );
        comRelationAcceptor.bindModelElementPool( modelElementPool );
    }

    @Test
    public void channelRemoved()
    {
        eventAcceptor.removeRuntimeEventChannel( removableChannel );
        assertFalse( "Channel should not exist", channelManager.checkIfChannelWithNameExists( "Removable" ) );
    }

    @Test
    public void eventsRemoved()
    {
        eventAcceptor.removeRuntimeEventChannel( removableChannel );
        assertTrue( "Events should be removed", eventAcceptor.getAllRuntimeEvents().size() == 1 );
    }

}
