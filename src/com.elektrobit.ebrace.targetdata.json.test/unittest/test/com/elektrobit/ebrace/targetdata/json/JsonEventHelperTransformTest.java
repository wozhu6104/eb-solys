/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
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
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventEdge;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventNew;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebrace.targetdata.impl.importer.json.util.JsonEventHelper;
import com.google.gson.JsonObject;

public class JsonEventHelperTransformTest
{

    @Test
    public void transformNewInOldEvent() throws Exception
    {
        JsonObject details = new JsonObject();
        details.addProperty( "key", "value" );

        JsonEventNew newEvent = new JsonEventNew( 1000L,
                                                  new JsonChannel( "c1", "d1", "u1" ),
                                                  new JsonEventValue( "s1", details ),
                                                  0L,
                                                  new JsonEventEdge( "so1", "dest1", "request" ) );

        JsonEvent expectedEvent = new JsonEvent( 1000L,
                                                 "c1",
                                                 new JsonEventValue( "s1", details ),
                                                 0L,
                                                 new JsonEventEdge( "so1", "dest1", "request" ) );

        assertEquals( expectedEvent, JsonEventHelper.transformNew2OldEvent( newEvent ) );
    }

}
