/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelsCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelsCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getRuntimeEventsOfChannels() throws Exception
    {
        RuntimeEventChannel<String> channelString = runtimeEventAcceptor.createRuntimeEventChannel( "Channel1",
                                                                                                    Unit.TEXT,
                                                                                                    "" );

        RuntimeEventChannel<String> channelString2 = runtimeEventAcceptor
                .createRuntimeEventChannel( "Channel2-not Wanted", Unit.TEXT, "" );

        RuntimeEventChannel<Long> channelInteger = runtimeEventAcceptor.createRuntimeEventChannel( "Channel3",
                                                                                                   Unit.COUNT,
                                                                                                   "" );

        for (long i = 0; i < 100; i++)
        {
            runtimeEventAcceptor.acceptEvent( System.currentTimeMillis(),
                                              channelString,
                                              ModelElement.NULL_MODEL_ELEMENT,
                                              "value" + i );
            runtimeEventAcceptor.acceptEvent( System.currentTimeMillis(),
                                              channelString2,
                                              ModelElement.NULL_MODEL_ELEMENT,
                                              "valueX" + i );
            runtimeEventAcceptor.acceptEvent( System.currentTimeMillis(),
                                              channelInteger,
                                              ModelElement.NULL_MODEL_ELEMENT,
                                              i );
        }

        List<RuntimeEventChannel<?>> wantedChannels = new ArrayList<RuntimeEventChannel<?>>();
        wantedChannels.add( channelString );
        wantedChannels.add( channelInteger );

        waitForCommit();

        Assert.assertEquals( 200,
                             runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannels( wantedChannels ).size() );

        for (RuntimeEvent<?> runtimeEvent : runtimeEventAcceptor
                .getRuntimeEventsOfRuntimeEventChannels( wantedChannels ))
        {
            RuntimeEventChannel<?> channel = runtimeEvent.getRuntimeEventChannel();
            Assert.assertTrue( channel != null );
            Assert.assertTrue( channel.equals( channelString ) || channel.equals( channelInteger ) );
        }
    }
}
