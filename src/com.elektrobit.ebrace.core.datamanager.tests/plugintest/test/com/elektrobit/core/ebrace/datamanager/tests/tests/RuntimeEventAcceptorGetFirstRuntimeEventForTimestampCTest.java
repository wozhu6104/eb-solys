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

public class RuntimeEventAcceptorGetFirstRuntimeEventForTimestampCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetFirstRuntimeEventForTimestampCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getFirstRuntimeEventForTimestamp() throws Exception
    {
        RuntimeEventChannel<String> channelString = runtimeEventAcceptor.createRuntimeEventChannel( "Channel1",
                                                                                                    Unit.TEXT,
                                                                                                    "" );

        RuntimeEventChannel<String> channelString2 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel2",
                                                                                                     Unit.TEXT,
                                                                                                     "" );

        RuntimeEventChannel<Long> channelInteger = runtimeEventAcceptor.createRuntimeEventChannel( "Channel3",
                                                                                                   Unit.COUNT,
                                                                                                   "" );

        for (long i = 0; i < 100; i++)
        {
            runtimeEventAcceptor.acceptEventMicros( i, channelString, ModelElement.NULL_MODEL_ELEMENT, "value" + i );
            runtimeEventAcceptor.acceptEventMicros( i, channelString2, ModelElement.NULL_MODEL_ELEMENT, "valueX" + i );
            runtimeEventAcceptor.acceptEventMicros( i, channelInteger, ModelElement.NULL_MODEL_ELEMENT, i );
        }

        waitForCommit();

        RuntimeEvent<?> event = runtimeEventAcceptor.getFirstRuntimeEventForTimeStampInterval( 50, 55, channelString2 );
        Assert.assertEquals( event.getRuntimeEventChannel().getName(), channelString2.getName() );
        Assert.assertEquals( event.getTimestamp(), 50 );

    }
}
