/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.racescriptexecutor.api;

import java.io.File;

import org.eclipse.core.runtime.Platform;

public interface Constants
{
    String DEFAULT_PATH_TO_SCRIPT_FOLDER = Platform.getLocation().toOSString() + File.separator + ".." + File.separator
            + "scripts";
    String RACE_SCRIPT_EXTENTION = ".jar";
    String PLUGIN_ID = "com.elektrobit.ebrace.core.racescriptexecutor";
}
