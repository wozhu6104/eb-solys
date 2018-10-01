/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.scriptconsole.impl;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.core.scriptconsolefactory.api.ScriptConsoleFactoryService;
import com.elektrobit.ebsolys.script.external.Console;

@Component(immediate = true)
public class AutomationScriptConsoleFactoryService implements ScriptConsoleFactoryService
{

    @Override
    public Console createNewConsole(String name)
    {
        return new AutomationScriptConsole();
    }

}
