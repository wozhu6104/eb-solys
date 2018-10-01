/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.runtimeEventCreator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Map;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.utils.StringHelper;
import com.elektrobit.ebrace.common.utils.UnitConverter;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltExtendedHeader;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStandardHeader;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.Measurement;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.ProcCpuEntry;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.ProcMemEntry;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class DltRuntimeEventCreatorImpl implements DltRuntimeEventCreator
{
    private RuntimeEventAcceptor runtimeEventAcceptor = null;
    private int currentSessionID = 0;
    private final TimeMarkerManager timeMarker;
    private final TimestampProvider tsProvider;

    public DltRuntimeEventCreatorImpl(TimestampProvider tsProvider, RuntimeEventAcceptor runtimeEventAcceptor,
            TimeMarkerManager timeMarker)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
        this.timeMarker = timeMarker;
        this.tsProvider = tsProvider;
    }

    @Override
    public void createRawDltMsg(DltMessage dltMsg)
    {
        RuntimeEventChannel<String> channel = createOrGetChannel( dltMsg.getExtendedHeader() );

        RuntimeEvent<String> event = createRawDltMsgHelper( dltMsg, channel );

        if (dltMsg.getExtendedHeader().getMessageTypeInfo().equals( "UNDEFINED_MESSAGE_TYPE_INFO" ))
        {

            runtimeEventAcceptor.setTag( event, RuntimeEventTag.WARNING, "Message Type Info not set" );
        }
    }

    @Override
    public void createTaggedDltMsg(DltMessage dltMsg, RuntimeEventTag tag, String description)
    {
        RuntimeEventChannel<String> channel = createOrGetChannel( dltMsg.getExtendedHeader() );

        RuntimeEvent<String> event = createRawDltMsgHelper( dltMsg, channel );

        runtimeEventAcceptor.setTag( event, tag, description );
    }

    @Override
    public void createCpuUsageDltMsg(Measurement<ProcCpuEntry> cpuResults, String channelPrefix, int sessionID)
    {
        long cpuSystem = 0;
        Timestamp timestamp;
        timestamp = tsProvider.getHostTimestampCreator().create( cpuResults.getTimestamp() );
        markSessionEnd( sessionID, timestamp.getTimeInMillis() * 1000 );
        for (Map.Entry<Integer, ProcCpuEntry> entry : cpuResults.getPidToMeasurement().entrySet())
        {
            timestamp = tsProvider.getHostTimestampCreator().create( cpuResults.getTimestamp() );
            double perProcessCpuUsage = (100 * entry.getValue().getCpuUsage() / entry.getValue().getTimestamp());
            String processCpuChannelName = "cpu.proc." + channelPrefix + "." + entry.getValue().getProcName() + ":"
                    + +entry.getKey();
            RuntimeEventChannel<Double> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( processCpuChannelName, Unit.PERCENT, "" );
            runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                    channel,
                                                    null,
                                                    formatTwoDecimals( perProcessCpuUsage ) );
            cpuSystem += perProcessCpuUsage;

        }
        timestamp = tsProvider.getHostTimestampCreator().create( cpuResults.getTimestamp() );
        if (cpuResults.getPidToMeasurement().size() > 0)
        {
            RuntimeEventChannel<Double> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( "cpu.system." + channelPrefix, Unit.PERCENT, "" );
            runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                    channel,
                                                    null,
                                                    formatTwoDecimals( cpuSystem ) );
        }
    }

    public RuntimeEvent<String> createRawDltMsgHelper(DltMessage dltMsg, RuntimeEventChannel<String> channel)
    {
        DltStandardHeader standardHeader = dltMsg.getStandardHeader();

        Timestamp timestamp = tsProvider.getHostTimestampCreator().create( standardHeader.getTimeStamp() );

        return runtimeEventAcceptor
                .acceptEventMicros( timestamp.getTimeInMillis() * 1000, channel, null, dltMsg.constructJsonEvent() );
    }

    @Override
    public void createCpuUsageDltMsg(Measurement<ProcCpuEntry> cpuResults)
    {
        createCpuUsageDltMsg( cpuResults, "", 0 );
    }

    @Override
    public void createMemUsageDltMsg(Measurement<ProcMemEntry> memResults, String channelPrefix, int sessionID)
    {
        long memorySystem = 0;
        Timestamp timestamp;
        markSessionEnd( sessionID, memResults.getTimestamp() * 1000 );
        for (Map.Entry<Integer, ProcMemEntry> entry : memResults.getPidToMeasurement().entrySet())
        {
            timestamp = tsProvider.getHostTimestampCreator().create( memResults.getTimestamp() );
            long perProcessMemoryUsage = entry.getValue().getMemoryUsage();

            String newChannelName = "mem.proc." + channelPrefix + "." + entry.getValue().getProcName() + ":"
                    + entry.getKey();

            RuntimeEventChannel<Long> memoryChannel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( newChannelName, Unit.KILOBYTE, "" );

            runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                    memoryChannel,
                                                    null,
                                                    UnitConverter.convertBytesToKB( perProcessMemoryUsage ) );

            renameMemChannel( channelPrefix, entry, newChannelName );
            memorySystem += perProcessMemoryUsage;

        }
        timestamp = tsProvider.getHostTimestampCreator().create( memResults.getTimestamp() );
        if (memResults.getPidToMeasurement().size() > 0)
        {
            RuntimeEventChannel<Long> channel = runtimeEventAcceptor
                    .createOrGetRuntimeEventChannel( "mem.system." + channelPrefix, Unit.KILOBYTE, "" );
            runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                    channel,
                                                    null,
                                                    UnitConverter.convertBytesToKB( memorySystem ) );
        }
    }

    private void renameMemChannel(String channelPrefix, Map.Entry<Integer, ProcMemEntry> entry, String newChannelName)
    {
        RuntimeEventChannel<Long> oldChannel = runtimeEventAcceptor
                .getRuntimeEventChannel( "mem.proc." + channelPrefix + "." + entry.getKey() + ":" + entry.getKey() );

        if (null != oldChannel && !oldChannel.getName().equals( newChannelName ))
        {
            runtimeEventAcceptor.renameRuntimeEventChannel( oldChannel, newChannelName );
        }
    }

    @Override
    public void createMemUsageDltMsg(Measurement<ProcMemEntry> memResults)
    {
        createMemUsageDltMsg( memResults, "", 0 );
    }

    private double formatTwoDecimals(double value)
    {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator( '.' );
        DecimalFormat df = new DecimalFormat( "#.##", symbols );

        return Double.parseDouble( df.format( value ) );
    }

    private RuntimeEventChannel<String> createOrGetChannel(DltExtendedHeader header)
    {
        final String messageType = StringHelper.extractLast( header.getMessageType(), "_" );
        String channelName = "trace.dlt." + messageType + "." + header.getApplicationId() + "." + header.getContextId();
        if (messageType.equals( "CONTROL" ))
        {
            channelName = "trace.dlt." + messageType;
        }

        RuntimeEventChannel<String> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( channelName,
                                                 Unit.TEXT,
                                                 "",
                                                 Arrays.asList( "appId",
                                                                "contextId",
                                                                "numArgs",
                                                                "logLevel",
                                                                "Value" ) );
        return channel;
    }

    private void markSessionEnd(int sessionID, long timestamp)
    {
        if (currentSessionID != 0 && sessionID != currentSessionID)
        {
            timeMarker.createNewTimeMarker( timestamp ).setName( "SESSION_" + sessionID );
        }
        currentSessionID = sessionID;
    }
}
