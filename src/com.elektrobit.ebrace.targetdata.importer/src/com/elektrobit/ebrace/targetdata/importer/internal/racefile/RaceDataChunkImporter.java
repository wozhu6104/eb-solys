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
import java.io.IOException;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.memory.MemoryObserver;
import com.elektrobit.ebrace.core.importerregistry.api.AbstractImporter;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.MetaData;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderMetaData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService.MetaDataKeys;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import lombok.extern.log4j.Log4j;

@Log4j
public class RaceDataChunkImporter extends AbstractImporter implements ResetListener
{
    private static final int LOG_EVERY_NTH_CHUNK_WARNING = 40;

    private static final int CHUNK_SIZE_TO_LOAD_IN_MIB = 50;
    private static final int CHUNK_SIZE_TO_LOAD_IN_BYTE = CHUNK_SIZE_TO_LOAD_IN_MIB * 1024 * 1024;

    private static final Logger LOG = Logger.getLogger( RaceFileImporterProxy.class );

    private static final long MAX_FILE_SIZE = Long.MAX_VALUE;

    private final Unit<Long> unitLong = Unit.createCustomUnit( "Long", Long.class );
    private ProtocolMessageDispatcher protocolMessageDispatcher = null;
    private TimestampProvider timestampProvider = null;
    private UserInteractionPreferences userInteractionPreferences = null;
    private UserMessageLogger userMessageLogger;
    private TargetHeaderMetaDataService targetHeaderMetaDataService;

    private File loadedFile;
    private Long chunkStartTime;
    private Long chunkEndTime;
    private RTimeRange fileTimeRange;
    private int chunkTooBigLogCounter = 0;

    private RuntimeEventAcceptor runtimeEventAcceptor;

    public RaceDataChunkImporter()
    {
    }

    public void activate()
    {
    }

    @Override
    public void processFileContent(long startTimestamp, Long desiredChunkLengthTime, File file) throws IOException
    {
        processFileContent2( seekToTimestamp( startTimestamp, file ), desiredChunkLengthTime, file );
    }

    private Integer seekToTimestamp(long timestamp, File file)
    {
        int numberOfElements = 0;

        RaceStreamScanner scanner = new RaceStreamScanner( file );
        RaceFileData nextHeader = null;
        while ((nextHeader = scanner.next()) != null)
        {
            long convertTimeToHostTimeInMicros = convertTimeToHostTimeInMicros( nextHeader.getHeader().getTimestamp() );
            if (convertTimeToHostTimeInMicros >= timestamp)
            {
                break;
            }
            else
            {
                numberOfElements++;
            }
        }

        return numberOfElements;
    }

    @Override
    public void processFileContent(File file) throws IOException
    {
        processFileContent2( 0, null, file );
    }

    private void processFileContent2(int numberOfElementsToSkip, Long desiredChunkLengthTime, File file)
            throws IOException
    {
        findFileTimeRangeIfNeeded( file );
        setOfflineMode();

        RTimeRange chunkTimeRange = new RTimeRange();

        int loadedBytes = 0;

        RaceStreamScanner scanner = new RaceStreamScanner( file, numberOfElementsToSkip );
        RaceFileData nextData = null;
        while ((nextData = scanner.next()) != null)
        {
            loadedBytes += updateBytesLoaded( nextData );
            if (importShouldStop( loadedBytes, desiredChunkLengthTime, chunkTimeRange ))
            {
                break;
            }
            else
            {
                updateChunkTimeRange( chunkTimeRange, nextData );

                protocolMessageDispatcher.newProtocolMessageReceived( convertTimeToHostTimeInMillis( nextData
                        .getHeader().getTimestamp() ),
                                                                      nextData.getHeader().getType(),
                                                                      nextData.getPayload(),
                                                                      timestampProvider.getHostTimestampCreator(),
                                                                      new DataSourceContext( SOURCE_TYPE.FILE,
                                                                                             file.getName() + "." ) );

                postProgress( loadedBytes, CHUNK_SIZE_TO_LOAD_IN_BYTE );
            }

        }

        chunkStartTime = chunkTimeRange.getStartTime();
        chunkEndTime = chunkTimeRange.getEndTime();

    }

    private boolean importShouldStop(int loadedBytes, Long desiredChunkLengthTime, RTimeRange chunkTimeRange)
    {
        if (desiredChunkLengthTime == null)
        {
            return isImportCanceled() || chunkSizeReached( loadedBytes );
        }
        else
        {
            boolean chunkLongEnough = isChunkLongEnough( chunkTimeRange, desiredChunkLengthTime );
            if (chunkSizeReached( loadedBytes ) && !chunkLongEnough)
            {
                logChunkTooBigForDesiredTime( loadedBytes );
            }
            return chunkLongEnough;
        }
    }

    private boolean isChunkLongEnough(RTimeRange chunkTimeRange, Long desiredChunkLengthTime)
    {
        if (chunkTimeRange.getStartTime() != null && chunkTimeRange.getEndTime() != null)
        {
            long currentLength = chunkTimeRange.getEndTime() - chunkTimeRange.getStartTime();
            return currentLength >= desiredChunkLengthTime;
        }
        else
        {
            return false;
        }
    }

    private boolean chunkSizeReached(int size)
    {
        return size >= CHUNK_SIZE_TO_LOAD_IN_BYTE;
    }

