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

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetAllRuntimeEventsCTest extends RuntimeEventAcceptorAbstractCTest
{

    public RuntimeEventAcceptorGetAllRuntimeEventsCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getAllRuntimeEvents() throws Exception
    {
        RuntimeEventChannel<Long> channel1 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel1",
                                                                                             Unit.COUNT,
                                                                                             "" );

        RuntimeEventChannel<String> channel2 = runtimeEventAcceptor.createRuntimeEventChannel( "Channel2",
                                                                                               Unit.TEXT,
                                                                                               "" );

        runtimeEventAcceptor.acceptEvent( 1000, channel1, null, 1000L );
        runtimeEventAcceptor.acceptEvent( 1234, channel2, null, "Event1" );
        runtimeEventAcceptor.acceptEvent( 2000, channel1, null, 2000L );
        runtimeEventAcceptor.acceptEvent( 1990, channel2, null, "Event2" );

        waitForCommit();

        Assert.assertEquals( 4, runtimeEventAcceptor.getAllRuntimeEvents().size() );
    }

}
