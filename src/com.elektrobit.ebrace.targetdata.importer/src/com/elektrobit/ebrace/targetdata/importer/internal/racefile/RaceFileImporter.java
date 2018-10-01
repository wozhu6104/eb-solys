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
import java.io.IOException;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrameOld.OldHeader;
import com.elektrobit.ebrace.targetdata.importer.internal.TraceFileConverter;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.google.protobuf.InvalidProtocolBufferException;

public class RaceFileImporter extends AbstractImporter implements ResetListener
{
    private static final Logger LOG = Logger.getLogger( RaceFileImporterProxy.class );

    private ProtocolMessageDispatcher protocolMessageDispatcher = null;
    private TimestampProvider timestampProvider = null;

    private double totalBytesRead = 0;
    private double fileLength = 0;

    private boolean userNotInformed = true;

    private UserMessageLogger userMessageLogger;

    private DataSourceContext dataSourceContext;

    private boolean isLoadingAtLeastPartiallySuccessful;

    private FileSizeLimitService fileSizeLimitService;

    @Override
    public void processFileContent(File file) throws IOException
    {
        reset();
        FileInputStream fileInputStream = new FileInputStream( file );
        int headerLength = -1;
        fileLength = file.length();
        dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, file.getName() + "." );
        while ((headerLength = fileInputStream.read()) != -1)
        {
            if (isImportCanceled())
            {
                break;
            }
            totalBytesRead = totalBytesRead + 1;
            byte[] headerBuffer = new byte[headerLength];
            totalBytesRead += fileInputStream.read( headerBuffer );

            Header header = parseHeader( fileInputStream, headerBuffer );
            if (header == null)
            {
                LOG.error( "Header of Target Agent message is neither new, nor old header and couldn't be parsed." );
                informUser( UserMessageLoggerTypes.ERROR, "Error when parsing file header." );
                break;
            }

            byte[] payloadBuffer = new byte[header.getLength()];
            totalBytesRead += fileInputStream.read( payloadBuffer );

            if (header.getVersionToken() != VersionHandler.getVersionToken())
            {
                informUser( UserMessageLoggerTypes.ERROR,
                            "File is not compatible with this version of EB solys (file version: "
                                    + header.getVersionToken() + ", EB solys importer version: "
                                    + VersionHandler.getVersionToken() + ")." );
                break;
            }

            TimestampCreator hostTimestampCreator = timestampProvider.getHostTimestampCreator();

            Timestamp timestamp = timestampProvider.getHostTimestampCreator().create( header.getTimestamp() );

            protocolMessageDispatcher.newProtocolMessageReceived( timestamp,
                                                                  header.getType(),
                                                                  payloadBuffer,
                                                                  hostTimestampCreator,
                                                                  dataSourceContext );
            isLoadingAtLeastPartiallySuccessful = true;
            postProgress( totalBytesRead, fileLength );
        }
        cleanup( fileInputStream );
    }

    private void reset()
    {
        isLoadingAtLeastPartiallySuccessful = false;
        userNotInformed = true;
        totalBytesRead = 0;
        fileLength = 0;
    }

    public void informUser(UserMessageLoggerTypes typeVersion, String message)
    {
        if (typeVersion == UserMessageLoggerTypes.INFO)
        {
            LOG.info( message );
        }
        else if (typeVersion == UserMessageLoggerTypes.WARNING)
        {
            LOG.warn( message );
        }
        else if (typeVersion == UserMessageLoggerTypes.ERROR)
        {
            LOG.error( message );
        }

        userMessageLogger.logUserMessage( typeVersion, message );
    }

    private void cleanup(FileInputStream fileInputStream) throws IOException
    {
        fileInputStream.close();
    }

    private Header parseHeader(FileInputStream fileInputStream, byte[] headerBuffer) throws IOException
    {
        Header header = tryToParseNewHeader( headerBuffer );
        if (header == null)
        {
            final OldHeader oldHeader = tryToParseOldHeader( headerBuffer );
            if (oldHeader != null)
            {
                if (userNotInformed)
                {
                    final String message = "Header of Target Agent message does not contain any protocol version. Do not trust this data, if you not sure that the protocol of your plug-in has not changed.";
                    LOG.warn( message );
                    userMessageLogger.logUserMessage( UserMessageLoggerTypes.WARNING, message );
                    userNotInformed = false;
                }
                header = new TraceFileConverter( VersionHandler.getVersionToken() ).convert( oldHeader );
            }
        }
        return header;
    }

    private Header tryToParseNewHeader(byte[] headerBuffer)
    {
        Header header = null;
        try
        {
            header = Header.parseFrom( headerBuffer );
        }
        catch (InvalidProtocolBufferException e)
        {
            // Do nothing, shall be check outside.
        }
        return header;
    }

    private OldHeader tryToParseOldHeader(byte[] headerBuffer)
    {
        OldHeader header = null;
        try
        {
            header = OldHeader.parseFrom( headerBuffer );
        }
        catch (InvalidProtocolBufferException e)
        {
            // Do nothing, shall be check outside.
        }
        return header;
    }

    public void bind(ProtocolMessageDispatcher protocolMessageDispatcher)
    {
        this.protocolMessageDispatcher = protocolMessageDispatcher;
    }

    public void unbind(ProtocolMessageDispatcher protocolMessageDispatcher)
    {
        this.protocolMessageDispatcher = null;
    }

    public void bind(TimestampProvider timestampProvider)
    {
        this.timestampProvider = timestampProvider;
    }

    public void unbind(TimestampProvider timestampProvider)
    {
        this.timestampProvider = null;
    }

    public void bind(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    public void unbind(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

    public void bind(FileSizeLimitService fileSizeLimitService)
    {
        this.fileSizeLimitService = fileSizeLimitService;
    }

    public void unbind(FileSizeLimitService fileSizeLimitService)
    {
        this.fileSizeLimitService = null;
    }

    @Override
    protected long getMaximumTraceFileSizeInMB()
    {
        return fileSizeLimitService.getMaxSolysFileSizeMB();
    }

    @Override
    public String getSupportedFileExtension()
    {
        return "bin";
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return "EB solys File";
    }

    @Override
    public boolean isLoadingAtLeastPartiallySuccessful()
    {
        return isLoadingAtLeastPartiallySuccessful;
    }

    @Override
    public void onReset()
    {
        reset();
    }
}
