/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.log4jtargetadapter.internal;

import com.elektrobit.ebrace.common.utils.JsonHelper;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonChannel;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventEdge;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebrace.targetdata.adapter.log4j.Log4jTAProto.LogData;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Log4jRuntimeEventCreator
{
    private final static String IPC_IDENTIFIER = "ipc";
    private final static String EDGE_IDENTIFIER = "edge";

    public static void createRuntimeEvent(LogData parsedMessage, Timestamp timestamp,
            RuntimeEventAcceptor runtimeEventAcceptor, JsonEventHandler jsonEventHandler)
    {
        // handle message as IPC call, if logger starts with 'ipc' and payload is valid json
        if (parsedMessage.getLogLevel().startsWith( IPC_IDENTIFIER ) && JsonHelper.isJson( parsedMessage.getTrace() ))
        {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject orig = parser.parse( parsedMessage.getTrace() ).getAsJsonObject();

            if (orig.get( EDGE_IDENTIFIER ) != null)
            {
                JsonEventEdge eventEdge = gson.fromJson( orig.get( EDGE_IDENTIFIER ), JsonEventEdge.class );

                JsonObject details = new JsonObject();
                orig.entrySet().stream().filter( e -> !e.getKey().equals( EDGE_IDENTIFIER ) )
                        .forEach( e -> details.add( e.getKey(), e.getValue() ) );

                JsonEventValue value = new JsonEventValue( makeSummaryHeader( eventEdge ) + details.toString(),
                                                           details );
                JsonEvent evt = new JsonEvent( timestamp.getTimeInMillis()
                        * 1000, new JsonChannel( parsedMessage.getLogLevel(), "", null ), value, null, eventEdge );

                jsonEventHandler.handle( evt );
            }
            else
            {
                handleAsPlainText( parsedMessage, timestamp, runtimeEventAcceptor );
            }

        }
        else
        {
            handleAsPlainText( parsedMessage, timestamp, runtimeEventAcceptor );
        }
    }

    private static String makeSummaryHeader(JsonEventEdge edge)
    {
        switch (edge.getType())
        {
            case "request" :
                return "-> ";
            case "response" :
                return "<- ";
            case "broadcast" :
                return "<-- ";
            default :
                return "? ";
        }
    }

    private static void handleAsPlainText(LogData parsedMessage, Timestamp timestamp,
            RuntimeEventAcceptor runtimeEventAcceptor)
    {
        RuntimeEventChannel<String> channel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "trace.log4j."
                + parsedMessage.getLogLevel(), Unit.TEXT, "log4j data" );

        runtimeEventAcceptor
                .acceptEventMicros( timestamp.getTimeInMillis() * 1000, channel, null, parsedMessage.getTrace() );
    }

}
