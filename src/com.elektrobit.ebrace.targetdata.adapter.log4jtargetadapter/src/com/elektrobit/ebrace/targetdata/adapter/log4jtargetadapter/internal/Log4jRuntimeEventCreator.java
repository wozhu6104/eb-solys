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
import com.elektrobit.ebrace.targetdata.adapter.log4j.Log4jTAProto.LogData;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class Log4jRuntimeEventCreator
{
    public RuntimeEventAcceptor runtimeEventAcceptor;

    public static void createRuntimeEvent(LogData parsedMessage, Timestamp timestamp,
            RuntimeEventAcceptor runtimeEventAcceptor)
    {
        RuntimeEventChannel<String> channel = runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( "trace.log4j." + parsedMessage.getLogLevel(), Unit.TEXT, "log4j data" );

        runtimeEventAcceptor.acceptEventMicros( timestamp.getTimeInMillis() * 1000,
                                                channel,
                                                null,
                                                parsedMessage.getTrace() );
    }
}
