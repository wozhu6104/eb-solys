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

import java.lang.management.ManagementFactory;
import java.util.List;

public class ScriptDebuggingHelper
{
    static final String DEBUG_OPTION_PARAM = "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005";

    private static List<String> VMARGS = ManagementFactory.getRuntimeMXBean().getInputArguments();
    private static boolean DEBUG_PARAM_AVAILABLE = VMARGS.stream().filter( arg -> arg.equals( DEBUG_OPTION_PARAM ) )
            .count() >= 1;

    public static boolean isDebugOptionInIni()
    {
        return DEBUG_PARAM_AVAILABLE;
    }
}
