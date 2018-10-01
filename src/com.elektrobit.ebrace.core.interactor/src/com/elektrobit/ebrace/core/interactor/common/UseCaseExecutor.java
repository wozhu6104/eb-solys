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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UseCaseExecutor
{
    private static volatile ExecutorService executorService = Executors
            .newFixedThreadPool( 10, new UseCaseThreadFactory() );

    private static UseCaseExecutorIF useCaseExecutor;

    private static UseCaseExecutorIF getUseCaseExecutor()
    {
        if (useCaseExecutor == null)
            useCaseExecutor = new UseCaseExecutorIF()
            {
                final Timer taskTimer = new Timer( "UseCaseExecutor timer" );

                @Override
                public void scheduleDelayed(TimerTask task, long delay)
                {
                    taskTimer.schedule( task, delay );
                }

                @Override
                public UseCaseRepeatedTask scheduleRepeated(UseCaseRepeatedTask task, long periodMs)
                {
                    taskTimer.schedule( task.getTimerTask(), 0, periodMs );
                    return task;
                }

                @Override
                public void schedule(Runnable r)
                {
                    if (r != null)
                        executorService.execute( r );
                }
            };

        return useCaseExecutor;
    }

    public static void schedule(Runnable r)
    {
        getUseCaseExecutor().schedule( r );
    }

    public static void scheduleDelayed(TimerTask task, long delay)
    {
        getUseCaseExecutor().scheduleDelayed( task, delay );
    }

    public static UseCaseRepeatedTask scheduleRepeated(UseCaseRepeatedTask task, long periodMs)
    {
        getUseCaseExecutor().scheduleRepeated( task, periodMs );
        return task;
    }

    public static void setExecutorService(UseCaseExecutorIF useCaseExecutorRef)
    {
        useCaseExecutor = useCaseExecutorRef;
    }
}
