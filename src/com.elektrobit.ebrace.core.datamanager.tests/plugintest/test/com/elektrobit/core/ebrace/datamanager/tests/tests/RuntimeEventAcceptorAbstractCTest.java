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
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

@RunWith(Parameterized.class)
public class RuntimeEventAcceptorAbstractCTest
{
    protected RuntimeEventAcceptor runtimeEventAcceptor;
    private final int waitForCommitTime;

    public RuntimeEventAcceptorAbstractCTest(int waitForCommitTime)
    {
        this.waitForCommitTime = waitForCommitTime;
    }

    @Parameters
    public static Collection<Object[]> commitTimeParameter()
    {
        Object[][] data = new Object[][]{{1}};
        return Arrays.asList( data );
    }

    @Before
    public void setup()
    {
        runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
    }

    protected void waitForCommit()
    {
        try
        {
            Thread.sleep( waitForCommitTime );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup()
    {
        runtimeEventAcceptor.dispose();
    }
}
