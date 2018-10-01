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

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetLatestRuntimeEventOfChannelCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetLatestRuntimeEventOfChannelCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getLatestRuntimeEventOfChannelWithOneChannel()
    {
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelX",
                                                                                            Unit.COUNT,
                                                                                            "" );

        Long timestamp = new Long( 1111 );

        runtimeEventAcceptor.acceptEventMicros( timestamp, channel, ModelElement.NULL_MODEL_ELEMENT, 1111L );
        waitForCommit();

        Assert.assertEquals( timestamp.longValue(),
                             runtimeEventAcceptor.getLatestRuntimeEventOfChannel( channel ).getTimestamp() );

    }

    @Test
    public void getLatestRuntimeEventOfChannelWithTwoChannels()
    {
        RuntimeEventChannel<Long> channel1 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel1",
                                                                                             Unit.COUNT,
                                                                                             "" );
        RuntimeEventChannel<Long> channel2 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel2",
                                                                                             Unit.COUNT,
                                                                                             "" );

        runtimeEventAcceptor.acceptEventMicros( 999, channel1, ModelElement.NULL_MODEL_ELEMENT, 1L );
        runtimeEventAcceptor.acceptEventMicros( 1000, channel2, ModelElement.NULL_MODEL_ELEMENT, 1L );
        runtimeEventAcceptor.acceptEventMicros( 1001, channel1, ModelElement.NULL_MODEL_ELEMENT, 1L );
        runtimeEventAcceptor.acceptEventMicros( 1002, channel2, ModelElement.NULL_MODEL_ELEMENT, 1L );

        waitForCommit();

        RuntimeEvent<?> event = runtimeEventAcceptor.getLatestRuntimeEventOfChannel( channel1 );
        Assert.assertEquals( channel1, event.getRuntimeEventChannel() );
        Assert.assertEquals( 1001, event.getTimestamp() );
    }

    @Test
    public void getLatestRuntimeEventOfChannelWithOneCachedAndOneStoredEvent()
    {
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelX",
                                                                                            Unit.COUNT,
                                                                                            "" );

        runtimeEventAcceptor.acceptEventMicros( 999, channel, ModelElement.NULL_MODEL_ELEMENT, 999L );
        waitForCommit();
        runtimeEventAcceptor.acceptEventMicros( 1000, channel, ModelElement.NULL_MODEL_ELEMENT, 1000L );

        RuntimeEvent<?> event = runtimeEventAcceptor.getLatestRuntimeEventOfChannel( channel );
        Assert.assertEquals( 1000, event.getTimestamp() );
    }

}
