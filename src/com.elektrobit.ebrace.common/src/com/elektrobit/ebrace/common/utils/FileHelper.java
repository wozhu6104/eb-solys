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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class FileHelper
{
    private static final Logger LOG = Logger.getLogger( FileHelper.class );

    public static String getBundleRootFolderOfClass(final Class<?> clazz)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "Class", clazz );

        URL binFolderOfBundle = clazz.getProtectionDomain().getCodeSource().getLocation();

        String bundleRoot = "";
        try
        {
            String binFolderOfBundleAsString = URLDecoder.decode( binFolderOfBundle.getFile(), "UTF-8" );

            if (binFolderOfBundleAsString.endsWith( "bin/" ))
            {
                // removes ending 'bin/' from binFolderOfBundleAsString,
                // it's the case if you start from a junit test
                bundleRoot = binFolderOfBundleAsString.substring( 0, binFolderOfBundleAsString.length() - 4 );
            }
            else
            {
                bundleRoot = binFolderOfBundleAsString;
            }
        }
        catch (UnsupportedEncodingException e)
        {
            LOG.error( "Couldn't decode bundle bin folder to UTF-8 String. Returning empty bundle root location." );
        }

        return bundleRoot;
    }

    public static void writeExampleConfigFileToPath(File file, String content)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "File", file );
        RangeCheckUtils.assertReferenceParameterNotNull( "File content", content );

        PrintWriter printWriter = null;
        try
        {
            printWriter = new PrintWriter( file );
            printWriter.write( content );
            printWriter.flush();
        }
        catch (FileNotFoundException e1)
        {
            LOG.error( "Couldn't found file at location " + file.getAbsolutePath() );
        }
        finally
        {
            printWriter.close();
        }

    }

    public static URI locateFileInBundle(final String bundleName, final String fullPath)
    {
        Bundle bundle = Platform.getBundle( bundleName );

        URL url = bundle.getEntry( fullPath );
        if (url != null)
        {
            URL urlWithoutSpaces = encodeSpacesInUrl( url );
            URL fileURL = null;
            try
            {
                fileURL = FileLocator.toFileURL( urlWithoutSpaces );
                fileURL = encodeSpacesInUrl( fileURL );
            }
            catch (IOException e)
            {
                LOG.error( "Couldn't found file at location " + fullPath + " in bundle " + bundle, e );
            }

            try
            {
                URI uri = fileURL.toURI();
                return uri;
            }
            catch (URISyntaxException e)
            {
                LOG.error( "Couldn't found file at location " + fullPath + " in bundle " + bundle, e );
            }
        }

        return null;
    }

    private static URL encodeSpacesInUrl(final URL inputUrl)
    {
        String urlAsStringWithoutSpaces = inputUrl.toExternalForm().replaceAll( " ", "%20" );
        URL outputUrl = null;
        try
        {
            outputUrl = new URL( urlAsStringWithoutSpaces );
        }
        catch (MalformedURLException e)
        {
            LOG.error( "Couldn't convert URL " + inputUrl + " in URL without white spaces.", e );
        }
        return outputUrl;
    }

    public static boolean deleteDirectory(File directory)
    {
        if (directory.exists())
        {
            File[] files = directory.listFiles();
            if (null != files)
            {
                for (int i = 0; i < files.length; i++)
                {
                    if (files[i].isDirectory())
                    {
                        deleteDirectory( files[i] );
                    }
                    else
                    {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    public static String getFileWithSuffix(final File folder, final String suffix)
    {
        for (String nextFile : Arrays.asList( folder.list() ))
        {
            if (nextFile.toLowerCase().endsWith( suffix.toLowerCase() ))
            {
                return folder.getAbsolutePath() + '/' + nextFile;
            }
        }

        return null;
    }

    public static String readFileToString(String filePath) throws FileNotFoundException
    {
        return readFileToString( new File( filePath ) );
    }

    public static String readFileToString(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner( file );
        String text = scanner.useDelimiter( "\\A" ).next();
        scanner.close();
        return text;
    }

    public static String readFileToStringFromPlugin(String pluginName, String filePath) throws FileNotFoundException
    {
        File file = new File( FileHelper.locateFileInBundle( pluginName, filePath ) );
        return readFileToString( file );
    }

    public static String removeExtension(String fileName)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "fileName", fileName );

        if (fileName.lastIndexOf( '.' ) == -1)
        {
            return fileName;
        }
        else
        {
            return fileName.substring( 0, fileName.lastIndexOf( '.' ) );
        }
    }

    public static String getFileExtension(String path)
    {
        int lastIndexOfPoint = path.lastIndexOf( '.' );
        int lastIndexOfFileSeperator = Math.max( path.lastIndexOf( '/' ), path.lastIndexOf( '\\' ) );

        return (lastIndexOfPoint > lastIndexOfFileSeperator) ? path.substring( lastIndexOfPoint + 1 ) : "";
    }
}
