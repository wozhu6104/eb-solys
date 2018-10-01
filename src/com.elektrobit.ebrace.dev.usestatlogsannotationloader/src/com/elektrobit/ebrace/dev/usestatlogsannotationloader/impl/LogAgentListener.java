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

import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

public class LogAgentListener implements Listener
{
    private final DelayedMessagePrinter delayedMessagePrinter;

    public LogAgentListener()
    {
        delayedMessagePrinter = new DelayedMessagePrinter();
        new Thread( delayedMessagePrinter ).start();
    }

    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
            boolean loaded, DynamicType dynamicType)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded)
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
        delayedMessagePrinter.update( System.currentTimeMillis() );
    }

}
