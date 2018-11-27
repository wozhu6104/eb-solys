/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.parser;

import com.elektrobit.ebrace.core.datainput.api.DataStreamParser;

public class JsonParser implements DataStreamParser
{

    @Override
    public String parse(byte[] message)
    {
        String jsonMessage = new String( message );
        if (jsonMessage.startsWith( "{" ))
        {
            return new String( message );
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getId()
    {
        return "json";
    }

}
