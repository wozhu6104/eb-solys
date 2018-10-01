/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipHelper
{
    public static void unzipToFolder(String pathToZipFile, File outputFolder)
    {
        byte[] buffer = new byte[1024];

        ZipInputStream zipInputStream = null;
        ZipEntry nextZipEntry = null;

        try
        {
            createOutputFolderIfNeeded( outputFolder );

            zipInputStream = new ZipInputStream( new FileInputStream( pathToZipFile ) );

            nextZipEntry = extractZipFilestream( outputFolder, buffer, zipInputStream );

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            cleanup( zipInputStream, nextZipEntry );
        }
    }

    private static void createOutputFolderIfNeeded(File outputFolder)
    {
        if (outputFolder.exists())
        {
            FileHelper.deleteDirectory( outputFolder );
        }
        outputFolder.mkdir();
    }

    private static ZipEntry extractZipFilestream(File outputFolder, byte[] buffer, ZipInputStream zipInputStream)
            throws IOException, FileNotFoundException
    {
        ZipEntry nextZipEntry;
        nextZipEntry = zipInputStream.getNextEntry();
        while (nextZipEntry != null)
        {
            File nextFile = createNeededFileAndParentFolders( outputFolder, nextZipEntry );

            writeContentToFile( buffer, zipInputStream, nextFile );

            nextZipEntry = zipInputStream.getNextEntry();
        }
        return nextZipEntry;
    }

    private static File createNeededFileAndParentFolders(File outputFolder, ZipEntry nextZipEntry)
    {
        String nextFileName = nextZipEntry.getName();
        File nextFile = new File( outputFolder + File.separator + nextFileName );
        new File( nextFile.getParent() ).mkdirs();
        return nextFile;
    }

    private static void writeContentToFile(byte[] buffer, ZipInputStream zipInputStream, File nextFile)
            throws FileNotFoundException, IOException
    {
        FileOutputStream fos = new FileOutputStream( nextFile );

        int len;
        while ((len = zipInputStream.read( buffer )) > 0)
        {
            fos.write( buffer, 0, len );
        }

        fos.close();
    }

    private static void cleanup(ZipInputStream zipInputStream, ZipEntry nextZipEntry)
    {
        if (nextZipEntry != null)
        {
            try
            {
                zipInputStream.closeEntry();
            }
            catch (IOException e)
            {
                // Ignoring, cannot do anything
            }
        }

        if (zipInputStream != null)
        {
            try
            {
                zipInputStream.close();
            }
            catch (IOException e)
            {
                // Ignoring, cannot do anything
            }
        }
    }

    public static void unzipToFolder(String pathToZipFile, String outputFolder)
    {
        unzipToFolder( pathToZipFile, new File( outputFolder ) );
    }
}
