/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal;

import java.io.IOException;
import java.io.InputStream;

import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;

public class BytesFromStreamReaderImpl implements BytesFromStreamReader
{
    int readBytes = 0;
    private final InputStream inputStream;

    public BytesFromStreamReaderImpl(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public byte[] readNBytes(int length)
    {
        byte[] result = new byte[length];
        try
        {
            readBytes = inputStream.read( result );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean isEndOfStream()
    {
        return readBytes == -1;
    }
}
