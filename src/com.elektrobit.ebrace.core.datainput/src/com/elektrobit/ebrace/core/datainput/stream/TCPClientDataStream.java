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
import java.net.Socket;

import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.google.gson.JsonObject;

public class TCPClientDataStream implements DataStream
{

    private final String host;
    private final int port;
    private Socket socket = null;

    public TCPClientDataStream(JsonObject instanceData)
    {
        host = instanceData.getAsJsonPrimitive( "host" ).getAsString();
        port = instanceData.getAsJsonPrimitive( "port" ).getAsInt();
    }

    @Override
    public void open() throws Exception
    {
        socket = new Socket( host, port );
    }

    @Override
    public BufferedInputStream getInputStream() throws IOException
    {
        return new BufferedInputStream( socket.getInputStream() );
    }

    @Override
    public void close() throws IOException
    {
        socket.close();
    }

    @Override
    public String getType()
    {
        return "TCP";
    }

    @Override
    public String getImplementationDetails()
    {
        return "{\"direction\":\"client\",\"host\":\"" + host + "\",\"port\":" + port + "}";
    }

}
