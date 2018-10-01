/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.script;

import java.io.File;

import lombok.Data;

@Data
public class ScriptData
{
    private final String name;
    private final String sourcePath;
    private final String jarPath;

    public boolean isPreinstalledScript()
    {
        if (sourcePath == null)
        {
            return false;
        }
        return new File( sourcePath ).getParentFile().getName().toLowerCase()
                .equals( ScriptConstants.PREINSTALLED_SCRIPT_SOURCE_FOLDER_NAME );
    }

    public boolean isUserScript()
    {
        return !isPreinstalledScript();
    }
}
