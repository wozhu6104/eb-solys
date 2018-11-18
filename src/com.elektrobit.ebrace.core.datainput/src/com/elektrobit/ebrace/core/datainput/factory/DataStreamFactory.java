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

import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.elektrobit.ebrace.core.datainput.service.DataStreamDescriptor;
import com.elektrobit.ebrace.core.datainput.stream.FileDataStream;
import com.elektrobit.ebrace.core.datainput.stream.SerialDataStream;
import com.elektrobit.ebrace.core.datainput.stream.TCPClientDataStream;

import lombok.extern.log4j.Log4j;

@Log4j
public class DataStreamFactory
{

    public static DataStream getDataStream(DataStreamDescriptor descriptor)
    {
        String streamType = descriptor.stringValueOf( "stream.type" );
        switch (streamType)
        {
            case "tcp-client" :
                try
                {
                    return new TCPClientDataStream( descriptor.jsonObjectValueOf( "stream.details" ) );
                }
                catch (Exception e)
                {
                    log.warn( "could not create data input for tcp-client" );
                }
            case "file" :
                return new FileDataStream( descriptor.jsonObjectValueOf( "stream.details" ) );
            case "serial" :
                return new SerialDataStream( descriptor.jsonObjectValueOf( "stream.details" ) );
            default : {
                log.warn( "no data stream for the type " + streamType + "available" );
                return null;
            }
        }
    }
}
