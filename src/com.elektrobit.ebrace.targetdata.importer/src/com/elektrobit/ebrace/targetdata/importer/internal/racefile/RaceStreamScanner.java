/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal.racefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;

public class RaceStreamScanner
{
    private FileInputStream inputStream;

    public RaceStreamScanner(String filePath)
    {
        this( new File( filePath ) );
    }

    public RaceStreamScanner(File file)
    {
        this( file, 0 );
    }

    public RaceStreamScanner(File file, int skipElements)
    {
        try
        {
            inputStream = new FileInputStream( file );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < skipElements; i++)
        {
            next();
        }
    }

    private void closeStream()
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
                inputStream = null;
            }
            catch (IOException e)
            {
            }
        }
    }

    public RaceFileData next()
    {
        Header header = null;
        byte[] payload = null;

        try
        {
            int nextbyte = inputStream.read();
            if (nextbyte == -1)
                return null;
            byte[] headerBuffer = new byte[nextbyte];
            inputStream.read( headerBuffer );
            header = Header.parseFrom( headerBuffer );
            payload = new byte[header.getLength()];
            inputStream.read( payload, 0, header.getLength() );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            closeStream();
            return null;
        }

        RaceFileData data = new RaceFileData( header, payload );

        return data;
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        closeStream();
    }
}
