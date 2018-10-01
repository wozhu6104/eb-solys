/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.debug.annotations.api;

import java.lang.reflect.Method;

public class EnterExitPrinter implements MethodInterceptor
{

    @Override
    public void before(Class<?> caller, Method method, Object[] args)
    {

        String msg = "Enter: " + caller.getName() + "." + method.getName() + "[";

        for (Object nextArg : args)
        {
            msg += nextArg.toString() + ",";
        }

        // remove last semicolon
        msg = msg.substring( 0, msg.length() - 1 );

        msg += "]";

        System.out.println( System.currentTimeMillis() + " | " + msg );
    }

    @Override
    public void after(Class<?> caller, Method method, Object[] args)
    {
        System.out.println( System.currentTimeMillis() + " | " + "Exit: " + caller.getName() + "." + method.getName() );
    }

}
