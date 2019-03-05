/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.script;

import org.eclipse.core.expressions.PropertyTester;

public class ScriptDebuggingPropertyTester extends PropertyTester
{

    private final boolean debugOptionInIni;

    public ScriptDebuggingPropertyTester()
    {
         debugOptionInIni = ScriptDebuggingHelper.isDebugOptionInIni();
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        return debugOptionInIni;
    }

}
