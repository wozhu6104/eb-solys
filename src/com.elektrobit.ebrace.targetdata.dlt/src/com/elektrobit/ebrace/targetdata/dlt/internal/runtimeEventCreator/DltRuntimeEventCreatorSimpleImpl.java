/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.runtimeEventCreator;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.common.utils.StringHelper;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonChannel;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.targetdata.dlt.api.Measurement;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcCpuEntry;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcMemEntry;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltExtendedHeader;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStandardHeader;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;

public class DltRuntimeEventCreatorSimpleImpl implements DltRuntimeEventCreator
{
    private final TimestampProvider tsProvider;
    private final JsonEventHandler jsonEventHandler;

    public DltRuntimeEventCreatorSimpleImpl(TimestampProvider tsProvider, JsonEventHandler jsonEventHandler)
    {
        this.tsProvider = tsProvider;
        this.jsonEventHandler = jsonEventHandler;
    }

    @Override
    public void createRawDltMsg(DltMessage dltMsg)
    {
        JsonChannel jsonChannel = createOrGetJsonChannel( dltMsg.getExtendedHeader() );

        DltStandardHeader standardHeader = dltMsg.getStandardHeader();

        Timestamp timestamp = tsProvider.getHostTimestampCreator().create( standardHeader.getTimeStamp() );

        JsonEvent jsonEvent = new JsonEvent( timestamp.getTimeInMillis()
                * 1000, jsonChannel, dltMsg.constructJsonEventValue(), null, null );
        jsonEventHandler.handle( jsonEvent );
    }

    @Override
    public void createTaggedDltMsg(DltMessage dltMsg, RuntimeEventTag tag, String description)
    {
    }

    @Override
    public void createCpuUsageDltMsg(Measurement<ProcCpuEntry> cpuResults, String channelPrefix, int sessionID)
    {
    }

    @Override
    public void createCpuUsageDltMsg(Measurement<ProcCpuEntry> cpuResults)
    {
    }

    @Override
    public void createMemUsageDltMsg(Measurement<ProcMemEntry> memResults, String channelPrefix, int sessionID)
    {
    }

    @Override
    public void createMemUsageDltMsg(Measurement<ProcMemEntry> memResults)
    {
    }

    private JsonChannel createOrGetJsonChannel(DltExtendedHeader header)
    {
        final String messageType = StringHelper.extractLast( header.getMessageType(), "_" );
        String channelName = "trace.dlt." + messageType + "." + header.getApplicationId() + "." + header.getContextId();
        if (messageType.equals( "CONTROL" ))
        {
            channelName = "trace.dlt." + messageType;
        }

        return new JsonChannel( channelName, "", null );
    }

}
