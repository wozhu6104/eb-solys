/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.factory;

import com.elektrobit.ebrace.core.datainput.api.DataStreamParser;
import com.elektrobit.ebrace.core.datainput.dlt.DltStreamParser;
import com.elektrobit.ebrace.core.datainput.parser.CSVParser;
import com.elektrobit.ebrace.core.datainput.parser.JsonParser;
import com.elektrobit.ebrace.core.datainput.service.DataStreamDescriptor;

import lombok.extern.log4j.Log4j;

@Log4j
public class DataStreamParserFactory
{

    public static DataStreamParser getParser(DataStreamDescriptor descriptor)
    {
        String protocolType = descriptor.stringValueOf( "protocol.type" );
        switch (protocolType)
        {
            case "csv" :
                return new CSVParser( descriptor.stringValueOf( "protocol.details.itemtoken" ),
                                      descriptor.stringValueOf( "channel" ) );
            case "json" :
                return new JsonParser();
            case "dlt" :
                return new DltStreamParser();
            default :
                log.warn( "no parser for the protocol type " + protocolType + "available" );
                return null;
        }
    }

}
