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

public class RuntimeEventAcceptorGetLatestRuntimeEventForTimespanCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetLatestRuntimeEventForTimespanCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getLatestRuntimeEventForTimespan()
    {
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelX",
                                                                                            Unit.COUNT,
                                                                                            "" );

        runtimeEventAcceptor.acceptEventMicros( 999L, channel, ModelElement.NULL_MODEL_ELEMENT, 999L );
        runtimeEventAcceptor.acceptEventMicros( 1000L, channel, ModelElement.NULL_MODEL_ELEMENT, 1000L );
        runtimeEventAcceptor.acceptEventMicros( 1010L, channel, ModelElement.NULL_MODEL_ELEMENT, 1010L );
        runtimeEventAcceptor.acceptEventMicros( 1999L, channel, ModelElement.NULL_MODEL_ELEMENT, 1999L );
        runtimeEventAcceptor.acceptEventMicros( 2001L, channel, ModelElement.NULL_MODEL_ELEMENT, 2001L );

        waitForCommit();

        Assert.assertEquals( 1999L,
                             runtimeEventAcceptor.getLastRuntimeEventForTimeStampInterval( 1000L, 2000L, channel )
                                     .getTimestamp() );

    }

}
