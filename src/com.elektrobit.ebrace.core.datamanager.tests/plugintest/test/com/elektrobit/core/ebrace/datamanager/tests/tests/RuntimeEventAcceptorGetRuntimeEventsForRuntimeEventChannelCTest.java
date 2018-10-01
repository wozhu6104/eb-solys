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

public class RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetRuntimeEventsForRuntimeEventChannelCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getRuntimeEventsOfChannel() throws Exception
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

        waitForCommit();

        Assert.assertEquals( 100, runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( channelString2 ).size() );

        for (RuntimeEvent<?> runtimeEvent : runtimeEventAcceptor
                .getRuntimeEventsOfRuntimeEventChannel( channelString2 ))
        {
            RuntimeEventChannel<?> channel = runtimeEvent.getRuntimeEventChannel();
            Assert.assertTrue( channel != null );
            Assert.assertTrue( channel.equals( channelString2 ) );
        }
    }
}
