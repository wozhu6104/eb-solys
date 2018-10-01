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

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
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

        BytesFromStreamReader bytesFromStreamReader = new BytesFromStreamReaderImpl( inputStream );

        MessageReader<DltMessage> parser = getMessageParser();

        DltMessage dltMsg = null;
        do
        {
            try
            {
                dltMsg = parser.readNextMessage( bytesFromStreamReader );
                messageProcessor.processMessage( dltMsg );

                postProgress( fileLength, inputStream );
            }
            catch (DltMessageParseException ex)
            {
                log.debug( "Failed to parse dlt message" + ex.getMessage() );
            }
        }
        while (!isImportCanceled() && inputStream.available() > 0);

        inputStream.close();
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
