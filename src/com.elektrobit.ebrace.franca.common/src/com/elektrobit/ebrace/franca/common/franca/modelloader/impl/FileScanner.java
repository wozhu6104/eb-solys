/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.modelloader.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileScanner
{
    private FilenameFilter mFilter = null;
    private final FileFilter dirFilter = new DirectoryFilter();

    public FileScanner()
    {
        mFilter = new FidlFilter();
    }

    public List<String> getFiles(File parent, String path)
    {
        List<String> fileNames = new ArrayList<String>();

        if (!parent.exists())
        {
            throw new IllegalArgumentException( "Path " + path + " does not exist" );
        }

        File[] direcories = parent.listFiles( dirFilter );

        for (File f : direcories)
        {
            fileNames.addAll( getFiles( f, f.getAbsolutePath() ) );
        }

        File[] relevantFiles = parent.listFiles( mFilter );

        for (File f : relevantFiles)
        {
            fileNames.add( f.getAbsolutePath() );
        }

        return fileNames;
    }

    private class FidlFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File arg0, String filename)
        {
            return filename.toLowerCase().endsWith( ".fidl" );
        }

    }

    private class DirectoryFilter implements FileFilter
    {

        @Override
        public boolean accept(File pathname)
        {
            return pathname.isDirectory();
        }

    }

}
