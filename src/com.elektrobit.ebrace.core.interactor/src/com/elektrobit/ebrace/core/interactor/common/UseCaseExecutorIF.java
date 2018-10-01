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

public interface UseCaseExecutorIF
{
    public void schedule(Runnable r);

    /**
     * Schedule TimerTask and execute it once after <i>delay</i> miliseconds. WARNING: Do not perform any long lasting
     * work inside the TimerTask as it would block other TimerTasks from executing. For long lasting operations use
     * UseCaseExecutorIF.schedule()
     */
    public void scheduleDelayed(TimerTask task, long delay);

    /**
     * Schedule TimerTask that will be executed periodically. WARNING: Do not perform any long lasting work inside the
     * TimerTask as it would block other TimerTasks from executing. For long lasting operations use
     * UseCaseExecutorIF.schedule()
     */
    public UseCaseRepeatedTask scheduleRepeated(UseCaseRepeatedTask task, long periodMs);
}
