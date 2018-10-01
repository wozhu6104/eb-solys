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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class SimpleFileWriter
{
    private static final Logger LOG = Logger.getLogger( SimpleFileWriter.class );

    private String fullPathOfChunk = null;
    private FileOutputStream fileStream;
    private boolean closed = false;

    public void startNewFile(String fullPath, String filename)
    {
        fullPathOfChunk = fullPath;
        closeStream();
        File file = createFile( fullPath, filename );
        createStream( file );
        closed = false;
    }

    public void writeBytes(byte[] dataToWrite)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "dataToWrite", dataToWrite );

        if (fullPathOfChunk == null)
            throw new IllegalStateException( "startNewFile() needs to be call first" );

        if (closed)
            return;

        try
        {
            fileStream.write( dataToWrite );
            fileStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void closeStream()
    {
        try
        {
            if (fileStream != null)
            {
                fileStream.close();
                closed = true;
                fileStream = null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File createFile(String fileDestinationPath, String filename)
    {
        File folder = new File( fileDestinationPath );
        File file = new File( folder, filename );
        try
        {
            if (file.exists())
            {
                LOG.warn( "File has been deleted as it already exists " + file );
                file.delete();
            }
            folder.mkdirs();
            file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }

    private void createStream(File file)
    {
        try
        {
            fileStream = new FileOutputStream( file );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
