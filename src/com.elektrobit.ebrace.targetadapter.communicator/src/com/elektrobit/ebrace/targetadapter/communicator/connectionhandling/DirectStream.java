/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.connectionhandling;

import java.io.IOException;
import java.io.InputStream;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;

public class DirectStream implements BytesFromStreamReader
{
    private InputStream orgInputStream;
    private final MessageReader<?> messageReader;
    private boolean shallRun = false;

    public DirectStream(ConnectionModel model, MessageReader<?> messageReader, String pathToCachedFile,
            ProtoMsgCacheDataAvailableListener callback)
    {
        this.messageReader = messageReader;
    }

    public void start(final InputStream orgInputStream)
    {
        this.orgInputStream = orgInputStream;
        shallRun = true;
    }

    @Override
    public byte[] readNBytes(int n)
    {
        if (shallRun)
        {

            if (n < 0)
            {
                return null;
            }

            byte[] content = new byte[n];
            try
            {
                fillBufferCompletely( orgInputStream, content );
            }
            catch (IOException e)
            {
                System.out.println( "Socket closed" );
            }
            return content;
        }
        else
        {
            throw new RuntimeException( "Run start first..." );
        }

    }

    private int fillBufferCompletely(InputStream is, byte[] bytes) throws IOException
    {
        int size = bytes.length;
        int offset = 0;
        while (offset < size)
        {
            int read = is.read( bytes, offset, size - offset );
            if (read == -1)
            {
                if (offset == 0)
                {
                    return -1;
                }
                else
                {
                    return offset;
                }
            }
            else
            {
                offset += read;
            }
        }

        return size;
    }

    @Override
    public boolean isEndOfStream()
    {
        return false;
    }

    public void waitForData()
    {
    }

    public Object getNextMsg()
    {
        return messageReader.readNextMessage( this );
    }

    public void stop()
    {
        shallRun = false;
    }

}
