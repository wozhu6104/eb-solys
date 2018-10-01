/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.application.impl.license;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LicenseFinder
{

    private final String pathToLicenseFolder;

    public LicenseFinder(String pathToLicenseFolder)
    {
        this.pathToLicenseFolder = pathToLicenseFolder;
    }

    public List<File> getLicenseKeys()
    {
        List<File> filesOfFolder = new ArrayList<>();

        File licenseFolder = new File( pathToLicenseFolder );
        if (licenseFolder.isDirectory())
        {
            filesOfFolder.addAll( Arrays.asList( licenseFolder.listFiles( new FileFilter()
            {
                @Override
                public boolean accept(File pathname)
                {
                    return pathname.getName().endsWith( ".key" );
                }
            } ) ) );
        }
        return filesOfFolder;
    }

}
