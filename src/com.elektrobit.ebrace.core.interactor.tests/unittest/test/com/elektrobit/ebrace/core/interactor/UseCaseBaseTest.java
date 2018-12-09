/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.junit.Before;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutorIF;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutorIF;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRepeatedTask;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;

public class UseCaseBaseTest
{

    private TestUseCaseExecutor testUseCaseExecutor;

    @Before
    public void initUIExecutorAsSync()
    {
        UIExecutor.set( new UIExecutorIF()
        {
            @Override
            public void execute(Runnable r)
            {
                r.run();
            }
        } );
    }

    @Before
    public void initUseCaseExecutorSync()
    {
        testUseCaseExecutor = new TestUseCaseExecutor();
        UseCaseExecutor.setExecutorService( testUseCaseExecutor );
    }

    class TestUseCaseExecutor implements UseCaseExecutorIF
    {
        private final List<UseCaseRepeatedTask> repeatedTasks = new ArrayList<UseCaseRepeatedTask>();

        @Override
        public void scheduleDelayed(TimerTask task, long delay)
        {
            task.run();
        }

        @Override
        public void schedule(UseCaseRunnable r)
        {
            r.run();
        }

        @Override
        public UseCaseRepeatedTask scheduleRepeated(UseCaseRepeatedTask task, long periodMs)
        {
            repeatedTasks.add( task );
            return task;
        }

        public void executeRepeatedTimerTasks()
        {
            for (UseCaseRepeatedTask timerTask : repeatedTasks)
            {
                if (!timerTask.isCancelled())
                {
                    timerTask.execute();
                }
            }
        }
    }

    public final void executePlannedRepeatedTasks()
    {
        testUseCaseExecutor.executeRepeatedTimerTasks();
    }
}
