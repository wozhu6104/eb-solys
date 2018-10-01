/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.tracefile.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.google.protobuf.InvalidProtocolBufferException;

public class TraceFileSplitter
{
    private final int chunkSizeBytes;
    private final String CHUNK_SUFFIX = "-part";
    private final String FILE_EXTENSION = ".bin";
    private final SimpleFileWriter dataFileWriter;
    private int fileIndex = 0;
    private int remainingBytes = 0;
    private boolean errorLogged = false;
    private final UserMessageLogger userMessageLogger;

    public TraceFileSplitter(SimpleFileWriter dataFileWriter, int chunkSizeBytes, UserMessageLogger userMessageLogger)
    {
        this.dataFileWriter = dataFileWriter;
        this.userMessageLogger = userMessageLogger;
        this.chunkSizeBytes = chunkSizeBytes;
    }

    public void splitFileIntoChunks(InputStream inputStream, String destinationPath)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try
        {
            splitFile( inputStream, destinationPath, outputStream );
        }
        catch (Exception e)
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.WARNING,
                                              "Problem has occured while splitting file, remaining unsaved data "
                                                      + remainingBytes / 1024
                                                      + " kB. Rest of data has been successfully saved." );
            errorLogged = true;
        }

        if (!errorLogged)
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.INFO, "File splitted successfully " );
        }

        saveRestBytesIntoNewFile( destinationPath, outputStream, fileIndex );
        dataFileWriter.closeStream();
    }

    private void saveRestBytesIntoNewFile(String destinationPath, ByteArrayOutputStream outputStream, int fileIndex)
    {
        if (outputStream.size() > 0)
        {
            writeChunkFile( destinationPath, fileIndex, outputStream );
        }
    }

    private void splitFile(InputStream inputStream, String destinationPath, ByteArrayOutputStream outputStream)
            throws IOException, InvalidProtocolBufferException
    {
        int headerLength = -1;

        while ((headerLength = inputStream.read()) != -1)
        {
            remainingBytes = inputStream.available();
            byte[] headerBuffer = new byte[headerLength];
            inputStream.read( headerBuffer );
            Header header = Header.parseFrom( headerBuffer );
            byte[] payloadBuffer = new byte[header.getLength()];
            inputStream.read( payloadBuffer );
            outputStream.write( headerLength );
            outputStream.write( headerBuffer );
            outputStream.write( payloadBuffer );
            if (outputStream.size() > chunkSizeBytes)
            {
                writeChunkFile( destinationPath, fileIndex, outputStream );
                fileIndex++;
                outputStream.reset();
            }
        }
    }

    private void writeChunkFile(String fullPathOfFileToSplit, int index, ByteArrayOutputStream outputStream)
    {
        File fileToSplit = new File( fullPathOfFileToSplit );
        String chunkName = fileToSplit.getName().replace( FILE_EXTENSION, "" ).concat( CHUNK_SUFFIX ) + index
                + FILE_EXTENSION;
        String destinationFolder = fileToSplit.getParent();

        dataFileWriter.startNewFile( destinationFolder, chunkName );
        dataFileWriter.writeBytes( outputStream.toByteArray() );
    }
}
