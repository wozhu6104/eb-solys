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
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetFirstRuntimeEventCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetFirstRuntimeEventCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getFirstRuntimeEventWithOneEvent()
    {
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelX",
                                                                                            Unit.COUNT,
                                                                                            "" );

        runtimeEventAcceptor.acceptEventMicros( 1111L, channel, ModelElement.NULL_MODEL_ELEMENT, 1111L );
        waitForCommit();

        Assert.assertEquals( 1111L, runtimeEventAcceptor.getFirstRuntimeEvent().getTimestamp() );

    }

    @Test
    public void getFirstRuntimeEventWithTwoEvents()
    {
        runtimeEventAcceptor.dispose();
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelX",
                                                                                            Unit.COUNT,
                                                                                            "" );

        runtimeEventAcceptor.acceptEventMicros( 1000L, channel, ModelElement.NULL_MODEL_ELEMENT, 1000L );
        runtimeEventAcceptor.acceptEventMicros( 999L, channel, ModelElement.NULL_MODEL_ELEMENT, 999L );

        waitForCommit();

        Assert.assertEquals( 999, runtimeEventAcceptor.getFirstRuntimeEvent().getTimestamp() );
    }

}
