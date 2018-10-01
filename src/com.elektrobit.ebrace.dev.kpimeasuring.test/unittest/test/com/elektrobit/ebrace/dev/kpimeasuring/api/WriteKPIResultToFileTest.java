/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.dev.kpimeasuring.api;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResult;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResult2JsonStringTransformer;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultBuilder;
import com.elektrobit.ebrace.dev.kpimeasuring.api.KPIResultFileWriter;

public class WriteKPIResultToFileTest
{
    private String filePath;

    @Before
    public void setup()
    {
        filePath = "tmp/my-file.json";
    }

    @Test
    public void writeToFileAndReadBackTest() throws Exception
    {
        KPIResult kpiResult = new KPIResultBuilder().addMetaData( "date", "2015/11/17 15:27:01" )
                .addErrorMessage( "Nothing failed." ).addMeasuredItem( "time_to_nav_fully_operable", "30", "sec" )
                .build();
        KPIResultFileWriter.writeToFile( filePath, kpiResult );

        String kpiResultsAsString = FileHelper.readFileToString( filePath );

        Assert.assertEquals( KPIResult2JsonStringTransformer.transform( kpiResult ), kpiResultsAsString );
    }

    @After
    public void cleanup() throws IOException
    {
        FileHelper.deleteDirectory( new File( "tmp" ) );
    }
}
