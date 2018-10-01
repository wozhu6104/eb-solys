/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.ui.console.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.ui.console.application.impl.license.LicenseFinder;

public class LicenseFinderTest
{
    private File licenseFolder;
    private LicenseFinder licenseImporter;

    @Before
    public void setup()
    {
        final String pathToLicenseFile = "license-key";
        licenseFolder = new File( pathToLicenseFile );
        licenseFolder.mkdir();

        licenseImporter = new LicenseFinder( pathToLicenseFile );
    }

    @Test
    public void isLicenseKeyFolderNotAvailable() throws Exception
    {
        licenseFolder.delete();

        assertTrue( licenseImporter.getLicenseKeys().isEmpty() );
    }

    @Test
    public void isLicenseFolderEmpty() throws Exception
    {
        assertTrue( licenseImporter.getLicenseKeys().isEmpty() );
    }

    @Test
    public void getLicenseKeysCorrect() throws Exception
    {
        createFileInLicenseFolder( "my-license.key" );

        List<File> licenseKeys = licenseImporter.getLicenseKeys();
        assertTrue( licenseKeys.size() == 1 );
        assertEquals( "my-license.key", licenseKeys.get( 0 ).getName() );
    }

    @Test
    public void getOnlyFilesWithSuffixKey() throws Exception
    {
        createFileInLicenseFolder( "my-license.key" );
        createFileInLicenseFolder( "my-license.wrong" );

        List<File> licenseKeys = licenseImporter.getLicenseKeys();
        assertTrue( licenseKeys.size() == 1 );
        assertEquals( "my-license.key", licenseKeys.get( 0 ).getName() );
    }

    @Test
    public void folderInLicenseFolderAreIgnored() throws Exception
    {
        createFileInLicenseFolder( "my-license.key" );
        createFileInLicenseFolder( "my-license.wrong" );
        createFolderInLicenseFolder( "my-folder" );

        List<File> licenseKeys = licenseImporter.getLicenseKeys();
        assertTrue( licenseKeys.size() == 1 );
        assertEquals( "my-license.key", licenseKeys.get( 0 ).getName() );
    }

    private File createFileInLicenseFolder(String fileName) throws IOException
    {
        File file = new File( licenseFolder.getAbsolutePath() + "/" + fileName );
        file.createNewFile();
        return file;
    }

    private File createFolderInLicenseFolder(String fileName) throws IOException
    {
        File subfolder = new File( licenseFolder.getAbsolutePath() + "/" + fileName );
        subfolder.mkdir();
        return subfolder;
    }

    @After
    public void cleanup() throws IOException
    {
        FileUtils.deleteDirectory( licenseFolder );
    }
}
