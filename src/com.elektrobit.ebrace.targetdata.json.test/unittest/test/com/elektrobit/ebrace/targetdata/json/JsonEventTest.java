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

import org.junit.Test;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonChannel;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventEdge;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class JsonEventTest
{

    private static final String EXPECTED_JSON_1 = "{\"uptime\":1234,\"channel\":{\"name\":\"cpu\",\"description\":\"\"},\"value\":{\"summary\":12,\"details\":{\"param\":12}},\"duration\":0,\"edge\":{\"source\":\"A\",\"destination\":\"B\",\"type\":\"request\"}}";
    private static final String EXPECTED_JSON_2 = "{\"uptime\":1234,\"channel\":{\"name\":\"cpu\",\"description\":\"\"},\"value\":{\"summary\":\"Hello\",\"details\":{\"param\":12}},\"duration\":0,\"edge\":{\"source\":\"A\",\"destination\":\"B\",\"type\":\"request\"}}";
    private static JsonEvent INPUT_EVENT_1 = new JsonEvent( 1234l,
                                                                  new JsonChannel( "cpu", "", null ),
                                                                  new JsonEventValue( 12,
                                                                                      new JsonParser()
                                                                                              .parse( "{\"param\":12}" )
                                                                                              .getAsJsonObject() ),
                                                                  0l,
                                                                  new JsonEventEdge( "A", "B", "request" ) );

    private static JsonEvent INPUT_EVENT_2 = new JsonEvent( 1234l,
                                                                  new JsonChannel( "cpu", "", null ),
                                                                  new JsonEventValue( "Hello",
                                                                                      new JsonParser()
                                                                                              .parse( "{\"param\":12}" )
                                                                                              .getAsJsonObject() ),
                                                                  0l,
                                                                  new JsonEventEdge( "A", "B", "request" ) );

    @Test
    public void serializedCorrectly1()
    {
        assertEquals( EXPECTED_JSON_1, new Gson().toJson( INPUT_EVENT_1 ) );
    }

    @Test
    public void serializedCorrectly2()
    {
        assertEquals( EXPECTED_JSON_2, new Gson().toJson( INPUT_EVENT_2 ) );
    }

}
