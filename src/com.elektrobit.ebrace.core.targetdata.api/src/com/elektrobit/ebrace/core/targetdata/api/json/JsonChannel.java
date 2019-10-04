/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.targetdata.api.json;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class JsonChannel
{
    private final String name;
    private final String description;
    private final String unitDescription;

    @Override
    public String toString()
    {
        return new Gson().toJson( this );
    }
}
