/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.preferences.impl;

import java.lang.reflect.Type;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PreferenceConnectionTypeInstanceCreator
        implements
            JsonSerializer<ConnectionType>,
            JsonDeserializer<PreferenceConnectionType>
{
    private static final String TYPE = "type";
    private static final String FIELDS = "fields";

    @Override
    public PreferenceConnectionType deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Class<?> clazz = PreferenceConnectionType.class;
        return jsonDeserializationContext.deserialize( jsonObject.get( FIELDS ), clazz );
    }

    @Override
    public JsonElement serialize(ConnectionType object, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty( TYPE, PreferenceConnectionType.class.getName() );
        JsonObject fieldsObject = new JsonObject();
        fieldsObject.addProperty( "name", object.getName() );
        fieldsObject.addProperty( "extension", object.getExtension() );
        jsonObject.add( FIELDS, fieldsObject );
        return jsonObject;
    }
}
