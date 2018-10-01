/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.usestatlogsannotationloader.api;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class UseStatLogHandlerCaller
{

    private final static UseStatLogHandlerRegistry USE_STAT_LOG_HANDLER_REGISTRY = new UseStatLogHandlerRegistry();

    @RuntimeType
    public static Object intercept(@SuperCall Callable<Object> zuper, @Origin Class<?> clazz, @Origin Method method,
            @AllArguments Object[] args)
    {

        Object result = null;
        UseStatLog useStatLogAnnotation = method.getAnnotation( UseStatLog.class );

        try
        {
            logToRegisteredUseStatLogHandler( args, useStatLogAnnotation );
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
        }

        return result;
    }

    private static void logToRegisteredUseStatLogHandler(Object[] args, UseStatLog useStatLogAnnotation)
            throws InstantiationException, IllegalAccessException
    {
        List<UseStatLogHandler> logHandler = USE_STAT_LOG_HANDLER_REGISTRY.getUseStatLogHandler();

        if (logHandler.isEmpty())
        {
            System.out.println( "INFO: No UsageStatsLogger available, but message is was logged." );
        }

        for (UseStatLogHandler nextLogHandler : logHandler)
        {
            String logText = createJsonString( "type", useStatLogAnnotation.value() );
            if (isParamParserAvailable( useStatLogAnnotation ))
            {
                UseStatLogParamParser parser = useStatLogAnnotation.parser().newInstance();
                String parseResult = parser.parse( args );
                logText += "," + createJsonSubString( "params", parseResult );
            }
            nextLogHandler.log( "{" + logText + "}" );
        }
    }

    private static String createJsonString(String key, String value)
    {
        return "\"" + key + "\"" + ":" + "\"" + value + "\"";
    }

    private static String createJsonSubString(String key, String value)
    {
        return "\"" + key + "\"" + ":" + value;
    }

    private static boolean isParamParserAvailable(UseStatLog useStatLogAnnotation)
    {
        return !useStatLogAnnotation.parser().equals( UseStatLogParamParser.class );
    }

}
