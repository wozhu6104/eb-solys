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

public abstract class UseCaseRepeatedTask
{
    private TimerTask timerTask;
    private boolean cancelled = false;

    public abstract void execute();

    public UseCaseRepeatedTask()
    {
        createTimerTask();
    }

    private void createTimerTask()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                execute();
            }
        };
    }

    public TimerTask getTimerTask()
    {
        return timerTask;
    }

    public void cancel()
    {
        timerTask.cancel();
        this.cancelled = true;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }
}
