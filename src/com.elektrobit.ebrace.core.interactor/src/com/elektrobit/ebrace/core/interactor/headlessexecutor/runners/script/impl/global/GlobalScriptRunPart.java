/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.api.ScriptRunPart;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.helper.ScriptMethodFinder;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.google.common.annotations.VisibleForTesting;

public class GlobalScriptRunPart implements ScriptRunPart
{
    private final ScriptMethodFinder genericScriptMethodFinder;
    private final ScriptExecutorService scriptExecutorService;

    public GlobalScriptRunPart(ScriptExecutorService scriptExecutorService)
    {
        this( scriptExecutorService, new ScriptMethodFinder() );
    }

    @VisibleForTesting
    public GlobalScriptRunPart(ScriptExecutorService scriptExecutorService, ScriptMethodFinder scriptMethodFinderImpl)
    {
        this.scriptExecutorService = scriptExecutorService;
        this.genericScriptMethodFinder = scriptMethodFinderImpl;
    }

    @Override
    public RaceScriptMethod extractScriptMethod(RaceScriptInfo script, String methodName)
    {
        return genericScriptMethodFinder.extractScriptMethod( script.getGlobalMethods(), methodName );
    }

    @Override
    public void runScript(RaceScriptInfo script, String methodName)
    {
        scriptExecutorService.runScriptWithGlobalMethod( script, methodName );
    }

    @Override
    public void stopScript(RaceScriptInfo script)
    {
        scriptExecutorService.stopScript( script );
    }
}
