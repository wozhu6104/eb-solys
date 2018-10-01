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
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorStateIDTest
{
    private RuntimeEventProvider runtimeEventProvider;
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private RuntimeEventChannel<Long> channel;
    private int stateIdBefore;

    @Before
    public void setup()
    {
        runtimeEventProvider = CoreServiceHelper.getRuntimeEventProvider();
        runtimeEventAcceptor = CoreServiceHelper.getRuntimeEventAcceptor();

        channel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "TestChannel", Unit.COUNT, "" );

        stateIdBefore = runtimeEventProvider.getStateId();
    }

    @Test
    public void getStateIdTest() throws Exception
    {
        runtimeEventAcceptor.acceptEvent( 1, channel, null, 0L );

        int stateIdAfter = runtimeEventProvider.getStateId();
        Assert.assertTrue( "StateId before acceptEvent and after should be different.", stateIdBefore != stateIdAfter );
    }

    @Test
    public void hasStateIdChangedTest() throws Exception
    {
        runtimeEventAcceptor.acceptEvent( 1, channel, null, 0L );

        Assert.assertTrue( "StateId before acceptEvent and after should be different.",
                           runtimeEventProvider.hasStateIdChanged( stateIdBefore ) );
    }
}
