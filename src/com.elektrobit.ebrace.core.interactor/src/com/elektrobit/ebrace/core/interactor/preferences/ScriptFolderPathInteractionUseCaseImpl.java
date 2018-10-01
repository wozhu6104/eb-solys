/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.preferences;

import java.io.File;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathInteractionUseCase;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;

public class ScriptFolderPathInteractionUseCaseImpl implements ScriptFolderPathInteractionUseCase
{
    private final PreferencesService preferencesService;

    public ScriptFolderPathInteractionUseCaseImpl(PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
    }

    @Override
    public void setScriptFolderPath(String path)
    {
        if (!isScriptFolderPathValid( path ))
        {
            throw new IllegalArgumentException( "Path is invalid, check it with isScriptFolderPathValid(String path) method first." );
        }

        preferencesService.setScriptFolderPath( path );
    }

    @Override
    public boolean isScriptFolderPathValid(String path)
    {
        File file = new File( path );
        return file.exists() && file.isDirectory();
    }

    @Override
    public void setScriptFolderPathToDefault()
    {
        preferencesService.setScriptFolderPathToDefault();
    }

    @Override
    public String getScriptFolderDefaultPath()
    {
        return preferencesService.getDefaultScriptFolderPath();
    }
}