    private void logChunkTooBigForDesiredTime(int loadedBytes)
    {
        chunkTooBigLogCounter++;
        if (chunkTooBigLogCounter % LOG_EVERY_NTH_CHUNK_WARNING == 0)
        {
            chunkTooBigLogCounter = 0;
            int usedMemoryPercent = MemoryObserver.getUsedMemoryPercent();
            LOG.warn( "Chunk too big before reaching desired length. Only every " + LOG_EVERY_NTH_CHUNK_WARNING
                    + "th warning is shown. Loaded bytes " + loadedBytes + " Used memory " + usedMemoryPercent + "%" );
        }
    }

    private int updateBytesLoaded(RaceFileData nextData)
    {
        int currentSize = 1;
        currentSize += nextData.getHeader().getSerializedSize();
        currentSize += nextData.getPayload().length;
        return currentSize;
    }

    private void updateChunkTimeRange(RTimeRange chunkTimeRange, RaceFileData nextData)
    {
        if (chunkTimeRange.getStartTime() == null)
        {
            chunkTimeRange.setStartTime( convertTimeToHostTimeInMicros( nextData.getHeader().getTimestamp() ) );
        }

        chunkTimeRange.setEndTime( convertTimeToHostTimeInMicros( nextData.getHeader().getTimestamp() ) );
    }

    private Timestamp convertTimeToHostTimeInMillis(long timestamp)
    {
        return timestampProvider.getHostTimestampCreator().create( timestamp );
    }

    private long convertTimeToHostTimeInMicros(long timestamp)
    {
        return convertTimeToHostTimeInMillis( timestamp ).getTimeInMillis() * 1000;
    }

    private void findFileTimeRangeIfNeeded(File currentFile)
    {
        if (loadedFile == null || !loadedFile.equals( currentFile ))
        {
            fileTimeRange = getFileTimeRange( currentFile );
            loadedFile = currentFile;
        }
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

    private void setOfflineMode()
    {
        userInteractionPreferences.setIsLiveMode( false );
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

    public void bind(UserInteractionPreferences userInteractionPreferences)
    {
        this.userInteractionPreferences = userInteractionPreferences;
    }

    public void unbind(UserInteractionPreferences userInteractionPreferences)
    {
        this.userInteractionPreferences = null;
    }

    public void bind(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    public void unbind(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

    public void bind(TargetHeaderMetaDataService targetHeaderMetaDataService)
    {
        this.targetHeaderMetaDataService = targetHeaderMetaDataService;
    }

    public void unbind(TargetHeaderMetaDataService targetHeaderMetaDataService)
    {
        this.targetHeaderMetaDataService = null;
    }

    public void bind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbind(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Override
    protected long getMaximumTraceFileSizeInMB()
    {
        return MAX_FILE_SIZE;
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
    public boolean isChunkLoadingSupported()
    {
        return true;
    }

    @Override
    public Long getFileStartTime()
    {
        return fileTimeRange.getStartTime();
    }

    @Override
    public Long getFileEndTime()
    {
        return fileTimeRange.getEndTime();
    }

    @Override
    public Long getChunkStartTime()
    {
        return chunkStartTime;
    }

    @Override
    public Long getChunkEndTime()
    {
        return chunkEndTime;
    }

    public RTimeRange getFileTimeRange(File file)
    {
        final RTimeRange fileDetails = new RTimeRange();

        RaceStreamScanner scanner = new RaceStreamScanner( file );
        RaceFileData nextHeader = null;
        while ((nextHeader = scanner.next()) != null)
        {
            updateChunkTimeRange( fileDetails, nextHeader );
            extractMetaDataInfo( nextHeader.getHeader() );
        }

        return fileDetails;
    }

    private void extractMetaDataInfo(Header header)
    {
        for (MetaData nextMetaDataItem : header.getMetaDataInfoList())
        {
            long convertedTimestampInMicros = convertTimeToHostTimeInMicros( header.getTimestamp() );
            RTargetHeaderMetaData metaData = new RTargetHeaderMetaData( convertedTimestampInMicros,
                                                                        nextMetaDataItem.getKey(),
                                                                        nextMetaDataItem.getValue() );
            targetHeaderMetaDataService.addMetaData( metaData );

            addSystemCpuValuesToChannelIfDebugLogEnabled( header, nextMetaDataItem, convertedTimestampInMicros );
        }
    }

    private void addSystemCpuValuesToChannelIfDebugLogEnabled(Header header, MetaData nextMetaDataItem,
            long convertedTimestampInMicros)
    {
        if (log.isDebugEnabled() && nextMetaDataItem.getKey().equals( MetaDataKeys.SYSTEM_CPU_VALUES.toString() ))
        {
            RuntimeEventChannel<Long> cpuHeaderRawChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( "debug.cpu.header.conv", unitLong, "" );

            runtimeEventAcceptor.acceptEventMicros( convertedTimestampInMicros,
                                                    cpuHeaderRawChannel,
                                                    null,
                                                    new Long( nextMetaDataItem.getValue() ) );

            cpuHeaderRawChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "debug.cpu.header.noconv",
                                                                                       unitLong,
                                                                                       "" );

            runtimeEventAcceptor.acceptEventMicros( header.getTimestamp(),
                                                    cpuHeaderRawChannel,
                                                    null,
                                                    new Long( nextMetaDataItem.getValue() ) );
        }
    }

    @Override
    public void onReset()
    {
        loadedFile = null;
        chunkStartTime = null;
        chunkEndTime = null;
        fileTimeRange = null;
        chunkTooBigLogCounter = 0;
    }

}
