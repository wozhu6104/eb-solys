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
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.google.common.annotations.VisibleForTesting;

public class FileGlobalModeRunner implements AutomationModeRunner
{
    private final GlobalScriptRunner globalScriptRunner;
    private final FileLoadRunner fileLoadRunner;

    public FileGlobalModeRunner(LoadFileService loadFileService, ScriptExecutorService scriptExecutorService)
    {
        this( new FileLoadRunner( loadFileService ), new GlobalScriptRunner( scriptExecutorService ) );
    }

    @VisibleForTesting
    public FileGlobalModeRunner(FileLoadRunner fileLoadRunner, GlobalScriptRunner globalScriptRunner)
    {
        this.fileLoadRunner = fileLoadRunner;
        this.globalScriptRunner = globalScriptRunner;
    }

    @Override
    public boolean paramsOk(String pathToFile, RaceScriptInfo script, String methodName)
    {
        return fileLoadRunner.paramsOk( pathToFile ) && globalScriptRunner.paramsOk( script, methodName );
    }

    @Override
    public boolean run(String pathToFile, RaceScriptInfo script, String methodName)
    {
        return fileLoadRunner.run( pathToFile ) && globalScriptRunner.run( script, methodName );
    }

}
