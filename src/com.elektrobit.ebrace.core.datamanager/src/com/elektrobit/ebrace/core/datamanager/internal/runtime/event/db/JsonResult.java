/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event.db;

import com.google.gson.JsonObject;

public class JsonResult
{

    private final static String EVT_TIMESTAMP_KEY = "eTimestamp";
    private final static String EVT_VALUE_KEY = "eValue";

    public static long toTimestamp(JsonObject obj)
    {
        return obj.get( EVT_TIMESTAMP_KEY ).getAsLong();
    }

    public static Double toDouble(JsonObject obj)
    {
        return obj.get( EVT_VALUE_KEY ).getAsDouble();
    }

    public static Number toNumber(JsonObject obj)
    {
        return obj.get( EVT_VALUE_KEY ).getAsNumber();
    }

    public static Long toLong(JsonObject obj)
    {
        return obj.get( EVT_VALUE_KEY ).getAsLong();
    }

    public static String toString(JsonObject obj)
    {
        return obj.get( EVT_VALUE_KEY ).getAsString();
    }

}
