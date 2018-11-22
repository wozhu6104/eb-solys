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

import lombok.extern.log4j.Log4j;

@Log4j
public class UseCaseRunnable implements Runnable
{

    private final String name;
    private final Runnable r;

    public UseCaseRunnable(String name, Runnable r)
    {
        this.name = name;
        this.r = r;
    }

    @Override
    public void run()
    {
        log.debug( "START: " + Thread.currentThread().getName() + ": " + name );
        r.run();
        log.debug( "END: " + Thread.currentThread().getName() + ": " + name );
    }

}
