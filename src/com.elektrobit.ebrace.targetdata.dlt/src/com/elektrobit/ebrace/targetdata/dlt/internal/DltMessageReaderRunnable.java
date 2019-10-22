/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal;

import java.io.ByteArrayInputStream;

import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;

public class DltMessageReaderRunnable implements Runnable
{
    DltMessageProcessor messageProcessor;
    MessageReader<DltMessage> parser;
    byte[] dltMessageBuffer;
    int length;

    public DltMessageReaderRunnable(DltMessageProcessor _messageProcessor, MessageReader<DltMessage> _parser,
            byte[] _dltMessageBuffer)
    {
        messageProcessor = _messageProcessor;
        parser = _parser;
        dltMessageBuffer = _dltMessageBuffer;
    }

    @Override
    public void run()
    {
        DltMessage dltMsg = null;
        BytesFromStreamReaderImpl reader = new BytesFromStreamReaderImpl( new ByteArrayInputStream( dltMessageBuffer ) );

        dltMsg = parser.readNextMessage( reader );
        messageProcessor.processMessage( dltMsg );
    }

}
