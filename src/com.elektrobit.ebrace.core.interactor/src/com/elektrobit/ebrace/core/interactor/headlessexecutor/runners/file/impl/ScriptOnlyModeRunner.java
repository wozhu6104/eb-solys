/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.api.AutomationModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global.GlobalScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.google.common.annotations.VisibleForTesting;

public class ScriptOnlyModeRunner implements AutomationModeRunner
{
    private final GlobalScriptRunner globalScriptRunner;

    public ScriptOnlyModeRunner(ScriptExecutorService scriptExecutorService)
    {
        this( scriptExecutorService, new GlobalScriptRunner( scriptExecutorService ) );
    }

    @VisibleForTesting
    public ScriptOnlyModeRunner(ScriptExecutorService scriptExecutorService, GlobalScriptRunner globalScriptRunner)
    {
        this.globalScriptRunner = globalScriptRunner;
    }

    @Override
    public boolean paramsOk(String pathToFile, RaceScriptInfo script, String methodName)
    {
        return pathToFile.trim().equals( "-" ) && globalScriptRunner.paramsOk( script, methodName );
    }

    @Override
    public boolean run(String pathToFile, RaceScriptInfo script, String methodName)
    {
        return globalScriptRunner.run( script, methodName );
    }

}
