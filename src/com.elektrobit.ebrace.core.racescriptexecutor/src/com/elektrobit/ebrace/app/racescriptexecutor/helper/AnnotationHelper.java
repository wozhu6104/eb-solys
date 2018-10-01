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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.RaceScriptMethodFilter;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebsolys.script.external.AfterScript;
import com.elektrobit.ebsolys.script.external.BeforeScript;
import com.elektrobit.ebsolys.script.external.Execute;
import com.elektrobit.ebsolys.script.external.Filter;

public class AnnotationHelper
{
    public static List<RaceScriptMethod> getAllMethods(final Object raceScriptInstance, String scriptName)
    {
        List<Method> allMethods = Arrays.asList( raceScriptInstance.getClass().getDeclaredMethods() );
        List<RaceScriptMethod> raceMethodObjects = createRaceMethodObjects( allMethods, scriptName );
        return raceMethodObjects;
    }

    public static List<RaceScriptMethod> getBeforeMethods(final Object raceScriptInstance, String scriptName)
    {
        List<RaceScriptMethod> allMethods = getAllMethods( raceScriptInstance, scriptName );
        List<RaceScriptMethod> beforeMethods = RaceScriptMethodFilter
                .getMethodsForAnnotationClass( allMethods, BeforeScript.class );
        return beforeMethods;
    }

    public static List<RaceScriptMethod> getAfterMethods(final Object raceScriptInstance, String scriptName)
    {
        List<RaceScriptMethod> allMethods = getAllMethods( raceScriptInstance, scriptName );
        List<RaceScriptMethod> afterMethods = RaceScriptMethodFilter.getMethodsForAnnotationClass( allMethods,
                                                                                                   AfterScript.class );
        return afterMethods;
    }

    public static List<RaceScriptMethod> createRaceMethodObjects(List<Method> javaMethods, String scriptName)
    {
        List<RaceScriptMethod> listOfMethods = new ArrayList<RaceScriptMethod>();

        for (Method method : javaMethods)
        {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof Execute)
                {

                    listOfMethods
                            .add( new RaceScriptMethod( method.getName(),
                                                        ((Execute)annotation).description(),
                                                        scriptName,
                                                        method ) );
                }
                else if (annotation instanceof Filter)
                {
                    listOfMethods.add( new RaceScriptMethod( method.getName(),
                                                             ((Filter)annotation).description(),
                                                             scriptName,
                                                             method ) );

                }
                else
                {
                    listOfMethods.add( new RaceScriptMethod( method.getName(), "", scriptName, method ) );
                }
            }
        }
        return listOfMethods;
    }
}
