/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.extern.log4j.Log4j;

@Log4j
public class JsonHelper
{

    public static String getFieldFromJsonString(String jsonString, String fieldName)
    {
        String result = null;
        JsonElement parse = null;
        try
        {
            JsonParser jsonParser = new JsonParser();
            parse = jsonParser.parse( jsonString );
        }
        catch (JsonSyntaxException e)
        {
            log.debug( e.getMessage() + "\n\t" + jsonString + "is not a valid JSON string" );
        }
        if (parse != null && parse.isJsonObject())
        {
            JsonObject jsonObject = parse.getAsJsonObject();
            JsonElement jsonElement = jsonObject.get( fieldName );
            if (jsonElement != null)
            {
                if (jsonElement.isJsonObject())
                {
                    result = jsonElement.getAsJsonObject().toString();
                }
                else
                {
                    result = jsonElement.getAsString();
                }
            }
        }
        return result;
    }

    public static boolean isJson(String text)
    {
        String str = text.trim();

        // Quick check before using the json parser
        if (str.startsWith( "{" ) && str.endsWith( "}" ))
        {
            try
            {
                JsonParser jsonParser = new JsonParser();
                jsonParser.parse( text );
                return true;
            }
            catch (JsonSyntaxException e)
            {
                return false;
            }
        }
        else
        {
            return false;
        }

    }

}
