/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.scriptcompiler.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.ResourcesPlugin;

public class PrintHelper
{
    public static void printScriptsFolder()
    {
        Path startingDir = Paths
                .get( ResourcesPlugin.getWorkspace().getRoot().getProject( "RaceScripts" ).getLocationURI() );
        PrintFiles pf = new PrintFiles();
        try
        {
            Files.walkFileTree( startingDir, pf );
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
