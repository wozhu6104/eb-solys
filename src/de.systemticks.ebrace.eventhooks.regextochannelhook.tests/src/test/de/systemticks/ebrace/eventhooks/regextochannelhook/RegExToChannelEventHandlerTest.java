/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.de.systemticks.ebrace.eventhooks.regextochannelhook;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.systemticks.ebrace.eventhooks.regextochannelhook.JsonEventByRegExMapper;

public class RegExToChannelEventHandlerTest
{
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
            "    \"edge\": {\n" + 
            "      \"source\": \"service1.module1\",\n" + 
            "      \"destination\": \"service2.module2\",\n" + 
            "      \"type\": \"request\"\n" + 
            "    }\n" + 
            "  }\n" + 
            "}";
    // @formatter:on
    // @formatter:off
    public static final String NEW_EVENT_JSON_STRING_VALUE = "{\"uptime\":\"12345\",\"channel\":\"cpu.yourname\",\"value\":\"12\"}";
    public static final String NEW_EVENT_JSON_LONG_VALUE = "{\"uptime\":\"12345\",\"channel\":\"cpu.yourname\",\"value\":12}";
    // @formatter:on
    private static final String REG_EX = "[\\s\\S]*channel\":\"[\\S]*\\.([\\S]*)\",[\\s\\S]*param1\":([0-9]*),[\\s\\S]*";
    private static JsonObject event;

    @BeforeClass
    public static void setup()
    {
        event = new JsonParser().parse( EVENT_JSON ).getAsJsonObject();
    }

    @Test
    public void handleJsonEventWithStringValue()
    {
        String result = new JsonEventByRegExMapper( REG_EX, "cpu", false ).map( event ).toString();
        assertEquals( NEW_EVENT_JSON_STRING_VALUE, result );
    }

    @Test
    public void handleJsonEventWithLongValue()
    {
        String result = new JsonEventByRegExMapper( REG_EX, "cpu", true ).map( event ).toString();
        assertEquals( NEW_EVENT_JSON_LONG_VALUE, result );
    }
}
