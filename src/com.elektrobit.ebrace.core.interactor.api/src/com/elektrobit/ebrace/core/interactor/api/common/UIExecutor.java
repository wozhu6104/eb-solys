/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.common;

public class UIExecutor
{
    private static UIExecutorIF executor;

    public static void set(UIExecutorIF executor)
    {
        UIExecutor.executor = executor;
    }

    public static void post(Runnable r)
    {
        if (executor == null)
            throw new IllegalStateException( "UIExecutor is not set" );
        else
            executor.execute( r );
    }
}
