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
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class BeforeAfterInterceptor
{

    @RuntimeType
    public static Object intercept(@SuperCall Callable<Object> zuper, @Origin Class<?> clazz, @Origin Method m,
            @AllArguments Object[] args)
    {
        Object result = null;
        InterceptMethod delegateAnnotation = m.getAnnotation( InterceptMethod.class );
        if (delegateAnnotation != null)
        {
            Class<? extends MethodInterceptor> caller = delegateAnnotation.interceptor();
            MethodInterceptor delegationCaller = null;
            try
            {
                delegationCaller = caller.newInstance();
                delegationCaller.before( clazz, m, args );
                result = zuper.call();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (delegationCaller != null)
                {
                    delegationCaller.after( clazz, m, args );
                }
            }

        }

        return result;
    }

}
