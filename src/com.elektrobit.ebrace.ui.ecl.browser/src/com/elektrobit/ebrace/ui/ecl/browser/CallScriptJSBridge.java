/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.browser;

import java.util.List;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class CallScriptJSBridge
{

    private final ResourcesModelManager resourcesManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
            .getService();

    public CallScriptJSBridge()
    {
    }

    public void startScript(String scriptName, String methodName)
    {
        RaceScriptResourceModel scriptModel = obtainScriptModel( scriptName, methodName );
        RunScriptInteractionUseCase runScriptUseCase = UseCaseFactoryInstance.get().makeRunScriptInteractionUseCase();
        runScriptUseCase.runScriptWithGlobalMethod( scriptModel.getScriptInfo(), methodName );
    }

    public void startScript(String scriptName, String methodName, Object... params)
    {
        RaceScriptResourceModel scriptModel = obtainScriptModel( scriptName, methodName );
        RunScriptInteractionUseCase runScriptUseCase = UseCaseFactoryInstance.get().makeRunScriptInteractionUseCase();
        runScriptUseCase.runScriptWithGlobalMethodAndParams( scriptModel.getScriptInfo(), methodName, params );
    }

    private RaceScriptResourceModel getLoadedScriptObject(String scriptName)
    {
        List<RaceScriptResourceModel> scripts = resourcesManager.getAllScripts();
        for (RaceScriptResourceModel script : scripts)
        {
            if (script.getName().equals( scriptName ))
            {
                return script;
            }
        }
        return null;
    }

    private RaceScriptResourceModel obtainScriptModel(String scriptName, String methodName)
    {

        RaceScriptResourceModel scriptModel = getLoadedScriptObject( scriptName );
        if (scriptModel != null)
        {
            List<RaceScriptMethod> globalMethods = scriptModel.getScriptInfo().getGlobalMethods();
            for (RaceScriptMethod method : globalMethods)
            {
                if (method.getMethodName().equals( methodName ))
                {
                    return scriptModel;
                }
            }
            return null;
        }
        else
        {
            return null;
        }
    }

}
