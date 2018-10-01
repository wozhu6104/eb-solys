/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.usestatlogsannotationloader.impl;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogHandlerCaller;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

class InterceptAgent
{
    public static void premain(String arguments, Instrumentation instrumentation)
    {
        System.out.println( "JVM | " + ManagementFactory.getRuntimeMXBean().getStartTime() );
        new AgentBuilder.Default().with( new LogAgentListener() )
                .ignore( ElementMatchers.isInterface().or( ElementMatchers.isEnum() ) )
                .type( ElementMatchers.nameStartsWith( "com.elektrobit" ) ).transform( new AgentBuilder.Transformer()
                {
                    @Override
                    public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
                            ClassLoader classLoader, JavaModule module)
                    {
                        return builder.method( ElementMatchers.isAnnotatedWith( UseStatLog.class ) )
                                .intercept( MethodDelegation.to( UseStatLogHandlerCaller.class ) );
                    }

                } ).installOn( instrumentation );

        System.out.println( "PREMAIN | " + System.currentTimeMillis() );

        // new AgentBuilder.Default().with( new LogAgentListener() )
        // .ignore( ElementMatchers.isInterface().or( ElementMatchers.isEnum() ) )
        // .type( ElementMatchers.nameStartsWith( "com.elektrobit" ) ).transform( new AgentBuilder.Transformer()
        // {
        // @Override
        // public Builder<?> transform(Builder<?> builder, TypeDescription arg1, ClassLoader arg2)
        // {
        // return builder.method( ElementMatchers.isAnnotatedWith( InterceptMethod.class ) )
        // .intercept( MethodDelegation.to( BeforeAfterInterceptor.class ) );
        // }
        //
        // } ).installOn( instrumentation );

    }
}
