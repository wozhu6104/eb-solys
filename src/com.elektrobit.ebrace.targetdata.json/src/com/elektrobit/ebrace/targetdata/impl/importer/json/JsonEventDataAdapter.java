/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.importer.json;

import com.elektrobit.ebrace.common.utils.SimpleJsonPath;
import com.elektrobit.ebrace.targetdata.json.api.JsonEventTag;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonEventDataAdapter
{
    private long timestamp = 0;
    private long duration = 0;

    public void initialize(JsonObject eventData)
    {
        timestamp = parseTimestamp( eventData, JsonEventTag.UPTIME );
        duration = parseTimestamp( eventData, JsonEventTag.DURATION );
    }

    private long parseTimestamp(JsonObject eventObject, String tagName)
    {
        JsonElement timestampElement = new SimpleJsonPath( eventObject ).get( tagName );
        if (timestampElement == null)
        {
            return 0;
        }
        String timestampString = timestampElement.getAsString();
        long timestamp = Long.parseLong( timestampString.replace( "u", "" ) );
        if (timestampString.endsWith( "u" ))
        {
            return timestamp;
        }
        else
        {
            return timestamp * 1000;
        }
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public Long getDuration()
    {
        return duration;
    }

}
