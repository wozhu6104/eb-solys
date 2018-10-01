/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.kpimeasuring.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class KPIResultFileWriter
{
    private static final Logger LOG = Logger.getLogger( KPIResultFileWriter.class );

    public static boolean writeToFile(String filePath, KPIResult kpiResult)
    {
        try
        {
            File file = new File( filePath );
            file.getParentFile().mkdirs();

            PrintWriter writer = new PrintWriter( file );
            writer.write( KPIResult2JsonStringTransformer.transform( kpiResult ) );
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            LOG.error( "Couldn't write KPIResult, because couldn't find the file with the path " + filePath + "." );
            return false;
        }
        return true;
    }
}
