/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetadapter.communicator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.resources.api.model.connection.ConnectionModelImpl;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.ProtoMsgCacheDataAvailableListener;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.ProtoMsgContainer;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.StreamCacheImpl;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.TargetAgentMessageReader;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;

import test.com.elektrobit.ebrace.targetadapter.communicator.helper.ProtoMsgCacheTestHelper;

public class StreamCacheTest implements ProtoMsgCacheDataAvailableListener
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private File file;
    private StreamCacheImpl protoMsgCacheImpl;
    private PipedInputStream inputStream;
    private PipedOutputStream outputStream;

    @Before
    public void setup() throws Exception
    {
        file = testFolder.newFile();
        protoMsgCacheImpl = new StreamCacheImpl( new ConnectionModelImpl( "Connection",
                                                                          "127.0.0.1",
                                                                          1234,
                                                                          false,
                                                                          null,
                                                                          null,
                                                                          null ),
                                                 new TargetAgentMessageReader(),
                                                 file.getPath(),
                                                 this );
        inputStream = new PipedInputStream();
        outputStream = new PipedOutputStream( inputStream );
    }

    @Test
    public void noMsgIfNotStarted() throws Exception
    {
        assertNull( protoMsgCacheImpl.getNextMsg() );
    }

    @Test
    public void writeBeforeStarting() throws Exception
    {
        Header header = ProtoMsgCacheTestHelper.createProtoBufHeader();
        ProtoMsgCacheTestHelper.writeProtoBufMsg( outputStream );

        protoMsgCacheImpl.start( inputStream );

        triggerThreadChange();

        ProtoMsgContainer resultMsg = (ProtoMsgContainer)protoMsgCacheImpl.getNextMsg();

        assertEquals( header, resultMsg.getHeader() );
        assertArrayEquals( new byte[1], resultMsg.getContent() );
    }

    private void triggerThreadChange() throws InterruptedException
    {
        Thread.sleep( 25 );
    }

    @Test
    public void writeAfterStarting() throws Exception
    {
        protoMsgCacheImpl.start( inputStream );

        Header header = ProtoMsgCacheTestHelper.createProtoBufHeader();
        ProtoMsgCacheTestHelper.writeProtoBufMsg( outputStream );

        triggerThreadChange();

        ProtoMsgContainer resultMsg = (ProtoMsgContainer)protoMsgCacheImpl.getNextMsg();

        assertEquals( header, resultMsg.getHeader() );
        assertArrayEquals( new byte[1], resultMsg.getContent() );
    }

    @Test
    public void stopBetweenWriting() throws Exception
    {
        Header header = ProtoMsgCacheTestHelper.createProtoBufHeader();

        protoMsgCacheImpl.start( inputStream );

        outputStream.write( header.toByteArray().length );

        protoMsgCacheImpl.stop();

        protoMsgCacheImpl.start( inputStream );

        outputStream.write( header.toByteArray() );
        outputStream.write( new byte[1] );

        triggerThreadChange();

        ProtoMsgContainer resultMsg = (ProtoMsgContainer)protoMsgCacheImpl.getNextMsg();

        assertNull( "Expecting null if message was not complete, e.g. on disconnect.", resultMsg );
    }

    @After
    public void cleanup() throws Exception
    {
        protoMsgCacheImpl.stop();
        inputStream.close();
        outputStream.close();
        ProtoMsgCacheTestHelper.deleteRecordingsFolder();
    }

    @Override
    public void onNewDataInCacheAvailable()
    {
    }
}
