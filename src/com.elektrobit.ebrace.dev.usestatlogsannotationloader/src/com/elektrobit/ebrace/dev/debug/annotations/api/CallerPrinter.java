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

public class CallerPrinter implements MethodInterceptor
{

    @Override
    public void before(Class<?> caller, Method method, Object[] args)
    {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println( stackTraceElements[4].getClassName() + "." + stackTraceElements[4].getMethodName() );
    }

    @Override
    public void after(Class<?> caller, Method method, Object[] args)
    {
    }

}
