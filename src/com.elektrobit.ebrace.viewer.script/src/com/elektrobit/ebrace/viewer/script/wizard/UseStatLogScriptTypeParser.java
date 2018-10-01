/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.wizard;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogParamParser;
import com.elektrobit.ebrace.viewer.script.wizard.ScriptSourceGenerator.ScriptContext;

public class UseStatLogScriptTypeParser implements UseStatLogParamParser
{

    @Override
    public String parse(Object[] args)
    {
        if (args.length == 4 && args[1] instanceof ScriptContext)
        {
            return ((ScriptContext)args[1]).name();
        }
        return null;
    }

}
