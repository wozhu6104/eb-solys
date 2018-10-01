/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.elektrobit.ebrace.protobuf.messagedefinitions.DltRawTAProto.DltRawMessage;
import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.dlt.internal.BytesFromStreamReaderImpl;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStreamMessageServiceImpl;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreatorImpl;
import com.elektrobit.ebsolys.core.targetdata.api.TimestampCreator;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.google.protobuf.InvalidProtocolBufferException;

public class DltRawAdaptor implements TargetAdapter
{
    private final DltStreamMessageServiceImpl parser = new DltStreamMessageServiceImpl( new DltChannelFromLogInfoCreatorImpl() );

    private final BytesFromStreamReader streamReader;

    private final CopyStream os = new CopyStream( 65535 );

    public DltRawAdaptor(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator,
            RuntimeEventAcceptor runtimeEventAcceptor, DataSourceContext dataSourceContext)
    {

        streamReader = new BytesFromStreamReaderImpl( os.toInputStream() );

        Thread thread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                parser.readNextMessage( streamReader );
            }
        } );
        thread.setName( "ParseDlt" );
        thread.start();
    }

    @Override
    public void onProtocolMessageReceived(Timestamp timestamp, MessageType type, byte[] payload,
            TimestampCreator timestampCreator)
    {

        if (type.equals( TargetAgentProtocolCommonDefinitions.MessageType.MSG_TYPE_DLT_RAW_PLUGIN ))
        {
            try
            {
                DltRawMessage rawMessage = DltRawMessage.parseFrom( payload );

                final byte[] bytesEncoded = Base64.decodeBase64( rawMessage.getData().getBytes() );
                os.write( bytesEncoded, 0, bytesEncoded.length );
                os.flush();
            }
            catch (InvalidProtocolBufferException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose()
    {
    }
}
