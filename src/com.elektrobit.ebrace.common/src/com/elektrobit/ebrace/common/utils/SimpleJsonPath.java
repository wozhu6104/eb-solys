/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimpleJsonPath
{
    private final JsonObject target;

    public SimpleJsonPath(String jsonString)
    {
        this( new JsonParser().parse( jsonString ).getAsJsonObject() );
    }

    public SimpleJsonPath(JsonObject target)
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

    public JsonElement get(String path)
    {
        return navigateTo( Arrays.asList( path.split( "\\." ) ) );
    }

    private JsonElement navigateTo(List<String> parts)
    {
        JsonObject latest = target;

        for (String part : parts.subList( 0, parts.size() - 1 ))
        {
            latest = latest.getAsJsonObject( part );
            if (latest == null)
            {
                return null;
            }
        }

        return latest.get( parts.get( parts.size() - 1 ) );
    }

}
