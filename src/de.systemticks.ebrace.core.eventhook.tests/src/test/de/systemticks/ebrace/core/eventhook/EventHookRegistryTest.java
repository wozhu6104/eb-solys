/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.de.systemticks.ebrace.core.eventhook;

import org.junit.BeforeClass;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

import de.systemticks.ebrace.core.eventhook.impl.EventHookRegistryServiceImpl;
import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

public class EventHookRegistryTest
{
    // @formatter:off
    public static String eventString = "{" + 
            "  \"uptime\": \"12345\",\n" + 
            "  \"channel\": \"trace.ipc.yourname\",\n" + 
            "  \"value\": {\n" + 
            "    \"summary\": \"I am a trace message\",\n" + 
            "    \"details\":{\n" + 
            "      \"requestId\": 1,\n" + 
            "      \"param1\": 1,\n" + 
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

    private static EventHookRegistry registry;
    private static EventHook testHook;
    private static int indicator = 0;

    @BeforeClass
    public static void setup()
    {
        registry = new EventHookRegistryServiceImpl();
        testHook = new EventHook()
        {

            @Override
            public void onEvent(RuntimeEvent<?> event)
            {
                indicator++;
            }
        };
    }

    /*
     * @Test public void isHookRegistered() { registry.register( testHook ); assertEquals( 1, registry.getAll().size()
     * ); }
     * 
     * @Test public void isHookUnregistered() { registry.unregister( testHook ); assertEquals( 0,
     * registry.getAll().size() ); }
     * 
     * @Test public void areInterestedHooksCalled() { registry.register( testHook ); registry.callFor( eventString );
     * assertEquals( 1, indicator ); }
     */
}
