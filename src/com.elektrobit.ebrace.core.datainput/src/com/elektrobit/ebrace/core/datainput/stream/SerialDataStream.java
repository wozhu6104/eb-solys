/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.stream;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.google.gson.JsonObject;

import dk.thibaut.serial.SerialPort;
import dk.thibaut.serial.enums.BaudRate;
import dk.thibaut.serial.enums.DataBits;
import dk.thibaut.serial.enums.Parity;
import dk.thibaut.serial.enums.StopBits;

public class SerialDataStream implements DataStream
{

    private final int timeout = 1000;
    private BaudRate baudrate = BaudRate.B9600;
    private final Parity parity = Parity.NONE;
    private final StopBits stopBits = StopBits.ONE;
    private final DataBits dataBits = DataBits.D8;
    private String portName = "";
    SerialPort port;

    public SerialDataStream(JsonObject details)
    {
        portName = details.getAsJsonPrimitive( "port" ).getAsString();
        baudrate = BaudRate.fromInteger( details.getAsJsonPrimitive( "baudrate" ).getAsInt() );
    }

    @Override
    public String getType()
    {
        return "serial";
    }

    @Override
    public String getImplementationDetails()
    {
        return "{}";
    }

    @Override
    public BufferedInputStream getInputStream() throws IOException
    {
        port.setTimeout( timeout );
        port.setConfig( baudrate, parity, stopBits, dataBits );
        return new BufferedInputStream( port.getInputStream() );
    }

    @Override
    public void open() throws Exception
    {
        port = SerialPort.open( portName );
    }

    @Override
    public void close() throws IOException
    {
        port.close();
    }

}
