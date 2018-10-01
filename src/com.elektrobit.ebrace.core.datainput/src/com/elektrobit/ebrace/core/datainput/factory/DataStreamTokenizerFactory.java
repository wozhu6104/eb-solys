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

import java.io.IOException;

import com.elektrobit.ebrace.core.datainput.api.DataStreamTokenizer;
import com.elektrobit.ebrace.core.datainput.service.DataStreamDescriptor;
import com.elektrobit.ebrace.core.datainput.tokenizer.ByteArrayDelimiterTokenizer;
import com.elektrobit.ebrace.core.datainput.tokenizer.DltStreamTokenizer;

import lombok.extern.log4j.Log4j;

@Log4j
public class DataStreamTokenizerFactory
{

    public static DataStreamTokenizer getTokenizer(DataStreamDescriptor descriptor) throws IOException
    {
        String tokenizerType = descriptor.stringValueOf( "protocol.details.delimiter" );
        switch (tokenizerType)
        {
            case "byte-array-delimiter" :
                return new ByteArrayDelimiterTokenizer( descriptor.stringValueOf( "protocol.details.messagetoken" )
                        .getBytes() );
            case "dlt-stream-header" :
                return new DltStreamTokenizer();
            default : {
                log.warn( "no tokenizer for the type " + tokenizerType + "available" );
                return null;
            }
        }
    }

}
