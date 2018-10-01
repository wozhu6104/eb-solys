/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.helper;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;

public class ScriptMethodFinder
{
    public RaceScriptMethod extractScriptMethod(List<RaceScriptMethod> methods, String methodName)
    {
        if (methodName.isEmpty() && methods.size() >= 1)
        {
            return methods.get( 0 );
        }
        else
        {
            for (RaceScriptMethod nextScriptMethod : methods)
            {
                if (nextScriptMethod.getMethodName().equals( methodName ))
                {
                    return nextScriptMethod;
                }
            }
        }

        return null;
    }
}
