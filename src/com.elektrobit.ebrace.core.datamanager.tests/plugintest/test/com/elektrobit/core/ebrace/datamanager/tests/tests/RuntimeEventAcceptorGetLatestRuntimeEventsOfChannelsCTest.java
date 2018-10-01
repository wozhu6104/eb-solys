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

public class RuntimeEventAcceptorGetLatestRuntimeEventsOfChannelsCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetLatestRuntimeEventsOfChannelsCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void testGetLatestRuntimeEventsOfChannels()
    {
        RuntimeEventChannel<Long> channelInteger = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelInteger",
                                                                                                   Unit.COUNT,
                                                                                                   "" );
        RuntimeEventChannel<Long> channelInteger1 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel1",
                                                                                                    Unit.COUNT,
                                                                                                    "" );
        RuntimeEventChannel<Long> channelInteger2 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel2",
                                                                                                    Unit.COUNT,
                                                                                                    "" );

        runtimeEventAcceptor.acceptEventMicros( 999L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 999L );
        runtimeEventAcceptor.acceptEventMicros( 1001L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 1000L );

        runtimeEventAcceptor.acceptEventMicros( 1999L, channelInteger1, ModelElement.NULL_MODEL_ELEMENT, 1999L );
        runtimeEventAcceptor.acceptEventMicros( 2000L, channelInteger1, ModelElement.NULL_MODEL_ELEMENT, 2000L );

        runtimeEventAcceptor.acceptEventMicros( 1999L, channelInteger2, ModelElement.NULL_MODEL_ELEMENT, 1999L );
        runtimeEventAcceptor.acceptEventMicros( 2000L, channelInteger2, ModelElement.NULL_MODEL_ELEMENT, 2000L );

        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
        channels.add( channelInteger );
        channels.add( channelInteger1 );
        channels.add( channelInteger2 );

        waitForCommit();

        List<RuntimeEvent<?>> eventsOfTimestamp = runtimeEventAcceptor.getLatestRuntimeEventsOfChannels( channels );

        Assert.assertEquals( 3, eventsOfTimestamp.size() );

        Assert.assertTrue( containsTimestamp( eventsOfTimestamp, 1001L ) );
        Assert.assertTrue( containsTimestamp( eventsOfTimestamp, 2000L ) );
        Assert.assertTrue( containsTimestamp( eventsOfTimestamp, 2000L ) );

    }

    private boolean containsTimestamp(List<RuntimeEvent<?>> eventsOfTimespan, long expectedTimestamp)
    {
        for (RuntimeEvent<?> runtimeEvent : eventsOfTimespan)
        {
            if (runtimeEvent.getTimestamp() == expectedTimestamp)
            {
                return true;
            }
        }
        return false;
    }

}
