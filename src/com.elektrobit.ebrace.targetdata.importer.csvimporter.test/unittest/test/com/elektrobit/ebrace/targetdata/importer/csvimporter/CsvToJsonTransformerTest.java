/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.csvimporter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.targetdata.importer.internal.csvimporter.CsvToJsonTransformer;
import com.google.gson.Gson;

public class CsvToJsonTransformerTest
{

    @Test
    public void transform()
    {
        String input = "1234,12.0,numeric channel";
        String output = "{\"uptime\":1234,\"channel\":\"numeric channel\",\"value\":{\"summary\":\"12.0\"},\"duration\":0}";
        CsvToJsonTransformer transformer = new CsvToJsonTransformer();
        transformer.acquireMetaData( null, "." );

        assertEquals( output, new Gson().toJson( transformer.transformEvent( input ), JsonEvent.class ) );
    }

    @Test
    public void transformWithHint()
    {
        String input = "1234;12.0";
        String output = "{\"uptime\":1234,\"channel\":\"trace.csv\",\"value\":{\"summary\":\"12.0\"},\"duration\":0}";
        CsvToJsonTransformer transformer = new CsvToJsonTransformer();
        transformer.acquireMetaData( "timestamp;value", FileHelper.getBundleRootFolderOfClass( getClass() ) );

        assertEquals( output, new Gson().toJson( transformer.transformEvent( input ), JsonEvent.class ) );
    }

}
