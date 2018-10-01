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
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetRuntimeEventsForTimespanAndChannelsCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetRuntimeEventsForTimespanAndChannelsCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getRuntimeEventsForTimespanAndChannels() throws Exception
    {
        int startTimestamp = 1001;
        int endTimestamp = 1050;

        RuntimeEventChannel<String> channelStringWanted1 = runtimeEventAcceptor
                .createRuntimeEventChannel( "Channel1", Unit.TEXT, "" );

        RuntimeEventChannel<String> channelString2 = runtimeEventAcceptor
                .createRuntimeEventChannel( "Channel2-not Wanted", Unit.TEXT, "" );

        RuntimeEventChannel<Long> channelIntegerWanted2 = runtimeEventAcceptor
                .createRuntimeEventChannel( "Channel3", Unit.COUNT, "" );

        for (long i = 1000; i < 1100; i++)
        {
            runtimeEventAcceptor
                    .acceptEventMicros( i, channelStringWanted1, ModelElement.NULL_MODEL_ELEMENT, "value" + i );
            runtimeEventAcceptor.acceptEventMicros( i, channelString2, ModelElement.NULL_MODEL_ELEMENT, "valueX" + i );
            runtimeEventAcceptor.acceptEventMicros( i, channelIntegerWanted2, ModelElement.NULL_MODEL_ELEMENT, i );
        }

        waitForCommit();

        List<RuntimeEvent<?>> result = runtimeEventAcceptor
                .getRuntimeEventForTimeStampIntervalForChannels( startTimestamp,
                                                                 endTimestamp,
                                                                 new ArrayList<RuntimeEventChannel<?>>( Arrays
                                                                         .asList( channelStringWanted1,
                                                                                  channelIntegerWanted2 ) ) );
        Assert.assertEquals( 100, result.size() );

        for (RuntimeEvent<?> runtimeEvent : result)
        {
            RuntimeEventChannel<?> channel = runtimeEvent.getRuntimeEventChannel();
            Assert.assertTrue( channel != null );
            Assert.assertTrue( channel.equals( channelStringWanted1 ) || channel.equals( channelIntegerWanted2 ) );
            Long timestamp = runtimeEvent.getTimestamp();
            Assert.assertTrue( timestamp >= startTimestamp && timestamp <= endTimestamp );
        }
    }
}
