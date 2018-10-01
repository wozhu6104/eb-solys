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

import java.util.Arrays;
import java.util.List;

import com.elektrobit.ebrace.app.racescriptexecutor.caller.RaceScriptCaller;
import com.elektrobit.ebrace.app.racescriptexecutor.helper.AnnotationHelper;
import com.elektrobit.ebrace.app.racescriptexecutor.helper.ExecuteMethodHelper;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;

public class DefaultRaceScriptCaller implements RaceScriptCaller
{
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
        List<RaceScriptMethod> allMethods = AnnotationHelper.getAllMethods( raceScriptInstance, scriptName );
        RaceScriptMethod methodByNameAndParamTypes = ExecuteMethodHelper
                .getMethodByNameAndParamTypes( executeMethod, allMethods, Arrays.asList( params ) );
        ExecuteMethodHelper.executeMethod( scriptName, methodByNameAndParamTypes, raceScriptInstance, params );
    }

    @Override
    public void callAfterMethod(String scriptName, Object raceScriptInstance)
    {
        List<RaceScriptMethod> afterRaceScriptMethods = AnnotationHelper.getAfterMethods( raceScriptInstance,
                                                                                          scriptName );
        ExecuteMethodHelper.executeFirstMethod( scriptName, afterRaceScriptMethods, raceScriptInstance );
    }
}
