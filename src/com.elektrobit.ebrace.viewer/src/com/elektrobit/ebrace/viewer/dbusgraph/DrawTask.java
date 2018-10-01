/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

class DrawTask extends TimerTask
{

    private final List<DrawNodeTimerTask> taskNodeList = new CopyOnWriteArrayList<DrawNodeTimerTask>();
    private final List<DrawCompleteTimerTask> taskCompleteList = new CopyOnWriteArrayList<DrawCompleteTimerTask>();

    public void add(DrawNodeTimerTask timerTask)
    {
        synchronized (taskNodeList)
        {
            if (taskCompleteList.isEmpty())
            {
                taskNodeList.add( timerTask );
            }
        }
    }

    public void add(DrawCompleteTimerTask timerTask)
    {
        if (taskCompleteList.isEmpty())
        {
            taskCompleteList.add( timerTask );
        }
    }

    @Override
    public void run()
    {
        if (!taskCompleteList.isEmpty())
        {
            runNextCompleteTimerTask();
        }
        else
        {
            runAllNodeTasks();
        }
    }

    private void runAllNodeTasks()
    {
        synchronized (taskNodeList)
        {
            for (TimerTask timerTask : taskNodeList)
            {
                timerTask.run();
            }
            taskNodeList.clear();
        }
    }

    private void runNextCompleteTimerTask()
    {
        DrawCompleteTimerTask removedTimerTask = taskCompleteList.remove( 0 );
        removedTimerTask.run();
    }

}
