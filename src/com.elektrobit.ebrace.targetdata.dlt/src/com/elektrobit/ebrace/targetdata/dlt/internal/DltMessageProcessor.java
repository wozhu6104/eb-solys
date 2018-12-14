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

import java.util.List;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebrace.targetdata.dlt.api.Measurement;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcCpuEntry;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcMemEntry;
import com.elektrobit.ebrace.targetdata.dlt.internal.dbusmsgtoprotomsg.DbusMsgToProtoMsg;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.DltProcPayloadParser;
import com.elektrobit.ebrace.targetdata.dlt.internal.runtimeEventCreator.DltRuntimeEventCreator;
import com.elektrobit.ebrace.targetdata.dlt.internal.runtimeEventCreator.DltRuntimeEventCreatorImpl;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class DltMessageProcessor
{
    private final DltRuntimeEventCreator runtimeEventCreator;
    private final DltProcPayloadParser procParser;
    private final DbusMsgToProtoMsg dbusParser;

    public DltMessageProcessor(String filename, long targetLength, TimestampProvider timestampProvider,
            TimeMarkerManager timeMarkerManager, ProtocolMessageDispatcher protocolMessageDispatcher,
            RuntimeEventAcceptor runtimeEventAcceptor)
    {
        DltSegmentedNetworkMessage.clear();
        runtimeEventCreator = new DltRuntimeEventCreatorImpl( timestampProvider,
                                                              runtimeEventAcceptor,
                                                              timeMarkerManager );
        procParser = new DltProcPayloadParser();
        dbusParser = new DbusMsgToProtoMsg( timestampProvider, protocolMessageDispatcher, filename );
    }

    public void processMessage(DltMessage dltMsg)
    {
        if (dltMsg != null)
        {
            parsePayload( runtimeEventCreator, dltMsg, procParser, dbusParser );
        }
        else
        {
            List<DltMessage> tokens = DltSegmentedNetworkMessage.queryRemainingData();
            tokens.forEach( token -> runtimeEventCreator
                    .createTaggedDltMsg( token, RuntimeEventTag.ERROR, "Network message incomplete" ) );
            DltSegmentedNetworkMessage.clear();
        }
    }

    private void parsePayload(DltRuntimeEventCreator runtimeEventCreator, DltMessage dltMsg,
            DltProcPayloadParser procParser, DbusMsgToProtoMsg dbusParser)
    {
        DltMessageType messageType = DltMessageTypeInterpreter.getMessageType( dltMsg );
        switch (messageType)
        {
            case DLT_MESSAGE_TYPE_CPU_INFO :
                parseCpuInfo( runtimeEventCreator, dltMsg, procParser );
                break;
            case DLT_MESSAGE_TYPE_MEM_INFO :
                parseMemInfo( runtimeEventCreator, dltMsg, procParser );
                break;
            case DLT_MESSAGE_TYPE_DBUS : {
                parseDbus( runtimeEventCreator, dltMsg, dbusParser );
            }
                break;
            case DLT_MESSAGE_TYPE_OTHER : {
                runtimeEventCreator.createRawDltMsg( dltMsg );
            }
                break;

            default :
                break;
        }
    }

    private void parseDbus(DltRuntimeEventCreator runtimeEventCreator, DltMessage dltMsg, DbusMsgToProtoMsg dbusParser)
    {
        if (!dbusParser.parseDbusMessage( dltMsg ))
        {
            dumpUnparsedMessage( runtimeEventCreator, dltMsg );
        }
        else
        {
            disposeNwMessage( dltMsg );
        }
    }

    public void dumpUnparsedMessage(DltRuntimeEventCreator runtimeEventCreator, DltMessage dltMsg)
    {
        List<DltMessage> tokens = DltSegmentedNetworkMessage.retrieveAllTokens( dltMsg );
        if (tokens != null)
        {
            tokens.forEach( token -> runtimeEventCreator
                    .createTaggedDltMsg( token,
                                         RuntimeEventTag.ERROR,
                                         "number of bytes expected is greater than number of bytes received" ) );
        }
        DltSegmentedNetworkMessage.disposeMessage( dltMsg );
    }

    public void disposeNwMessage(DltMessage dltMsg)
    {
        if (DltSegmentedNetworkMessage.isNetworkMessage( dltMsg ) && DltSegmentedNetworkMessage.isLastToken( dltMsg ))
        {
            DltSegmentedNetworkMessage.disposeMessage( dltMsg );
        }
    }

    private void parseMemInfo(DltRuntimeEventCreator runtimeEventCreator, DltMessage dltMsg,
            DltProcPayloadParser procParser)
    {
        Measurement<ProcMemEntry> memResults = procParser.parseMemData( dltMsg );

        if (memResults != null)
        {
            runtimeEventCreator.createMemUsageDltMsg( memResults,
                                                      "session:" + dltMsg.getStandardHeader().getSessionId() + "",
                                                      dltMsg.getStandardHeader().getSessionId() );

        }
    }

    private void parseCpuInfo(DltRuntimeEventCreator runtimeEventCreator, DltMessage dltMsg,
            DltProcPayloadParser procParser)
    {
        Measurement<ProcCpuEntry> cpuResults = procParser.parseCpuData( dltMsg );
        if (cpuResults != null)
        {
            runtimeEventCreator.createCpuUsageDltMsg( cpuResults,
                                                      "session:" + dltMsg.getStandardHeader().getSessionId() + "",
                                                      dltMsg.getStandardHeader().getSessionId() );
        }
    }
}
