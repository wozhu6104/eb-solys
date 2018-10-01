/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.io.File;

public class RaceScriptHelper
{
    public static String getScriptNameFromPath(String fullPath)
    {
        return new File( fullPath ).getName().replaceAll( "\\.jar", "" );
    }

    public static String createRunJarFilePath(final String absolutePathToJarFile)
    {
        String originalScriptName = getScriptNameFromPath( absolutePathToJarFile );
        String newScriptName = originalScriptName + "-run";

        String newFilePath = absolutePathToJarFile.replaceFirst( originalScriptName, newScriptName );
        return newFilePath;
    }
}
