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

public class SimpleJsonPath
{
    private final JsonObject target;

    public SimpleJsonPath(String jsonString)
    {
        this( StringToJsonObjectTransformer.transform( jsonString ) );
    }

    public SimpleJsonPath(JsonObject target)
    {
        this.target = target;
    }

    public String stringValueOf(String key)
    {
        JsonElement value = getByPath( key );

        if (value == null)
        {
            return "";
        }

        if (value.isJsonPrimitive())
        {
            return value.getAsString();
        }
        else
        {
            return value.toString();
        }
    }

    public JsonObject jsonObjectValueOf(String key)
    {
        return getByPath( key ).getAsJsonObject();
    }

    public JsonElement get(String jsonPath)
    {
        return getByPath( jsonPath );
    }

    private JsonElement getByPath(String jsonPath)
    {
        return navigateTo( jsonPath );
    }

    private JsonElement navigateTo(String jsonPath)
    {
        List<String> parts = Arrays.asList( jsonPath.split( "\\." ) );
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
