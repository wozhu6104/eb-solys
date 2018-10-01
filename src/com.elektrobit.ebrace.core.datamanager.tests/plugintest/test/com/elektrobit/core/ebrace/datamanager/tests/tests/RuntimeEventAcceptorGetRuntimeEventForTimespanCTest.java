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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetRuntimeEventForTimespanCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetRuntimeEventForTimespanCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getRuntimeEventsInTimespan()
    {
        RuntimeEventChannel<Long> channelInteger = runtimeEventAcceptor.createRuntimeEventChannel( "Channel",
                                                                                                   Unit.COUNT,
                                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 999L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 999L );
        runtimeEventAcceptor.acceptEventMicros( 1000L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 1000L );
        runtimeEventAcceptor.acceptEventMicros( 1999L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 1999L );
        runtimeEventAcceptor.acceptEventMicros( 2000L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 2000L );
        runtimeEventAcceptor.acceptEventMicros( 2001L, channelInteger, ModelElement.NULL_MODEL_ELEMENT, 2001L );

        waitForCommit();

        List<RuntimeEvent<?>> eventsOfTimespan = runtimeEventAcceptor.getRuntimeEventsOfTimespan( 1000, 2000 );

        Assert.assertEquals( 3, eventsOfTimespan.size() );

        Assert.assertTrue( containsTimestamp( eventsOfTimespan, 1000L ) );
        Assert.assertTrue( containsTimestamp( eventsOfTimespan, 1999L ) );
        Assert.assertTrue( containsTimestamp( eventsOfTimespan, 2000L ) );

        // Assert.assertEquals( 1000L, eventsOfTimespan.get( 0 ).getTimestamp() );
        // Assert.assertEquals( 1999L, eventsOfTimespan.get( 1 ).getTimestamp() );
        // Assert.assertEquals( 2000L, eventsOfTimespan.get( 2 ).getTimestamp() );
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
