/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.json;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elektrobit.ebrace.targetdata.impl.importer.json.JsonEventDataAdapter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonEventDataAdapterTest
{
    private static final JsonEventDataAdapter ADAPTER = new JsonEventDataAdapter();

    // @formatter:off
    public static final String EVENT_JSON = "{" + 
            "  \"uptime\": \"12345\",\n" + 
            "  \"channel\": \"trace.ipc.yourname\",\n" + 
            "  \"value\": {\n" + 
            "    \"summary\": \"I am a trace message\",\n" + 
            "    \"details\":{\n" + 
            "      \"requestId\": 1,\n" + 
            "      \"param1\": 12,\n" + 
            "      \"param2\": \"Value 2\",\n" + 
            "      \"param3\": null\n" + 
            "    },\n" + 
            "    \"duration\": \"123\",\n" +
            "    \"edge\": {\n" + 
            "      \"source\": \"service1.module1\",\n" + 
            "      \"destination\": \"service2.module2\",\n" + 
            "      \"type\": \"request\"\n" + 
            "    }\n" + 
            "  }\n" + 
            "}";
    // @formatter:on

    private static JsonObject eventData;

    private static final Long EXPECTED_TIMESTAMP = 12345000l;
    private static final Long EXPECTED_DURATION = 123000l;

    @BeforeClass
    public static void setup()
    {
        eventData = new JsonParser().parse( EVENT_JSON ).getAsJsonObject();
        ADAPTER.initialize( eventData );
    }

    @Test
    public void testGetTimestamp()
    {
        assertEquals( EXPECTED_TIMESTAMP, ADAPTER.getTimestamp() );
    }

    @Test
    public void testGetDuration()
    {
        assertEquals( EXPECTED_DURATION, ADAPTER.getDuration() );
    }
}
