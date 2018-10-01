/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.kpimeasuring.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KPIResult2JsonStringTransformer
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private KPIResult2JsonStringTransformer()
    {
    }

    public static String transform(KPIResult kpiResult)
    {
        return GSON.toJson( kpiResult );
    }

}
