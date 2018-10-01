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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetRuntimeEventChannelsForTypeCTest extends RuntimeEventAcceptorAbstractCTest
{
    public RuntimeEventAcceptorGetRuntimeEventChannelsForTypeCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void getRuntimeEventChannelForType()
    {
        RuntimeEventChannel<String> channelString = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelString",
                                                                                                    Unit.TEXT,
                                                                                                    "" );
        RuntimeEventChannel<Long> channelCount = runtimeEventAcceptor.createRuntimeEventChannel( "ChannelInteger",
                                                                                                 Unit.COUNT,
                                                                                                 "" );

        waitForCommit();

        Assert.assertEquals( Arrays.asList( channelString ),
                             runtimeEventAcceptor.getRuntimeEventChannelsForUnit( Unit.TEXT ) );
        Assert.assertEquals( Arrays.asList( channelCount ),
                             runtimeEventAcceptor.getRuntimeEventChannelsForUnit( Unit.COUNT ) );
        Assert.assertNotNull( runtimeEventAcceptor.getRuntimeEventChannelsForUnit( Unit.PERCENT ) );
        Assert.assertTrue( runtimeEventAcceptor.getRuntimeEventChannelsForUnit( Unit.PERCENT ).isEmpty() );
    }
}
