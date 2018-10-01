/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.reset;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

public class ResetListenerNotificationTest implements ResetListener
{
    private final CountDownLatch latch = new CountDownLatch( 1 );
    private ResetNotifier resetNotifier;

    @Before
    public void setup()
    {
        GenericOSGIServiceRegistration.registerService( ResetListener.class, this );

        resetNotifier = CoreServiceHelper.getResetNotifier();
    }

    @Test
    public void notifyResetListenerTest()
    {
        resetNotifier.performReset();

        if (calledWithinOneSecond())
            Assert.assertTrue( true );
        else
            Assert.fail( "Expecting that reset listener was called within one second." );
    }

    private boolean calledWithinOneSecond()
    {
        boolean result = false;
        try
        {
            result = latch.await( 1000, TimeUnit.MILLISECONDS );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onReset()
    {
        latch.countDown();
    }
}
