/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.prof.agent.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class LogInterceptor
{

    private static final Logger log = Logger.getLogger( "logntrace" );

    @RuntimeType
    public static Object intercept(@SuperCall Callable<Object> zuper, @Origin Class<?> clazz, @Origin Method m,
            @AllArguments Object[] allArguments)
    {

        String callee = extractCallee( clazz, m );
        String caller = extractCaller();

        String[] calleeParts = callee.split( "\\." );
        String[] callerParts = caller.split( "\\." );

        String calleeClass = calleeParts[calleeParts.length - 2];
        String callerClass = callerParts[callerParts.length - 2];

        String calleeMethod = calleeParts[calleeParts.length - 1];
        String callerMethod = callerParts[callerParts.length - 1];

        List<String> parameters = new ArrayList<>();
        for (int i = 0; i < allArguments.length; i++)
        {
            parameters.add( ("\"" + m.getParameters()[i].getName() + "\":\"" + allArguments[i] + "\"")
                    .replaceAll( "[^\\x00-\\x7F]", "?" ).replaceAll( "\u0000", "?" ).replace( '\n', ' ' ) );
            System.out.println( i + ":" + parameters.get( i ) );
        }

        log.debug( "\"summary\":\"" + callerMethod + "->" + calleeMethod + "\",\"value\":{"
                + String.join( ",", parameters ) + "},\"edge\":{\"source\":\"" + callerClass + "\",\"destination\":"
                + "\"" + calleeClass + "\"," + "\"type\":\"request\"}" );

        // if (isPublicCall( callee, caller ) && isRelevantCall( callee, caller ))
        // {
        // log.debug( caller + " -> " + callee + ", " + allArguments.length );
        // }

        Object result = null;
        try
        {
            result = zuper.call();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (isPublicCall( callee, caller ) && isRelevantCall( callee, caller ))
            {
                // log.debug( caller + " <- " + callee );
            }
        }
        return result;
    }

    private static boolean isRelevantCall(String callee, String caller)
    {
        String calleeMethodName = getMethodName( callee );
        String callerMethodName = getMethodName( caller );

        return !calleeMethodName.equals( "hashCode" ) && !calleeMethodName.equals( "equals" )
                && !calleeMethodName.equals( "toString" ) && !callerMethodName.equals( "hashCode" )
                && !callerMethodName.equals( "equals" ) && !callerMethodName.equals( "toString" );
    }

    private static String getMethodName(String fullQualifiedMethodName)
    {
        String methodName = null;
        if (null != fullQualifiedMethodName && fullQualifiedMethodName.length() > 0)
        {
            int endIndex = fullQualifiedMethodName.lastIndexOf( "." );
            if (endIndex != -1)
            {
                methodName = fullQualifiedMethodName.substring( endIndex + 1, fullQualifiedMethodName.length() );
            }
        }

        return methodName;
    }

    private static boolean isPublicCall(String callee, String caller)
    {

        String fullQualifiedClassNameOfCallee = getFullQualifiedClassName( callee );
        String fullQualifiedClassNameOfCaller = getFullQualifiedClassName( caller );

        if (fullQualifiedClassNameOfCallee != null && fullQualifiedClassNameOfCaller != null)
        {
            return !fullQualifiedClassNameOfCallee.equals( fullQualifiedClassNameOfCaller );
        }
        else
        {
            return false;
        }
    }

    private static String getFullQualifiedClassName(String fullQualifiedMethodName)
    {
        String fullQualifiedClassName = null;
        if (null != fullQualifiedMethodName && fullQualifiedMethodName.length() > 0)
        {
            int endIndex = fullQualifiedMethodName.lastIndexOf( "." );
            if (endIndex != -1)
            {
                fullQualifiedClassName = fullQualifiedMethodName.substring( 0, endIndex - 1 );
            }
        }

        return fullQualifiedClassName;
    }

    private static String extractCallee(Class<?> clazz, Method m)
    {
        return clazz.getName() + "." + m.getName();
    }

    private static String extractCallerDep()
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String caller = "unknown";
        for (int i = 0; i < stackTrace.length; i++)
        {
            if (stackTrace[i].getClassName().equals( LogInterceptor.class.getName() )
                    && stackTrace[i].getMethodName().equals( "intercept" ) && (i + 2) < stackTrace.length)
            {
                if (stackTrace[i + 2].getMethodName().startsWith( "invoke" ))
                {
                    System.out.println( "stop" );
                }
                caller = stackTrace[i + 2].getClassName() + "." + stackTrace[i + 2].getMethodName();
                break;
            }
        }
        return caller;
    }

    private static String extractCaller()
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String caller = "unknown.unknown";
        if (stackTrace.length > 5)
        {
            caller = stackTrace[5].getClassName() + "." + stackTrace[5].getMethodName();
        }

        return caller;
    }
}
