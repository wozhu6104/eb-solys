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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class XtendProject2JarHelper
{
    private static final int BUFFER_SIZE = 2048;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    private int byteCounter = 0;

    private String canonicalPathOfJar = "";

    public boolean createJarFromDir(File eclipseProjectRootFolder, File jarFile)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "eclipseProjectRootFolder", eclipseProjectRootFolder );
        RangeCheckUtils.assertReferenceParameterNotNull( "jarFile", jarFile );

        FileOutputStream fileOutputStream = null;
        JarOutputStream jarOutputStream = null;
        try
        {
            canonicalPathOfJar = jarFile.getCanonicalPath();
            fileOutputStream = new FileOutputStream( jarFile );
            jarOutputStream = new JarOutputStream( fileOutputStream );
            for (File nextFile : eclipseProjectRootFolder.listFiles())
            {
                jarFileOrFolder( nextFile, jarOutputStream, "" );
            }
        }
        catch (IOException ioException)
        {
            return false;
        }
        finally
        {
            closeStreamSilently( jarOutputStream );
            closeStreamSilently( fileOutputStream );
        }
        return true;
    }

    private void closeStreamSilently(OutputStream outputStream)
    {
        try
        {
            if (outputStream != null)
            {
                outputStream.close();
                outputStream = null;
            }

        }
        catch (IOException e)
        {
        }
    }

    private void jarFileOrFolder(File nextFile, JarOutputStream jarOutputStream, String path) throws IOException
    {
        if (nextFile.isDirectory())
        {
            String subPath = path + nextFile.getName() + "/";
            if (!path.isEmpty())
            {
                createFolderInJar( nextFile, jarOutputStream, subPath );
            }
            jarDirForChildren( nextFile, jarOutputStream, subPath );
        }
        else
        {
            if (nextFile.getCanonicalPath().equals( canonicalPathOfJar ))
            {
                return;
            }

            createFileInJar( nextFile, jarOutputStream, path );
        }
    }

    private void createFileInJar(File nextFile, JarOutputStream jarOutputStream, String path)
            throws FileNotFoundException, IOException
    {
        FileInputStream fileInputStream = new FileInputStream( nextFile );
        try
        {
            JarEntry entry = new JarEntry( path + nextFile.getName() );
            entry.setTime( nextFile.lastModified() );
            jarOutputStream.putNextEntry( entry );
            while ((byteCounter = fileInputStream.read( buffer )) != -1)
            {
                jarOutputStream.write( buffer, 0, byteCounter );
            }
            jarOutputStream.flush();
            jarOutputStream.closeEntry();
        }
        catch (IOException ioException)
        {
            throw ioException;
        }
        finally
        {
            fileInputStream.close();
        }
    }

    private void jarDirForChildren(File nextFile, JarOutputStream jarOutputStream, String subPath) throws IOException
    {
        String[] contentOfDir = nextFile.list();
        for (String nextDirElement : contentOfDir)
        {
            File nextFileInDir = new File( nextFile, nextDirElement );
            jarFileOrFolder( nextFileInDir, jarOutputStream, subPath );
        }
    }

    private void createFolderInJar(File nextFile, JarOutputStream jarOutputStream, String subPath) throws IOException
    {
        JarEntry jarDirEntry = new JarEntry( subPath );
        jarDirEntry.setTime( nextFile.lastModified() );
        jarOutputStream.putNextEntry( jarDirEntry );
        jarOutputStream.flush();
        jarOutputStream.closeEntry();
    }

}
