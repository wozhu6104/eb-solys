/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.google.common.io.ByteStreams;

public class SimpleFileWriterTest
{
    private static final String DEFAULT_FILES_FOLDER_NAME = "recordings";
    private static final String CHUNK_SUFFIX = "-part";
    private static final String FILE_EXTENSTION = ".bin";
    private byte[] raceFileAsByteArray;

    @Before
    public void configureLog4j()
    {
        Properties properties = new Properties();
        properties.setProperty( "log4j.rootLogger", "warn, default" );
        properties.setProperty( "log4j.appender.default", "org.apache.log4j.ConsoleAppender" );
        properties.setProperty( "log4j.appender.default.layout", "org.apache.log4j.PatternLayout" );
        properties.setProperty( "log4j.appender.default.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n" );
        properties.setProperty( "log4j.logger.com.elektrobit.ebrace", "warn" );
        PropertyConfigurator.configure( properties );
    }

    @Before
    public void setup()
    {
        cleanupRecordingsFolder();
        setupRaceTestFile();
    }

    private void cleanupRecordingsFolder()
    {
        File folder = new File( getFolderPath() );
        if (folder.exists())
            folder.delete();
    }

    private void setupRaceTestFile()
    {
        SimpleFileWriter sut = new SimpleFileWriter();
        raceFileAsByteArray = new byte[250];
        new Random().nextBytes( raceFileAsByteArray );

        sut.startNewFile( getFolderPath(), "test.bin" );
        sut.writeBytes( raceFileAsByteArray );
        sut.closeStream();
    }

    @Ignore
    @Test
    public void testOfflineFileWriter()
    {
        verifyFileContent( raceFileAsByteArray );
    }

    @Test
    public void testStartNewFile() throws Exception
    {
        SimpleFileWriter sut = new SimpleFileWriter();
        String destPath = getFileToSplit().toString();
        FileInputStream inputStreamToSplit = new FileInputStream( getFileToSplit() );
        byte[] initialFileContent = ByteStreams.toByteArray( new FileInputStream( new File( destPath ) ) );

        int bytesRead = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int index = 0;

        while ((bytesRead = inputStreamToSplit.read()) != -1)
        {
            outputStream.write( bytesRead );

            if (outputStream.size() > 50)
            {
                sut.startNewFile( destPath.substring( 0, destPath.lastIndexOf( File.separatorChar ) + 1 ),
                                  destPath.substring( destPath.lastIndexOf( File.separatorChar ) + 1,
                                                      destPath.length() )
                                          .replace( FILE_EXTENSTION, "" ).concat( CHUNK_SUFFIX ) + index
                                          + FILE_EXTENSTION );
                index++;
                sut.writeBytes( outputStream.toByteArray() );
                outputStream.reset();
            }
        }
        if (outputStream.size() > 0)
        {
            sut.startNewFile( destPath.substring( 0, destPath.lastIndexOf( File.separatorChar ) + 1 ),
                              destPath.substring( destPath.lastIndexOf( File.separatorChar ) + 1, destPath.length() )
                                      .replace( FILE_EXTENSTION, "" ).concat( CHUNK_SUFFIX ) + index
                                      + FILE_EXTENSTION );
            sut.writeBytes( outputStream.toByteArray() );
            outputStream.reset();
        }

        sut.closeStream();
        outputStream.flush();
        outputStream.close();
        inputStreamToSplit.close();

        verifySizeAndChunkNames();
        verifyChunkContent( initialFileContent );
    }

    private void verifyChunkContent(byte[] fullFileContent) throws IOException
    {
        File folderFile = new File( getFolderPath() );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        File[] sortedFiles = folderFile.listFiles();
        Arrays.sort( sortedFiles );

        for (File nextFile : sortedFiles)
        {
            if (nextFile.getCanonicalPath().contains( CHUNK_SUFFIX ))
            {
                byte[] fileContent = ByteStreams.toByteArray( new FileInputStream( nextFile ) );
                out.write( fileContent );
            }
        }
        Assert.assertArrayEquals( fullFileContent, out.toByteArray() );
    }

    private void verifySizeAndChunkNames() throws IOException
    {
        File folderOfFileToSplit = new File( getFolderPath() );
        int totalSize = (int)folderOfFileToSplit.listFiles( new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return !name.contains( CHUNK_SUFFIX );
            }
        } )[0].length();

        for (File chunk : folderOfFileToSplit.listFiles())
        {
            if (chunk.getCanonicalPath().contains( CHUNK_SUFFIX ))
            {
                Assert.assertTrue( chunk.getCanonicalPath().contains( CHUNK_SUFFIX ) );
                totalSize -= chunk.length();
            }
        }
        Assert.assertEquals( 0, totalSize );
    }

    private void verifyFileContent(byte[] testBytes)
    {
        File folder = new File( getFolderPath() );
        File[] files = folder.listFiles();
        Assert.assertEquals( 1, files.length );
        try
        {
            byte[] fileContent = ByteStreams.toByteArray( new FileInputStream( files[0] ) );
            Assert.assertTrue( Arrays.equals( testBytes, fileContent ) );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String getFolderPath()
    {
        return Platform.getLocation().toOSString() + File.separator + DEFAULT_FILES_FOLDER_NAME;
    }

    private File getFileToSplit() throws IOException
    {
        File folderOfFileToSplit = new File( getFolderPath() );
        return folderOfFileToSplit.listFiles()[0];
    }

}
