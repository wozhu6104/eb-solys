/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;

public class ExecuteMethodHelper
{
    private final static Logger LOG = Logger.getLogger( ExecuteMethodHelper.class );

    public static void executeFirstMethod(String scriptName, List<RaceScriptMethod> methods, Object calleeObject,
            Object... params) throws RuntimeException
    {
        if (!methods.isEmpty())
        {
            executeMethod( scriptName, methods.get( 0 ), calleeObject, params );
        }
    }

    public static Object executeMethod(String scriptName, RaceScriptMethod raceMethod, Object calleeObject,
            Object... params) throws RuntimeException
    {
        Method method = raceMethod.getMethod();
        Object result = null;
        try
        {
            if (params.length == 0)
            {
                result = method.invoke( calleeObject );
            }
            else
            {
                result = method.invoke( calleeObject, params );
            }
            return result;
        }
        catch (IllegalAccessException e)
        {
            LOG.error( "Couldn't call message of RaceScript " + scriptName + ". Method name was " + method.getName()
                    + ". Exception was " + e.getMessage() );
            throw new RuntimeException( method.getName(), e );
        }
        catch (IllegalArgumentException e)
        {
            LOG.error( "Couldn't call message of RaceScript " + scriptName + ". Exception was " + e.getMessage() );
            throw new RuntimeException( method.getName(), e );
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException( method.getName(), e );
        }
    }

    public static RaceScriptMethod getMethodByNameAndParamTypes(String methodName, List<RaceScriptMethod> allMethods,
            List<Object> params)
    {
        for (RaceScriptMethod method : allMethods)
        {
            if (method.getMethodName().equals( methodName ) && fitsMethodToParameters( method.getMethod(), params ))
            {
                return method;
            }
        }
        return null;
    }

    private static boolean fitsMethodToParameters(Method method, List<Object> params)
    {
        if (method.getParameterTypes().length == params.size())
        {
            for (int i = 0; i < method.getParameterTypes().length; i++)
            {
                if (!method.getParameterTypes()[i].isAssignableFrom( params.get( i ).getClass() ))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

}
