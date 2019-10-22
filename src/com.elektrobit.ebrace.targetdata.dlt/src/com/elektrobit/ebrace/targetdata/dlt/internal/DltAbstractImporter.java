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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class DltAbstractImporter extends AbstractImporter
{
    protected TimestampProvider timestampProvider;
    protected TimeMarkerManager timeMarkerManager;
    protected ProtocolMessageDispatcher protocolMessageDispatcher;
    protected RuntimeEventAcceptor runtimeEventAcceptor;

    private File dltFile;

    @Override
    public void processFileContent(File file) throws IOException
    {
        log.info( "processing DLT file" + file.getName() );
        dltFile = file;
        long fileLength = dltFile.length();

        DltMessageProcessor messageProcessor = new DltMessageProcessor( file.getName(),
                                                                        file.length(),
                                                                        timestampProvider,
                                                                        timeMarkerManager,
                                                                        protocolMessageDispatcher,
                                                                        runtimeEventAcceptor );

        BufferedInputStream inputStream = new BufferedInputStream( new FileInputStream( file ) );

        MessageReader<DltMessage> parser = getMessageParser();

        int count = 0;

        ExecutorService executor = Executors.newFixedThreadPool( 20 );

        do
        {
            byte[] msgBuffer = nextMessageAsByteArray( inputStream );

            postProgress( fileLength, inputStream );

            executor.execute( new DltMessageReaderRunnable( messageProcessor, parser, msgBuffer ) );

            count++;
        }
        while (!isImportCanceled() && inputStream.available() > 0);

        System.out.println( "DLT Messages : " + count );

        inputStream.close();

        executor.shutdown();
    }

    private byte[] nextMessageAsByteArray(BufferedInputStream inputStream) throws IOException
    {
        final int STORAGE_HEADER_SIZE = 16;
        final int STANDARD_HEADER_SIZE = 4;
        final int BUFFER_SIZE = 2048;

        int bufLen = 0;
        int len = 0;

        byte[] dltMessageBuffer = new byte[BUFFER_SIZE];

        // read storage header
        inputStream.read( dltMessageBuffer, bufLen, STORAGE_HEADER_SIZE );
        String s = new String( dltMessageBuffer, 0, 3 );
        if (!s.equals( "DLT" ))
        {
            System.out.println( "Storage header not found" );
        }
        bufLen += STORAGE_HEADER_SIZE;

        // read standard header
        inputStream.read( dltMessageBuffer, bufLen, STANDARD_HEADER_SIZE );
        bufLen += STANDARD_HEADER_SIZE;
        len = (((dltMessageBuffer[bufLen - 2] & 0xff) << 8) | (dltMessageBuffer[bufLen - 1] & 0xff));

        inputStream.read( dltMessageBuffer, bufLen, len - STANDARD_HEADER_SIZE );

        return Arrays.copyOf( dltMessageBuffer, bufLen + len - STANDARD_HEADER_SIZE );
    }

    protected abstract MessageReader<DltMessage> getMessageParser();

    private void postProgress(long fileLength, BufferedInputStream inputStream) throws IOException
    {
        long remaining = fileLength - inputStream.available();
        postProgress( remaining, fileLength );
    }

    @Override
    protected abstract long getMaximumTraceFileSizeInMB();

    @Override
    public abstract String getSupportedFileExtension();

    @Override
    public abstract String getSupportedFileTypeName();
}
