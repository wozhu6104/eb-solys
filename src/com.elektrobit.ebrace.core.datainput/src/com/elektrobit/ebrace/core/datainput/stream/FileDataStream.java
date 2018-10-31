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
import java.io.File;
import java.io.FileInputStream;

import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.google.gson.JsonObject;

public class FileDataStream implements DataStream
{

    private final String path;
    private File file;
    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;

    public FileDataStream(JsonObject details)
    {
        path = details.get( "path" ).getAsString();
    }

    @Override
    public String getType()
    {
        return "file";
    }

    @Override
    public void open() throws Exception
    {
        file = new File( path );
        fileInputStream = new FileInputStream( file );
        bufferedInputStream = new BufferedInputStream( fileInputStream );
    }

    @Override
    public BufferedInputStream getInputStream() throws Exception
    {
        return bufferedInputStream;
    }

    @Override
    public void close() throws Exception
    {
        fileInputStream.close();
        bufferedInputStream.close();
    }

    @Override
    public String getImplementationDetails()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
