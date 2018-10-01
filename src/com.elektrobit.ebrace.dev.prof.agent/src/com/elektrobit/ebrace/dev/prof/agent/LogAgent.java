/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.prof.agent;

import java.lang.instrument.Instrumentation;

import com.elektrobit.ebrace.dev.prof.agent.api.LogInterceptor;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

class LogAgent
{
    public static void premain(String arguments, Instrumentation instrumentation)
    {
        System.out.println( "Instrumenting start." );
        new AgentBuilder.Default().with( new AgentBuilder.Listener()
        {

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
                    boolean loaded, DynamicType dynamicType)
            {
                System.out.println( "Transformed - " + typeDescription + ", type = " + dynamicType );
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
                    boolean loaded)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded,
                    Throwable throwable)
            {
                System.out.println( "Error - " + typeName + ", " + throwable.getMessage() );
                throwable.printStackTrace();

            }

            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded)
            {
                // TODO Auto-generated method stub

            }
        } ).type( ElementMatchers.nameStartsWith( "com.elektrobit.ebrace.targetdata.dlt." ) )
                .transform( new AgentBuilder.Transformer()
                {
                    @Override
                    public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
                            ClassLoader classLoader, JavaModule module)
                    {
                        return builder.method( ElementMatchers.any() )
                                .intercept( MethodDelegation.to( LogInterceptor.class ) );
                    }
                } ).installOn( instrumentation );

        // new AgentBuilder.Default()
        // .ignore(ElementMatchers.isInterface().or(ElementMatchers.isEnum()))
        // .type(ElementMatchers.hasSuperType(ElementMatchers.nameStartsWith("com.elektrobit.ebrace.interactor.api.")))
        // .transform(new AgentBuilder.Transformer() {
        // @Override
        // public DynamicType.Builder transform(DynamicType.Builder builder, TypeDescription typeDescription,
        // ClassLoader classloader) {
        // return builder
        // .method(ElementMatchers.isDeclaredBy(ElementMatchers.nameStartsWith("com.elektrobit.ebrace.interactor.api.")))
        // .intercept(MethodDelegation.to(LogInterceptor.class));
        // }
        // }).installOn(instrumentation);
        // System.out.println("Instrumenting end.");
    }
}
