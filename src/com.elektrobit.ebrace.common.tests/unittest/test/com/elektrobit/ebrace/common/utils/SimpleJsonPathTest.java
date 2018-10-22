/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.SimpleJsonPath;

public class SimpleJsonPathTest
{
    private static final int EXPECTED_FIRST_LEVEL_VALUE = 12345;
    private static final String EXPECTED_SECOND_LEVEL_VALUE = "I am a trace message";
    private static final int EXPECTED_THIRD_LEVEL_VALUE = 12;

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
    private static SimpleJsonPath simpleJsonPath;

    @BeforeClass
    public static void setup()
    {
        simpleJsonPath = new SimpleJsonPath( EVENT_JSON );
    }

    @Test
    public void getFirstLevelObject()
    {
        assertEquals( EXPECTED_FIRST_LEVEL_VALUE, simpleJsonPath.get( "uptime" ).getAsNumber().intValue() );
    }

    @Test
    public void getSecondLevelObject()
    {
        assertEquals( EXPECTED_SECOND_LEVEL_VALUE, simpleJsonPath.get( "value.summary" ).getAsString() );
    }

    @Test
    public void getThirdLevelObject()
    {
        assertEquals( EXPECTED_THIRD_LEVEL_VALUE,
                      simpleJsonPath.get( "value.details.param1" ).getAsNumber().intValue() );
    }

    @Test
    public void tryGetNonExistingValue()
    {
        assertEquals( null, simpleJsonPath.get( "value.paramX.paramY" ) );
    }
}
