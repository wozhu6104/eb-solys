/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptimporter.impl.project;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.elektrobit.ebrace.dev.debug.annotations.api.EnterExitPrinter;
import com.elektrobit.ebrace.dev.debug.annotations.api.InterceptMethod;

public class ScriptProjectBuilderImpl
{

    private final CopyFileToProjectHelper copyFileToProjectHelper;

    public ScriptProjectBuilderImpl(CopyFileToProjectHelper copyFileToProjectHelper)
    {
        this.copyFileToProjectHelper = copyFileToProjectHelper;
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    public void addUserScripts(List<File> userScripts)
    {
        for (File nextUserScript : userScripts)
        {
            copyScriptToProject( nextUserScript, true );
        }
    }

    private IFile copyScriptToProject(File sourceXtendScript, boolean userScript)
    {
        IFile scriptInProject = null;
        if (userScript)
        {
            scriptInProject = copyFileToProjectHelper.copyUserScriptToProject( sourceXtendScript );
        }
        else
        {
            scriptInProject = copyFileToProjectHelper.copyPreinstalledScriptToProject( sourceXtendScript );
            if (scriptInProject != null)
            {
                copyFileToProjectHelper.makeScriptFileReadOnly( scriptInProject );
            }
        }
        return scriptInProject;
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    public void addPreinstalledScripts(List<File> userScripts)
    {
        for (File nextUserScript : userScripts)
        {
            copyScriptToProject( nextUserScript, false );
        }
    }

}
