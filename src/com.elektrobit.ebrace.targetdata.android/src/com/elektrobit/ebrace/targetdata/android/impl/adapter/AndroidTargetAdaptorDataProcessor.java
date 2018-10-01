/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.adapter;

import com.elektrobit.ebrace.targetdata.adapter.androidlog.AndroidLogTAProto;
import com.elektrobit.ebrace.targetdata.adapter.androidlog.AndroidLogTAProto.LogMessage;
import com.elektrobit.ebrace.targetdata.adapter.androidlog.AndroidLogTAProto.LogPayload;
import com.elektrobit.ebrace.targetdata.android.impl.common.AndroidLogParserFactory;
import com.elektrobit.ebrace.targetdata.android.impl.importer.AndroidLogLineParserAbstract;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

public class AndroidTargetAdaptorDataProcessor
{

    private AndroidLogLineParserAbstract parser = null;
    private final RuntimeEventAcceptor runtimeEventAcceptor;

    public AndroidTargetAdaptorDataProcessor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void processMessage(byte[] payload, Timestamp timestamp) throws InvalidProtocolBufferException
    {

        LogMessage message = AndroidLogTAProto.LogMessage.parseFrom( payload );

        if (parser == null)
        {
            parser = AndroidLogParserFactory.createParser( message.getHeader().getFormat(), runtimeEventAcceptor );
        }

        if (parser != null)
        {
            for (LogPayload content : message.getContentList())
            {
                parser.processLine( content.getTrace(), null, timestamp );
            }
        }

    }

}
