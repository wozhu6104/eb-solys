/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.tracefile.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.elektrobit.ebrace.common.utils.UnitConverter;
import com.elektrobit.ebrace.core.tracefile.api.SplitFileListener;
import com.elektrobit.ebrace.core.tracefile.api.SplitFileService;
import com.elektrobit.ebrace.core.tracefile.util.TraceFileSplitter;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;

@Component
public class SplitFileServiceImpl implements SplitFileService
{
    private static final int FILE_LIMIT_TO_CHUNK_SIZE_DIFFERENCE = 1;
    private final static Logger LOG = Logger.getLogger( SplitFileServiceImpl.class );
    private final List<SplitFileListener> listeners = new ArrayList<SplitFileListener>();
    private UserMessageLogger userMessageLogger;
    private FileSizeLimitService fileSizeLimitService;

    public SplitFileServiceImpl()
    {

    }

    @Override
    public void startSplittingFile(final String pathOfFileToSplit)
    {
        int chunkSizeBytes = getRequiredChunkSize();
        final TraceFileSplitter splitter = new TraceFileSplitter( new SimpleFileWriter(),
                                                                  chunkSizeBytes,
                                                                  userMessageLogger );
        FileInputStream inputStream = null;

        try
        {
            notifyStarted();
            inputStream = new FileInputStream( pathOfFileToSplit );
            splitter.splitFileIntoChunks( inputStream, pathOfFileToSplit );
            notifyDone();
        }
        catch (IOException e)
        {
            LOG.error( "Error splitting file: " + e.getMessage() );
            notifyError( e.getMessage() );
        }
        finally
        {
            closeStream( inputStream );
        }
    }

    private int getRequiredChunkSize()
    {
        long fileLimitMB = fileSizeLimitService.getMaxSolysFileSizeMB();
        return (int)UnitConverter.convertMBToBytes( fileLimitMB - FILE_LIMIT_TO_CHUNK_SIZE_DIFFERENCE );
    }

    private void closeStream(FileInputStream inputStream)
    {
        try
        {
            inputStream.close();
        }
        catch (IOException e)
        {
            LOG.error( "Error closing FileInputStream: " + e.getMessage() );
        }
    }

    @Override
    public void registerSplitDoneListener(SplitFileListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public void unregisterSplitDoneListener(SplitFileListener listener)
    {
        listeners.remove( listener );
    }

    private void notifyStarted()
    {
        List<SplitFileListener> listenersCopy = new ArrayList<SplitFileListener>( listeners );
        for (SplitFileListener listener : listenersCopy)
        {
            listener.onSplittingStarted();
        }
    }

    private void notifyDone()
    {
        List<SplitFileListener> listenersCopy = new ArrayList<SplitFileListener>( listeners );
        for (SplitFileListener listener : listenersCopy)
        {
            listener.onSplittingDone();
        }
    }

    private void notifyError(String message)
    {
        List<SplitFileListener> listenersCopy = new ArrayList<SplitFileListener>( listeners );
        for (SplitFileListener listener : listenersCopy)
        {
            listener.onSplittingError( message );
        }
    }

    @Reference(unbind = "unbindUserMessageLogger")
    public void bindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    public void unbindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

    @Reference(unbind = "unbindFileSizeLimitService")
    public void bindFileSizeLimitService(FileSizeLimitService fileSizeLimitService)
    {
        this.fileSizeLimitService = fileSizeLimitService;
    }

    public void unbindFileSizeLimitService(FileSizeLimitService fileSizeLimitService)
    {
        this.fileSizeLimitService = null;
    }

}
