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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.dev.debug.annotations.api.EnterExitPrinter;
import com.elektrobit.ebrace.dev.debug.annotations.api.InterceptMethod;

import lombok.extern.log4j.Log4j;

@Log4j
public class ScriptProjectImporter
{
    private final String scriptFolderPath;
    private IProject project;
    private IProjectDescription description;

    public ScriptProjectImporter(String scriptFolderPath)
    {
        this.scriptFolderPath = scriptFolderPath;
    }

    public IProject getScriptsProject()
    {
        return project;
    }

    public synchronized IProject importProject()
    {
        if (project == null)
        {
            cleanupProject();

            try
            {
                Path projectDescriptionFile = new Path( scriptFolderPath + "/.project" );
                if (!projectDescriptionFile.toFile().exists())
                {
                    createAndImportCleanProject( new Path( scriptFolderPath ) );
                    setXtendFilesInFolderToReadOnly( scriptFolderPath + File.separator + "src" + File.separator
                            + "api" );
                }

                try
                {
                    updateClasspathFile( new Path( scriptFolderPath ) );
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                importExistingProject( projectDescriptionFile );

                if (project.exists())
                {
                    recreateAndOpenProject();
                }
                else
                {
                    createAndOpenProject();
                }

            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }

        }

        return project;
    }

    private void cleanupProject()
    {
        deleteFolderIfExists( scriptFolderPath + "/" + ScriptConstants.XTENDGEN_FOLDER );
        deleteFolderIfExists( scriptFolderPath + "/" + ScriptConstants.BIN_FOLDER );
    }

    private void deleteFolderIfExists(String folder)
    {
        File xtendFolder = new File( folder );
        if (xtendFolder.exists() && xtendFolder.isDirectory())
        {
            try
            {
                FileUtils.deleteDirectory( xtendFolder );
            }
            catch (IOException e)
            {
                log.warn( "Couldn't clean folder: " + folder + ". Reason was:  " + e.getMessage() );
            }
        }
    }

    private static List<String> getScriptBundleIds()
    {
        List<String> bundelIds = new ArrayList<String>();
        bundelIds.add( "com.elektrobit.ebrace.racescript.interface" );
        bundelIds.add( "org.eclipse.ui.console" );
        bundelIds.add( "com.elektrobit.ebrace.core.targetdata.api" );
        bundelIds.add( "org.eclipse.xtext.xbase.lib" );
        bundelIds.add( "com.elektrobit.ebrace.decoder.common" );
        bundelIds.add( "com.elektrobit.ebrace.dev.kpimeasuring" );
        bundelIds.add( "org.apache.commons.math" );
        bundelIds.add( "org.eclipse.xtend.lib" );
        bundelIds.add( "com.google.collect" );
        bundelIds.add( "com.google.gson" );
        bundelIds.add( "com.elektrobit.ebrace.core.scriptannotation" );
        bundelIds.add( "org.apache.log4j" );
        bundelIds.add( "com.elektrobit.ebrace.core.systemmodel" );

        return bundelIds;
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    private void createAndImportCleanProject(Path scriptFolderPath)
    {
        try
        {
            File scriptsFolder = new File( FileHelper.locateFileInBundle( "com.elektrobit.ebrace.core.scriptimporter",
                                                                          "files/RaceScripts" ) );
            FileUtils.copyDirectory( scriptsFolder, new File( scriptFolderPath + "/" ) );
            FileUtils.moveFile( new File( scriptFolderPath + "/project" ), new File( scriptFolderPath + "/.project" ) );

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setXtendFilesInFolderToReadOnly(String pathString)
    {
        java.nio.file.Path path = Paths.get( pathString );
        try
        {
            Files.newDirectoryStream( path, "*.xtend" ).forEach( (f) -> setFileToReadOnly( f ) );
        }
        catch (IOException e)
        {
            log.warn( "Failed to set script files as read-only" );
        }
    }

    private void setFileToReadOnly(java.nio.file.Path path)
    {
        boolean success = path.toFile().setReadOnly();
        if (!success)
        {
            log.warn( "Failed to set file as read only " + path );
        }
    }

    private void updateClasspathFile(Path scriptFolderPath) throws FileNotFoundException
    {
        String createClasspath = ClasspathFileCreator.createClasspath( getScriptBundleIds() );
        try (PrintWriter writer = new PrintWriter( scriptFolderPath + "/.classpath" ))
        {
            writer.write( createClasspath );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    private void importExistingProject(Path projectDescriptionFile) throws CoreException
    {
        description = ResourcesPlugin.getWorkspace().loadProjectDescription( projectDescriptionFile );
        project = ResourcesPlugin.getWorkspace().getRoot().getProject( description.getName() );
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    private void recreateAndOpenProject() throws CoreException
    {
        project.delete( false, true, new IProgressMonitor()
        {

            @Override
            public void worked(int work)
            {
            }

            @Override
            public void subTask(String name)
            {
            }

            @Override
            public void setTaskName(String name)
            {
            }

            @Override
            public void setCanceled(boolean value)
            {
            }

            @Override
            public boolean isCanceled()
            {
                return false;
            }

            @Override
            public void internalWorked(double work)
            {
            }

            @Override
            public void done()
            {
                createAndOpenProject();
            }

            @Override
            public void beginTask(String name, int totalWork)
            {
            }
        } );
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    private void createAndOpenProject()
    {
        try
        {
            project.create( description, null );
            project.open( null );
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

}
