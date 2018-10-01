/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.service;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataStreamDescriptor
{

    private final JsonObject target;
    private final JsonParser parser = new JsonParser();

    public DataStreamDescriptor(JsonObject target)
    {
        this.target = target;
    }

    public String stringValueOf(String key)
    {
        return navigateTo( Arrays.asList( key.split( "\\." ) ) ).getAsString();
    }

    public JsonObject jsonObjectValueOf(String key)
    {
        return navigateTo( Arrays.asList( key.split( "\\." ) ) ).getAsJsonObject();
    }

    private JsonElement navigateTo(List<String> parts)
    {
        JsonObject latest = target;

        for (String part : parts.subList( 0, parts.size() - 1 ))
        {
            latest = latest.getAsJsonObject( part );
        }

        return latest.get( parts.get( parts.size() - 1 ) );
    }

    public void setId(String id)
    {
        target.addProperty( "id", id );
    }

    public void setStream(String type, String implementationDetails)
    {
        target.addProperty( "type", type );
        target.add( "details", parser.parse( implementationDetails ).getAsJsonObject() );

    }

    public void setTokenizer(String id)
    {
    }

    public void setParser(String id)
    {
    }
}
