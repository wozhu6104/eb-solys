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

import java.util.concurrent.ThreadFactory;

public class UseCaseThreadFactory implements ThreadFactory
{
    int counter = 0;

    @Override
    public Thread newThread(Runnable r)
    {
        counter++;
        return new Thread( r, "use-case-executor-pool-thread-" + String.valueOf( counter ) );
    }
}
