/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.wizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StringFromFileReaderHelper
{
    public static String stringFromFile(File file) throws IOException
    {
        return stringFromFile( file, System.getProperty( "line.separator" ) );
    }

    private static String stringFromFile(File file, String joinWith) throws IOException
    {
        StringBuffer buf = new StringBuffer();
        BufferedReader in = new BufferedReader( new FileReader( file ) );

        try
        {
            String line = null;
            while ((line = in.readLine()) != null)
            {
                buf.append( line ).append( joinWith );
            }
        }
        finally
        {
            in.close();
        }
        return buf.toString();
    }
}
