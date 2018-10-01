/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.caller.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.app.racescriptexecutor.caller.RaceScriptCaller;
import com.elektrobit.ebrace.app.racescriptexecutor.helper.AnnotationHelper;
import com.elektrobit.ebrace.app.racescriptexecutor.helper.ExecuteMethodHelper;
import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.racescriptexecutor.api.Constants;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventLoggerClient;
import com.elektrobit.ebsolys.script.external.Execute;

public class CallbackScriptCaller implements RaceScriptCaller, RuntimeEventLoggerClient
{

    private final ServiceRegistration<?> runtimeEventLoggerClientService;
    private List<RuntimeEvent<?>> readCache;
    private List<RuntimeEvent<?>> writeCache = new ArrayList<RuntimeEvent<?>>();
    private final Object syncObject = new Object();

    public CallbackScriptCaller()
    {
        runtimeEventLoggerClientService = Platform.getBundle( Constants.PLUGIN_ID ).getBundleContext()
                .registerService( RuntimeEventLoggerClient.class.getName(), this, null );
    }

    @Override
    public void callBeforeMethod(String scriptName, Object raceScriptInstance) throws RuntimeException
    {
        List<RaceScriptMethod> beforeRaceScriptMethods = AnnotationHelper.getBeforeMethods( raceScriptInstance,
                                                                                            scriptName );
        ExecuteMethodHelper.executeFirstMethod( scriptName, beforeRaceScriptMethods, raceScriptInstance );
    }

    @Override
    public void callScript(String scriptName, Object raceScriptInstance, String executeMethod, Object... params)
            throws RuntimeException
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "raceScriptInstance", raceScriptInstance );
        List<RaceScriptMethod> allMethods = AnnotationHelper.getAllMethods( raceScriptInstance, scriptName );
        RaceScriptMethod callbackMethodToExecute = ExecuteMethodHelper
                .getMethodByNameAndParamTypes( executeMethod, allMethods, Arrays.asList( params ) );

        int timer = getCallbackTime( callbackMethodToExecute.getMethod() );
        while (true)
        {
            if (runtimeEventsWaitingToBePosted())
            {
                switchReadAndWriteCache();
                ExecuteMethodHelper.executeMethod( scriptName, callbackMethodToExecute, raceScriptInstance, readCache );
                readCache = null;
            }

            try
            {
                Thread.sleep( timer );
            }
            catch (InterruptedException e)
            {

            }
        }
    }

    private int getCallbackTime(Method callBackMethod)
    {
        for (Annotation annotation : callBackMethod.getAnnotations())
        {
            if (annotation instanceof Execute)
            {
                Execute executeAnnotation = (Execute)annotation;
                return executeAnnotation.time().getTime();
            }
        }
        throw new IllegalArgumentException( "Wrong annotation for called method" );
    }

    public boolean runtimeEventsWaitingToBePosted()
    {
        synchronized (syncObject)
        {
            return !writeCache.isEmpty();
        }
    }

    private void switchReadAndWriteCache()
    {
        synchronized (syncObject)
        {
            readCache = writeCache;
            writeCache = new ArrayList<RuntimeEvent<?>>();
        }
    }

    @Override
    public void runtimeEventOccured(RuntimeEvent<?> occuredRuntimeEvent)
    {
        synchronized (syncObject)
        {
            writeCache.add( occuredRuntimeEvent );
        }
    }

    public void dispose()
    {
        runtimeEventLoggerClientService.unregister();
    }

    @Override
    public void callAfterMethod(String scriptName, Object raceScriptInstance)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "raceScriptInstance", raceScriptInstance );
        List<RaceScriptMethod> afterRaceScriptMethods = AnnotationHelper.getAfterMethods( raceScriptInstance,
                                                                                          scriptName );
        ExecuteMethodHelper.executeFirstMethod( scriptName, afterRaceScriptMethods, raceScriptInstance );
    }
}
