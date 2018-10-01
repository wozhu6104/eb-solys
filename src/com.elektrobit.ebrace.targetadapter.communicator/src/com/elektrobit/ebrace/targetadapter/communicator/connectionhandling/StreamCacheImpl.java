/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.connectionhandling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;

import lombok.extern.log4j.Log4j;

@Log4j
public class StreamCacheImpl implements BytesFromStreamReader
{
    private final Object waitForFullMsgReadMutex = new Object();
    private final File cacheFile;
    private volatile boolean shallRun = false;
    private BufferedOutputStream cacheFileOutStream;
    private Thread thread;
    private BufferedInputStream cacheFileInStream;
    private volatile AtomicLong write = new AtomicLong( 0 );
    private volatile AtomicLong read = new AtomicLong( 0 );
    private volatile boolean cacheEmpty = true;
    private final ProtoMsgCacheDataAvailableListener callback;
    private final ConnectionModel model;
    private SimpleFileWriter fileWriter = null;
    private final MessageReader<?> messageReader;

    public StreamCacheImpl(ConnectionModel model, MessageReader<?> messageReader, String pathToCachedFile,
            ProtoMsgCacheDataAvailableListener callback)
    {
        this.model = model;
        this.messageReader = messageReader;
        this.callback = callback;
        cacheFile = new File( pathToCachedFile );

        initCacheFileStreams();
    }

    private void initCacheFileStreams()
    {
        try
        {
            cacheFileOutStream = new BufferedOutputStream( new FileOutputStream( cacheFile ) );
            cacheFileInStream = new BufferedInputStream( new FileInputStream( cacheFile ) );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void start(final InputStream orgInputStream)
    {
        startFileWriter();
        shallRun = true;
        thread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                byte[] readCache = new byte[1024];
                while (shallRun)
                {

                    try
                    {
                        int bytesRead = orgInputStream.read( readCache, 0, 1024 );
                        if (bytesRead > 0)
                        {
                            byte[] arrayToWrite = Arrays.copyOfRange( readCache, 0, bytesRead );
                            if (model.isSaveToFile())
                            {
                                fileWriter.writeBytes( arrayToWrite );
                            }
                            cacheFileOutStream.write( arrayToWrite );
                            updateWriteCounter();
                        }
                        else
                        {
                            log.warn( "InputStream (Socket) closed while waiting for next byte." );
                            stop();
                        }
                    }
                    catch (SocketException e)
                    {
                        log.info( "Socket closed while reading from stream" );
                        stop();
                    }
                    catch (IOException e)
                    {
                        log.warn( "Reading from stream or writing to file cache failed.", e );
                        stop();
                    }
                }
            }
        } );
        thread.setName( "Cache: file writer" );
        thread.start();
    }

    private void startFileWriter()
    {
        if (fileWriter == null && model.isSaveToFile())
        {
            fileWriter = new SimpleFileWriter();
            fileWriter.startNewFile( model.getRecordingsFolder(), getFileName() );
        }
    }

    private String getFileName()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy-HH.mm.ss.S" );
        String fileExtension = model.getConnectionType().getExtension();
        return model.getRecordingFilePrefix() + "-" + model.getHost() + "-" + model.getPort() + "-"
                + dateFormat.format( new Date() ) + "." + fileExtension;
    }

    private void updateWriteCounter()
    {
        synchronized (cacheFile)
        {
            write.incrementAndGet();
            if (cacheEmpty)
            {
                callback.onNewDataInCacheAvailable();
                cacheEmpty = false;
            }
        }
    }

    @Override
    public byte[] readNBytes(int n)
    {
        if (n < 0)
        {
            return null;
        }

        byte[] content = new byte[n];

        int count = 0;
        try
        {
            while (count < n)
            {
                int result = cacheFileInStream.read();
                if (result > -1)
                {
                    content[count] = (byte)result;
                    count++;
                    updateReadCounter();
                }
                else
                {
                    flushFileCache();
                }
            }
        }
        catch (IOException e1)
        {
            log.warn( e1 );
        }

        if (count != n)
        {
            // Message not complete due to stream close, e.g. on disconnect.
            return null;
        }

        return content;
    }

    @Override
    public boolean isEndOfStream()
    {
        return false;
    }

    public void waitForData()
    {
        synchronized (cacheFile)
        {
            cacheEmpty = true;
        }
    }

    public Object getNextMsg()
    {
        Object nextProtoMsg = readNextMessage();
        if (nextProtoMsg == null)
        {
            nextProtoMsg = readMessageAfterFlush();
        }
        return nextProtoMsg;
    }

    private synchronized Object readNextMessage()
    {
        if (cacheFileInStream != null)
        {
            try
            {
                if (cacheFileInStream.available() < 1)
                {
                    return null;
                }

                synchronized (waitForFullMsgReadMutex)
                {
                    try
                    {
                        return messageReader.readNextMessage( this );
                    }
                    catch (Exception e)
                    {
                        log.warn( e );
                    }
                }
            }
            catch (IOException e)
            {
                // Ignore exception, because stream must be closed on disconnect to stop waiting threads
            }
        }
        return null;

    }

    private void updateReadCounter()
    {
        synchronized (cacheFile)
        {
            read.incrementAndGet();
        }
    }

    private void flushIfRun()
    {
        if (shallRun)
        {
            flushFileCache();
        }
    }

    private Object readMessageAfterFlush()
    {
        flushIfRun();
        return readNextMessage();
    }

    public void stop()
    {
        // Wake up message reading thread
        closeFileInStreamSilent();
        synchronized (waitForFullMsgReadMutex)
        {
            shallRun = false;
            if (thread != null)
            {
                try
                {
                    thread.join( 100 );
                }
                catch (InterruptedException e)
                {
                    log.warn( "Joining on thread dead failed.", e );
                }

                stopFileWriter();
                flushFileCache();
            }
        }
    }

    private void closeFileInStreamSilent()
    {
        if (cacheFileInStream != null)
        {
            try
            {
                cacheFileInStream.close();
            }
            catch (IOException ioException)
            {
            }
            cacheFileInStream = null;
        }
    }

    private void stopFileWriter()
    {
        if (fileWriter != null)
        {
            fileWriter.closeStream();
            fileWriter = null;
        }
    }

    public void flushFileCache()
    {
        try
        {
            log.debug( "Flush file cache." );
            cacheFileOutStream.flush();
        }
        catch (IOException e)
        {
            log.warn( "File cache flush failed.", e );
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        stopFileWriter();
        closeFileInStreamSilent();
        closeFileOutStreamSilent();
        super.finalize();
    }

    private void closeFileOutStreamSilent()
    {
        if (cacheFileOutStream != null)
        {
            try
            {
                cacheFileOutStream.close();
            }
            catch (IOException ioException)
            {
            }
            cacheFileOutStream = null;
        }

    }

}
