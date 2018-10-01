/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elektrobit.ebrace.app.racescriptexecutor.helper.AnnotationHelper;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.scriptannotation.impl.InjectedParam;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.script.external.BeforeScript;
import com.elektrobit.ebsolys.script.external.Execute;
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext;
import com.elektrobit.ebsolys.script.external.Filter;

public class RaceScriptMethodFilter
{
    public static List<RaceScriptMethod> getAllMethods(Class<?> raceScriptClass, String scriptName)
    {
        List<Method> allMethods = new ArrayList<Method>( Arrays.asList( raceScriptClass.getDeclaredMethods() ) );
        List<RaceScriptMethod> scriptMethods = AnnotationHelper.createRaceMethodObjects( allMethods, scriptName );
        return scriptMethods;
    }

    public static List<RaceScriptMethod> filterGlobalExecutionMethods(List<RaceScriptMethod> methods)
    {
        List<RaceScriptMethod> foundMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod method : getMethodsForAnnotationClass( methods, Execute.class ))
        {
            for (Annotation a : method.getMethod().getAnnotations())
            {
                if (a instanceof Execute)
                {
                    if (ExecutionContext.GLOBAL.equals( ((Execute)a).context() ))
                    {
                        foundMethods.add( method );
                    }
                }
            }
        }
        methods.removeAll( foundMethods );
        return foundMethods;
    }

    /**
     * Returns a list of methods that are annotated with {@link BeforeScript}. These methods will be removed from passed
     * list.
     * 
     * @param methods
     *            Methods to be filtered
     * @param annotationClass
     *            Annotation class that methods will be filtered for
     * @return List of methods that are annotated with {@link BeforeScript}
     */
    public static List<RaceScriptMethod> filterMethodsWithAnnotationClass(List<RaceScriptMethod> methods,
            Class<? extends Annotation> annotationClass)
    {
        List<RaceScriptMethod> foundMethods = getMethodsForAnnotationClass( methods, annotationClass );
        methods.removeAll( foundMethods );
        return foundMethods;
    }

    public static List<RaceScriptMethod> getMethodsForAnnotationClass(List<RaceScriptMethod> methods,
            Class<? extends Annotation> annotationClass)
    {
        List<RaceScriptMethod> foundMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : methods)
        {
            Annotation[] annotations = m.getMethod().getAnnotations();
            for (Annotation a : annotations)
            {
                if (a.annotationType().equals( annotationClass ))
                {
                    foundMethods.add( m );
                }
            }
        }
        return foundMethods;
    }

    /**
     * Returns a list of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     * type {@link TimeMarker} These methods will be removed from passed list.
     * 
     * @param methods
     *            Methods to be filtered
     * @return List of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     *         type {@link TimeMarker}
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static List<RaceScriptMethod> filterPreselectionMethodsWithTimeMarker(List<RaceScriptMethod> methods)

    {
        List<RaceScriptMethod> timeMarkerMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : new ArrayList<RaceScriptMethod>( methods ))
        {
            if (isTimeMarkerMethod( m.getMethod() ))
            {
                timeMarkerMethods.add( m );
                methods.remove( m );
            }
        }
        return timeMarkerMethods;
    }

    private static boolean isTimeMarkerMethod(Method m)
    {
        return isPreselectionMethodForType( m, TimeMarker.class, null );
    }

    private static boolean isPreselectionMethodForType(Method m, Class<?> requestedClass, Class<?> listGenericType)

    {
        for (Annotation annotation : m.getAnnotations())
        {
            if (annotation instanceof Execute)
            {

                Execute executeAnnotation = (Execute)annotation;
                if (ExecutionContext.PRESELECTION.equals( executeAnnotation.context() ))
                {
                    Class<?>[] paramTypes = m.getParameterTypes();
                    Type[] genericParamType = m.getGenericParameterTypes();

                    if (listGenericType == null)
                    {
                        if (paramTypes.length == 1 && paramTypes[0] == requestedClass)
                        {
                            return true;
                        }
                    }
                    else
                    {
                        if (paramTypes.length == 1)
                        {

                            for (Type typ : genericParamType)
                            {
                                if (typ instanceof ParameterizedType)
                                {
                                    ParameterizedType pType = (ParameterizedType)typ;
                                    Type[] arrType = pType.getActualTypeArguments();

                                    for (Type tp : arrType)
                                    {
                                        if (tp.toString().contains( listGenericType.getName() ))
                                        {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a list of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     * type {@link RuntimeEventChannel}. These methods will be removed from passed list.
     * 
     * @param methods
     *            Methods to be filtered
     * @return List of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     *         type {@link RuntimeEventChannel}
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static List<RaceScriptMethod> filterPreselectionMethodsWithChannel(List<RaceScriptMethod> methods)
            throws NoSuchFieldException, SecurityException

    {
        List<RaceScriptMethod> channelMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : new ArrayList<RaceScriptMethod>( methods ))
        {
            if (isChannelMethod( m.getMethod() ))
            {
                channelMethods.add( m );
                methods.remove( m );
            }
        }
        return channelMethods;
    }

    private static boolean isChannelMethod(Method m)
    {
        return isPreselectionMethodForType( m, RuntimeEventChannel.class, null );
    }

    /**
     * Returns a list of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     * type {@link List<RuntimeEvent<?>>} These methods will be removed from passed list.
     * 
     * @param methods
     *            Methods to be filtered
     * @return List of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     *         type {@link List<RuntimeEvent<?>>}
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static List<RaceScriptMethod> filterPreselectionMethodsWithRuntimeEvents(List<RaceScriptMethod> methods)
            throws NoSuchFieldException, SecurityException
    {
        List<RaceScriptMethod> eventListMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : new ArrayList<RaceScriptMethod>( methods ))
        {
            if (isRuntimeEventListMethod( m.getMethod() ))
            {
                eventListMethods.add( m );
                methods.remove( m );
            }
        }
        return eventListMethods;
    }

    /**
     * Returns a list of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     * type {@link <RuntimeEvent<?>} These methods will be removed from passed list.
     * 
     * @param methods
     *            Methods to be filtered
     * @return List of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     *         type {@link RuntimeEvent<?>}
     */
    public static List<RaceScriptMethod> filterPreselectionMethodsWithRuntimeEvent(List<RaceScriptMethod> methods)
            throws NoSuchFieldException, SecurityException
    {
        List<RaceScriptMethod> eventListMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : new ArrayList<RaceScriptMethod>( methods ))
        {
            if (isRuntimeEventMethod( m.getMethod() ))
            {
                eventListMethods.add( m );
                methods.remove( m );
            }
        }
        return eventListMethods;
    }

    private static boolean isRuntimeEventMethod(Method m)
    {
        return isPreselectionMethodForType( m, RuntimeEvent.class, null );
    }

    /**
     * Returns a list of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     * type {@link List<RuntimeEventChannel<?>>} These methods will be removed from passed list.
     * 
     * @param methods
     *            Methods to be filtered
     * @return List of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     *         type {@link List<RuntimeEventChannel<?>>}
     * @throws SecurityException
     * @throws NoSuchFieldException
     */

    public static List<RaceScriptMethod> filterPreselectionMethodsWithListOfChannels(List<RaceScriptMethod> methods)

    {
        List<RaceScriptMethod> eventListMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : new ArrayList<RaceScriptMethod>( methods ))
        {
            if (isRuntimeEvenChanneltListMethod( m.getMethod() ))
            {
                eventListMethods.add( m );
                methods.remove( m );
            }
        }
        return eventListMethods;
    }

    /**
     * Returns a list of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     * type {@link List<TimeMarker>} These methods will be removed from passed list.
     * 
     * @param methods
     *            Methods to be filtered
     * @return List of methods that are annotated with {@link ExecutionContext.PRESELECTION} and have one parameter of
     *         type {@link List<Timemarker>}
     */
    public static List<RaceScriptMethod> filterPreselectionMethodsWithListOfTimeMarker(List<RaceScriptMethod> methods)
    {
        List<RaceScriptMethod> eventListMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod m : new ArrayList<RaceScriptMethod>( methods ))
        {
            if (isTimeMarkerListMethod( m.getMethod() ))
            {
                eventListMethods.add( m );
                methods.remove( m );
            }
        }
        return eventListMethods;
    }

    private static boolean isRuntimeEventListMethod(Method m)
    {
        return isPreselectionMethodForType( m, List.class, RuntimeEvent.class );
    }

    private static boolean isRuntimeEvenChanneltListMethod(Method m)
    {
        return isPreselectionMethodForType( m, List.class, RuntimeEventChannel.class );
    }

    private static boolean isTimeMarkerListMethod(Method m)
    {
        return isPreselectionMethodForType( m, List.class, TimeMarker.class );
    }

    public static List<RaceScriptMethod> filterCallbackMethod(List<RaceScriptMethod> methods)
    {
        List<RaceScriptMethod> foundMethods = new ArrayList<RaceScriptMethod>();
        for (RaceScriptMethod method : getMethodsForAnnotationClass( methods, Execute.class ))
        {
            for (Annotation annotation : method.getMethod().getAnnotations())
            {
                if (annotation instanceof Execute)
                {
                    Execute executeAnnotation = (Execute)annotation;
                    if (ExecutionContext.CALLBACK.equals( executeAnnotation.context() ))
                    {
                        Class<?>[] paramTypes = method.getMethod().getParameterTypes();
                        if (method.getMethod().getParameterTypes().length == 1 && paramTypes[0] == List.class)
                        {
                            foundMethods.add( method );
                        }
                    }
                }
            }
        }
        methods.removeAll( foundMethods );
        return foundMethods;
    }

    public static List<RaceScriptMethod> filterFilterMethods(List<RaceScriptMethod> methods)
    {
        List<RaceScriptMethod> foundMethods = new ArrayList<RaceScriptMethod>();

        for (RaceScriptMethod method : methods)
        {
            for (Annotation annotation : method.getMethod().getAnnotations())
            {
                if (annotation instanceof Filter)
                {
                    Class<?>[] paramTypes = method.getMethod().getParameterTypes();
                    boolean parameterOk = paramTypes.length == 1 && paramTypes[0] == RuntimeEvent.class;

                    boolean returnTypeBooleanObject = Boolean.class
                            .isAssignableFrom( method.getMethod().getReturnType() );

                    boolean returnTypeBooleanPrimitive = boolean.class
                            .isAssignableFrom( method.getMethod().getReturnType() );

                    boolean returnTypeOk = returnTypeBooleanObject || returnTypeBooleanPrimitive;
                    if (parameterOk && returnTypeOk)
                    {
                        foundMethods.add( method );
                    }
                }
            }
        }
        methods.removeAll( foundMethods );
        return foundMethods;
    }

    public static List<String> collectInjectedParamNames(Class<?> raceScriptClass)
    {
        List<String> injectedParamFields = new ArrayList<>();
        Field[] declaredFields = raceScriptClass.getDeclaredFields();
        for (Field field : declaredFields)
        {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof InjectedParam)
                {
                    injectedParamFields.add( field.getName() );
                }
            }
        }
        return injectedParamFields;
    }
}
