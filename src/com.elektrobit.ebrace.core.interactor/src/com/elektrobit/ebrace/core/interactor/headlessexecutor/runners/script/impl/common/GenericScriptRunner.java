/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.common;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.api.ScriptRunPart;

public class GenericScriptRunner
{
    private final ScriptRunPart scriptRunPart;

    public GenericScriptRunner(ScriptRunPart scriptRunPart)
    {
        this.scriptRunPart = scriptRunPart;
    }

    public boolean paramsOk(RaceScriptInfo script, String methodName)
    {
        final RaceScriptMethod method = scriptRunPart.extractScriptMethod( script, methodName );
        final boolean result = (method != null);
        return result;
    }

    public boolean run(RaceScriptInfo script, String methodName)
    {
        boolean result = true;
        final RaceScriptMethod method = scriptRunPart.extractScriptMethod( script, methodName );

        if (method == null)
        {
            result &= false;
        }
        else
        {
            scriptRunPart.runScript( script, method.getMethodName() );
        }

        return result;
    }

    public void stop(RaceScriptInfo script)
    {
        scriptRunPart.stopScript( script );
    }

}
