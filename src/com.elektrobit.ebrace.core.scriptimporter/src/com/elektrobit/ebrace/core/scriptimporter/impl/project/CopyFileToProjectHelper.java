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
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;

import lombok.extern.log4j.Log4j;

@Log4j
public class CopyFileToProjectHelper
{

    private final ScriptProjectImporter scriptProjectManager;

    public CopyFileToProjectHelper(ScriptProjectImporter scriptProjectManager)
    {
        this.scriptProjectManager = scriptProjectManager;
    }

    public IFile copyUserScriptToProject(File sourceFilePath)
    {
        return copyScriptToProject( sourceFilePath, ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME );
    }

    private IFile copyScriptToProject(File sourceFilePath, String pathInProject)
    {
        checkIfXtendFile( sourceFilePath );

        IFile raceScriptXtendFile = scriptProjectManager.importProject().getFolder( pathInProject )
                .getFile( sourceFilePath.getName() );

        if (!raceScriptXtendFile.exists())
        {
            try (FileInputStream fileInputStream = new FileInputStream( sourceFilePath ))
            {
                raceScriptXtendFile.create( fileInputStream, true, new NullProgressMonitor() );
            }
            catch (IOException | CoreException e)
            {
                log.error( "Couldn't copy script " + sourceFilePath + " to script project. Cause: " + e );
                raceScriptXtendFile = null;
            }
        }

        return raceScriptXtendFile;

    }

    public IFile copyPreinstalledScriptToProject(File sourceFilePath)
    {
        return copyScriptToProject( sourceFilePath,
                                    ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME + "/"
                                            + ScriptConstants.PREINSTALLED_SCRIPT_SOURCE_FOLDER_NAME );
    }

    public boolean makeScriptFileReadOnly(IFile raceScriptXtendFile)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "raceScriptXtendFile", raceScriptXtendFile );

        IPath location = raceScriptXtendFile.getLocation();
        if (location != null)
        {
            IPath absoluteLocation = location.makeAbsolute();
            File absoluteFile = absoluteLocation.toFile();
            return absoluteFile.setReadOnly();
        }
        else
        {
            log.error( "Couldn't set script " + raceScriptXtendFile.getName()
                    + " read-only, because location cannot be found!" );
        }

        return false;
    }

    private static void checkIfXtendFile(File sourceFilePath)
    {
        if (!sourceFilePath.getName().endsWith( ".xtend" ))
        {
            throw new IllegalArgumentException( "Expecting a xtend file as script file." );
        }
    }
}
