/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.thread;

import java.util.concurrent.CountDownLatch;

import lombok.extern.log4j.Log4j;

@Log4j
public class UninterruptibleCountDownLatch
{
    private final CountDownLatch countDownLatch;

    public UninterruptibleCountDownLatch(int count)
    {
        countDownLatch = new CountDownLatch( count );
    }

    public void await()
    {
        try
        {
            countDownLatch.await();
        }
        catch (InterruptedException e)
        {
            log.warn( "Latch waiting has been interrupted, waiting again..", e );
            await();
        }
    }

    public void countDown()
    {
        countDownLatch.countDown();
    }
}
