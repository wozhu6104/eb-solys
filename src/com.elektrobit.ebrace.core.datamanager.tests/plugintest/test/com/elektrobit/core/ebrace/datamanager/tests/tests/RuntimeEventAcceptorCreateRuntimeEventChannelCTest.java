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

public class RuntimeEventAcceptorCreateRuntimeEventChannelCTest extends RuntimeEventAcceptorAbstractCTest
{
    public RuntimeEventAcceptorCreateRuntimeEventChannelCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Test
    public void testCreateRuntimeEventChannel()
    {
        runtimeEventAcceptor.createRuntimeEventChannel( "Channel 1", Unit.KILOBYTE, "My first channel" );
        RuntimeEventChannel<Long> channel = runtimeEventAcceptor
                .createRuntimeEventChannel( "Channel 1", Unit.KILOBYTE, "My first channel" );

        Assert.assertNull( channel );
    }
}
