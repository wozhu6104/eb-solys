/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.util;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.script.InjectedParamsCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;

public class GlobalScriptExecutionHelper
{

    public static void toggleExecution(final RaceScriptInfo raceScript, String globalMethod,
            InjectedParamsCallback callback)
    {
        if (raceScript.isRunning())
        {
            RunScriptInteractionUseCase runScriptInteractionUseCase = UseCaseFactoryInstance.get()
                    .makeRunScriptInteractionUseCase();
            runScriptInteractionUseCase.stopScript( raceScript );
        }
        else
        {
            RunScriptInteractionUseCase runScriptInteractionUseCase = UseCaseFactoryInstance.get()
                    .makeRunScriptInteractionUseCase();
            runScriptInteractionUseCase.setInjectedParamsCallback( callback );
            runScriptInteractionUseCase.runScriptWithGlobalMethod( raceScript, globalMethod );
        }
    }

    public static void stopScript(final RaceScriptInfo raceScript)
    {
        RunScriptInteractionUseCase runScriptInteractionUseCase = UseCaseFactoryInstance.get()
                .makeRunScriptInteractionUseCase();
        runScriptInteractionUseCase.stopScript( raceScript );
    }

}
