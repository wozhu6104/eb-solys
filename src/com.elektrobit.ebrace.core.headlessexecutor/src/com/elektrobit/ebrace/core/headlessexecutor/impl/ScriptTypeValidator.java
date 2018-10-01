/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.headlessexecutor.impl;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;

public class ScriptTypeValidator
{
    private final RaceScriptInfo script;

    public ScriptTypeValidator(final RaceScriptInfo script)
    {
        this.script = script;
    }

    public boolean isMethodCallback(final String methodName)
    {
        if (script.getName() != null)
        {
            List<RaceScriptMethod> ListMethods = script.getCallbackMethods();
            for (RaceScriptMethod method : ListMethods)
            {
                if (method.getMethodName().equals( methodName ))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOnlyExecutableMethodCallback()
    {
        if (script.getName() != null)
        {
            if (script.numberExecutableMethods() == 1 && script.getCallbackMethods().size() == 1)
            {
                return true;
            }
        }
        return false;
    }

    public boolean scriptContainsMoreCallbackMethdos()
    {
        if (script.getCallbackMethods().size() != 1)
        {
            return true;
        }
        return false;
    }

    public boolean isMethodGlobal(final String methodName)
    {
        if (script.getName() != null)
        {
            List<RaceScriptMethod> globalMethods = script.getGlobalMethods();
            for (RaceScriptMethod method : globalMethods)
            {
                if (method.getMethodName().equals( methodName ))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOnlyExecutableMethodGlobal()
    {
        if (script.getName() != null)
        {
            if (script.numberExecutableMethods() == 1 && script.getGlobalMethods().size() == 1)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isMethodPreselection(final String methodName)
    {
        if (script.getName() != null)
        {
            if (isMethodPreselectionContext( methodName ))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodPreselectionContext(final String methodName)
    {
        if (script.getChannelMethods().contains( methodName )
                || script.getRuntimeEventListMethods().contains( methodName )
                || script.getTimeMarkerMethods().contains( methodName ))
        {
            return true;
        }
        return false;
    }

    public boolean scriptContainsOnlyPreselectionMethods()
    {
        if (script.getName() != null)
        {
            if (preselectionMethodsNumber() != script.numberExecutableMethods())
            {
                return false;
            }
        }
        return true;
    }

    private int preselectionMethodsNumber()
    {
        return script.getChannelMethods().size() + script.getTimeMarkerMethods().size()
                + script.getRuntimeEventListMethods().size();
    }

    public boolean isCallbackModeActive(final String methodName)
    {
        boolean isMethodCallback = isMethodCallback( methodName );
        boolean isOnlyExecutableMethodCallback = isOnlyExecutableMethodCallback();

        if (isMethodCallback || (isOnlyExecutableMethodCallback && methodName == null))
        {
            return true;
        }
        return false;
    }

    public boolean isGlobalModeActive(final String methodName)
    {
        if (isMethodGlobal( methodName ) || (isOnlyExecutableMethodGlobal() && methodName == null))
        {
            return true;
        }
        return false;
    }

    public boolean isPreselectionModeActive(final String methodName)
    {
        if (isMethodPreselection( methodName ) || scriptContainsOnlyPreselectionMethods())
        {
            return true;
        }
        return false;
    }
}
