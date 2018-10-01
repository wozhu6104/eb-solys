/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.common;

import java.util.TimerTask;

public final class NotificationAccumulator
{
    private final int accumulationPeriod;
    private TimerTask delayedTask = null;
    private final NotificationAccumulatorCallback callback;

    public NotificationAccumulator(int accumulationPeriod, NotificationAccumulatorCallback callback)
    {
        this.accumulationPeriod = accumulationPeriod;
        this.callback = callback;
    }

    public void postNotification()
    {
        if (delayedTask == null)
        {
            delayedTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    postToCallback();
                    delayedTask = null;
                }
            };
            UseCaseExecutor.scheduleDelayed( delayedTask, accumulationPeriod );
        }
    }

    private void postToCallback()
    {
        callback.onAccumulatedNotification();
    }
}
