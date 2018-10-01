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
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;

import lombok.extern.log4j.Log4j;

@Log4j
public class ClasspathFileCreator
{
    public static String createClasspath(List<String> bundleIds) throws FileNotFoundException
    {
        String classpathTemplate = FileHelper.readFileToStringFromPlugin( "com.elektrobit.ebrace.core.scriptimporter",
                                                                          "files/classpat_" );

        classpathTemplate = classpathTemplate.replaceFirst( "%JRE_CONTAINER%",
                                                            System.lineSeparator()
                                                                    + "<classpathentry kind=\"con\" path=\""
                                                                    + getJVMPath() + "\"/>" + System.lineSeparator() );
        classpathTemplate = classpathTemplate.replaceFirst( "%XTEND_CONTAINER%",
                                                            "<classpathentry kind=\"con\" path=\"org.eclipse.xtend.XTEND_CONTAINER\"/>"
                                                                    + System.lineSeparator() );

        String libs = "";

        for (String nextBundleId : bundleIds)
        {
            String bundleLocationAbsPath = getBundleLocationAbsPath( nextBundleId );
            if (bundleLocationAbsPath == null)
            {
                log.info( "Bundle " + nextBundleId + " not installed." );
                continue;
            }
            if (!bundleLocationAbsPath.endsWith( ".jar" ))
            {
                bundleLocationAbsPath += "bin";
            }

            libs += "<classpathentry kind=\"lib\" path=\"" + bundleLocationAbsPath + "\"/>" + System.lineSeparator();
        }

        classpathTemplate = classpathTemplate.replaceFirst( "%LIBS%", libs );

        classpathTemplate = classpathTemplate.replaceFirst( "%SRC%",
                                                            "<classpathentry kind=\"src\" path=\""
                                                                    + ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME
                                                                    + "\"/>" + System.lineSeparator() );
        classpathTemplate = classpathTemplate.replaceFirst( "%XTEND_GEN%",
                                                            "<classpathentry kind=\"src\" path=\""
                                                                    + ScriptConstants.XTENDGEN_FOLDER + "\"/>"
                                                                    + System.lineSeparator() );
        classpathTemplate = classpathTemplate.replaceFirst( "%OUTPUT%",
                                                            "<classpathentry kind=\"output\" path=\""
                                                                    + ScriptConstants.BIN_FOLDER + "\"/>"
                                                                    + System.lineSeparator() );

        return classpathTemplate;

    }

    private static String getBundleLocationAbsPath(String nextBundleId)
    {
        String plainPath = getBundleLocation( nextBundleId );
        if (plainPath == null)
        {
            return null;
        }

        if (!plainPath.startsWith( "/" ))
        {
            File installFolder = null;
            try
            {
                installFolder = new File( Platform.getInstallLocation().getURL().toURI() );
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            if (installFolder != null)
            {
                String absolutePath = installFolder.getAbsolutePath().replace( "\\", "/" );
                plainPath = absolutePath + "/" + plainPath;
            }
        }
        else
        {
            if (plainPath.startsWith( "/" ))
            {
                plainPath = plainPath.replaceFirst( "/", "" );
            }
        }

        return plainPath;
    }

    private static IPath getJVMPath()
    {
        IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
        IPath containerPath = new Path( JavaRuntime.JRE_CONTAINER );
        IPath vmPath = containerPath.append( vmInstall.getVMInstallType().getId() ).append( vmInstall.getName() );
        return vmPath;
    }

    private static String getBundleLocation(String bundleId)
    {
        Bundle bundle = Platform.getBundle( bundleId );

        if (bundle == null)
        {
            return null;
        }

        IPath path = Path.fromOSString( bundle.getLocation() );

        String osString = path.toOSString();
        String plainPath = osString.replaceFirst( "reference:file:", "" );
        return plainPath.replace( "\\", "/" );
    }
}
