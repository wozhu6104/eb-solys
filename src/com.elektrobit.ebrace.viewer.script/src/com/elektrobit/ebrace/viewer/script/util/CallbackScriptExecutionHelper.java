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
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;

public class CallbackScriptExecutionHelper
{
    public static void toggleExecution(final RaceScriptInfo raceScript, String callbackMethod)
    {
        if (raceScript.isRunning())
        {
            RunScriptInteractionUseCase runScriptUseCase = UseCaseFactoryInstance.get()
                    .makeRunScriptInteractionUseCase();
            runScriptUseCase.stopScript( raceScript );
        }
        else
        {
            RunScriptInteractionUseCase runScriptUseCase = UseCaseFactoryInstance.get()
                    .makeRunScriptInteractionUseCase();

            runScriptUseCase.runCallbackScript( raceScript, callbackMethod );
        }
    }
}
