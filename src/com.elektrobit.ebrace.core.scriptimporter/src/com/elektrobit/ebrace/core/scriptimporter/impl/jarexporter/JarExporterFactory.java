/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptimporter.impl.jarexporter;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;

public class JarExporterFactory
{
    public static JarExporter create(IProject project)
    {
        String srcFolder = project.getFolder( ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME ).getLocation()
                .toOSString();
        String binFolder = project.getFolder( ScriptConstants.BIN_FOLDER ).getLocation().toOSString();

        return new JarExporter( srcFolder,
                                binFolder,
                                createParallelToWorkspaceFolder( "scripts" ),
                                createParallelToWorkspaceFolder( "tmp-bin" ) );
    }

    private static String createParallelToWorkspaceFolder(String name)
    {
        String path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        String scriptsFolder = new File( path + "/../" + name ).toString();
        return scriptsFolder;
    }

    public static JarExporter create(String scriptFolderPath)
    {
        String srcFolder = scriptFolderPath + "/" + ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME;
        String binFolder = scriptFolderPath + "/" + ScriptConstants.BIN_FOLDER;

        return new JarExporter( srcFolder,
                                binFolder,
                                createParallelToWorkspaceFolder( "scripts" ),
                                createParallelToWorkspaceFolder( "tmp-bin" ) );
    }
}
