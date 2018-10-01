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

public class CSVParser implements DataStreamParser
{
    private String delimiter = ",";
    private String channelName = "csv";

    public CSVParser(String delimiter, String channelName)
    {
        this.delimiter = delimiter;
        this.channelName = channelName;
    }

    @Override
    public String parse(byte[] message)
    {
        String[] parts = (new String( message )).split( delimiter );
        if (parts.length == 2)
        {
            String result = "{\"uptime\":" + parts[0] + ",\"channel\":\"" + channelName + "\",\"summary\":" + parts[1]
                    + ",\"value\":" + parts[1] + "}";
            return result;
        }
        else
        {
            return "";
        }
    }

    @Override
    public String getId()
    {
        return "csv";
    }
}
