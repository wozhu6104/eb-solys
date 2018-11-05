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

import com.elektrobit.ebrace.common.utils.SimpleJsonPath;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataStreamDescriptor
{

    private final JsonObject target;
    private final JsonParser parser = new JsonParser();

    private SimpleJsonPath jsonPath = null;

    public DataStreamDescriptor(JsonObject target)
    {
        this.target = target;
        jsonPath = new SimpleJsonPath( target );
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

    public JsonObject jsonObjectValueOf(String key)
    {
        return jsonPath.jsonObjectValueOf( key );
    }

    public String stringValueOf(String key)
    {
        return jsonPath.stringValueOf( key );
    }
}
