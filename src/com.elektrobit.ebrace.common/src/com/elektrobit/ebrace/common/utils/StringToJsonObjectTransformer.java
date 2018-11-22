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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StringToJsonObjectTransformer
{
    private static JsonParser parser = new JsonParser();

    static JsonObject transform(final String jsonString)
    {
        return parser.parse( jsonString ).getAsJsonObject();
    }
}
