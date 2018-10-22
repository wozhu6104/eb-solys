/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package de.systemticks.ebrace.eventhooks.regextochannelhook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

public class JsonEventByRegExMapper
{
    private final Pattern pattern;
    private Matcher matcher;
    private final String channelName;
    private final boolean asLong;

    public JsonEventByRegExMapper(String regEx, String channelName, boolean asLong)
    {
        pattern = Pattern.compile( regEx );
        this.channelName = channelName;
        this.asLong = asLong;
    }

    public JsonObject map(JsonObject event)
    {
        JsonObject mappedEvent = new JsonObject();
        matcher = pattern.matcher( event.toString() );
        if (matcher.find())
        {
            mappedEvent.addProperty( "uptime", event.get( "uptime" ).getAsString() );
            mappedEvent.addProperty( "channel", channelName + "." + matcher.group( 1 ) );
            if (asLong)
            {
                mappedEvent.addProperty( "value", Long.parseLong( matcher.group( 2 ) ) );
            }
            else
            {
                mappedEvent.addProperty( "value", matcher.group( 2 ) );
            }
        }
        return mappedEvent;
    }

}
