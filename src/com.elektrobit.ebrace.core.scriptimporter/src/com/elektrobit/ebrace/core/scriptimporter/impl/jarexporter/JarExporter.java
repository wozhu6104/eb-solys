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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.dev.debug.annotations.api.EnterExitPrinter;
import com.elektrobit.ebrace.dev.debug.annotations.api.InterceptMethod;

import lombok.extern.log4j.Log4j;

@Log4j
public class JarExporter
{
    private static final String XTEND_EXTENSION = ".xtend";
    private final String binFolder;
    private final String jarExportFolder;
    private final String binFolderCopy;
    private final String srcFolder;

    public JarExporter(String srcFolder, String binFolder, String jarExportFolder, String binFolderCopy)
    {
        this.srcFolder = srcFolder;
        this.binFolder = binFolder;
        this.jarExportFolder = jarExportFolder;
        this.binFolderCopy = binFolderCopy;
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    public synchronized List<ScriptData> exportAllScriptsToJar()
    {
        List<ScriptData> exportedScripts = new ArrayList<>();
        File binFolderAsFile = new File( binFolder );

        if (binFolderAsFile.exists())
        {
            createTmpFolder( binFolderCopy );
            deleteExportedScriptFiles( jarExportFolder );
            {
                try
                {
                    FileUtils.copyDirectory( new File( binFolder ), new File( binFolderCopy ) );
                    List<File> xtendSrcFiles = findXtendFiles( srcFolder );
                    exportedScripts = doExportNew( xtendSrcFiles, binFolderCopy );
                    deleteTmpFolder( binFolderCopy );
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            log.warn( "Couldn't export scripts, because bin folder not exists." );
        }

        return exportedScripts;
    }

    private List<ScriptData> doExportNew(List<File> xtendSrcFiles, String binFolderCopy)
    {
        XtendProject2JarHelper jarHelper = new XtendProject2JarHelper();
        List<ScriptData> exportedScripts = new ArrayList<>();
        for (File srcFile : xtendSrcFiles)
        {
            String fileNameWithoutExtension = srcFile.getName().replace( XTEND_EXTENSION, "" );
            File jarFile = new File( jarExportFolder + "/" + fileNameWithoutExtension + ".jar" );
            jarHelper.createJarFromDir( new File( binFolderCopy ), jarFile );
            ScriptData scriptData = new ScriptData( fileNameWithoutExtension,
                                                    srcFile.getAbsolutePath(),
                                                    jarFile.getAbsolutePath() );
            exportedScripts.add( scriptData );
        }
        return exportedScripts;
    }

    private List<File> findXtendFiles(String srcFolder) throws IOException
    {
        Stream<Path> allFiles = Files.walk( Paths.get( srcFolder ) );
        Stream<Path> xtendFilesPaths = allFiles.filter( path -> path.toString().endsWith( XTEND_EXTENSION ) );
        Stream<File> xtendFilesStream = xtendFilesPaths.map( path -> new File( path.toString() ) );
        List<File> xtendFiles = xtendFilesStream.collect( Collectors.toList() );
        allFiles.close();

        return xtendFiles;
    }

    private void createTmpFolder(String tmpFolder)
    {
        new File( tmpFolder ).mkdirs();
    }

    private void deleteExportedScriptFiles(String exportFolder)
    {
        try
        {
            FileUtils.deleteDirectory( new File( exportFolder ) );
        }
        catch (IOException e)
        {
            log.warn( "Couldn't cleanup scripts folder. Maybe script(s) are still running. Error was: "
                    + e.getMessage() );
        }
        new File( exportFolder ).mkdirs();
    }

    private void deleteTmpFolder(String tmpFolder) throws IOException
    {
        FileUtils.deleteDirectory( new File( tmpFolder ) );
    }
}
