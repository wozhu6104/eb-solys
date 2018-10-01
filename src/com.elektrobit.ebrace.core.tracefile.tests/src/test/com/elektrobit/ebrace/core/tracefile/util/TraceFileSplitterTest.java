/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.tracefile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.tracefile.util.TraceFileSplitter;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;

public class TraceFileSplitterTest
{
    private static final int CHUNK_SIZE = 115;
    private final String PATH = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "file.bin";
    final List<ByteArrayOutputStream> listOutputStreamsOfChunks = new ArrayList<ByteArrayOutputStream>();

    @Test
    public void testSplitFileIntoChunks() throws Exception
    {
        listOutputStreamsOfChunks.clear();
        ByteArrayOutputStream outputStreamToBeSplitted = new ByteArrayOutputStream();

        byte[] messageToWrite = createMessage( MessageType.MSG_TYPE_DBUS, 115 );
        byte[] messageToWrite1 = createMessage( MessageType.MSG_TYPE_DBUS, 115 );
        byte[] messageToWrite2 = createMessage( MessageType.MSG_TYPE_DBUS, 77 );
        byte[] messageToWrite3 = createMessage( MessageType.MSG_TYPE_DBUS, 78 );
        byte[] messageToWrite4 = createMessage( MessageType.MSG_TYPE_DBUS, 45 );
        byte[] messageToWrite5 = createMessage( MessageType.MSG_TYPE_DBUS, 10 );

        outputStreamToBeSplitted.write( messageToWrite );
        outputStreamToBeSplitted.write( messageToWrite1 );
        outputStreamToBeSplitted.write( messageToWrite2 );
        outputStreamToBeSplitted.write( messageToWrite3 );
        outputStreamToBeSplitted.write( messageToWrite4 );
        outputStreamToBeSplitted.write( messageToWrite5 );

        ByteArrayInputStream inputStreamToBeSplitted = new ByteArrayInputStream( outputStreamToBeSplitted
                .toByteArray() );

        SimpleFileWriterMocked mockedSimpleFileWriter = new SimpleFileWriterMocked();
        UserMessageLogger mockedUserMessageLogger = Mockito.mock( UserMessageLogger.class );

        TraceFileSplitter sut = new TraceFileSplitter( mockedSimpleFileWriter, CHUNK_SIZE, mockedUserMessageLogger );

        sut.splitFileIntoChunks( inputStreamToBeSplitted, PATH );

        Mockito.verify( mockedUserMessageLogger ).logUserMessage( Mockito.eq( UserMessageLoggerTypes.INFO ),
                                                                  Mockito.anyString() );
        verifyChunkSize( outputStreamToBeSplitted, listOutputStreamsOfChunks );
        verifyChunkContent( outputStreamToBeSplitted.toByteArray(), listOutputStreamsOfChunks );
    }

    @Test
    public void testSplitCorruptedFile() throws Exception
    {
        listOutputStreamsOfChunks.clear();

        byte[] messageToWrite = createMessage( MessageType.MSG_TYPE_DBUS, 115 );
        byte[] messageToWrite1 = createMessage( MessageType.MSG_TYPE_DBUS, 115 );
        byte[] messageToWrite2 = createMessage( MessageType.MSG_TYPE_DBUS, 77 );
        byte[] messageToWrite3 = createMessage( MessageType.MSG_TYPE_DBUS, 78 );
        byte[] messageToWrite4 = createMessage( MessageType.MSG_TYPE_DBUS, 45 );
        byte[] messageToWrite5 = createMessage( MessageType.MSG_TYPE_DBUS, 50 );

        ByteArrayOutputStream outputStreamToBeSplitted = new ByteArrayOutputStream();
        outputStreamToBeSplitted.write( messageToWrite );
        outputStreamToBeSplitted.write( messageToWrite1 );
        outputStreamToBeSplitted.write( messageToWrite2 );
        outputStreamToBeSplitted.write( messageToWrite3 );
        outputStreamToBeSplitted.write( messageToWrite4 );
        outputStreamToBeSplitted.write( messageToWrite5, 0, 10 ); // last message is present only partially

        ByteArrayOutputStream expectedStream = new ByteArrayOutputStream();
        expectedStream.write( messageToWrite );
        expectedStream.write( messageToWrite1 );
        expectedStream.write( messageToWrite2 );
        expectedStream.write( messageToWrite3 );
        expectedStream.write( messageToWrite4 );

        ByteArrayInputStream inputStreamToBeSplitted = new ByteArrayInputStream( outputStreamToBeSplitted
                .toByteArray() );

        SimpleFileWriterMocked mockedSimpleFileWriter = new SimpleFileWriterMocked();
        UserMessageLogger mockedUserMessageLogger = Mockito.mock( UserMessageLogger.class );

        TraceFileSplitter sut = new TraceFileSplitter( mockedSimpleFileWriter, CHUNK_SIZE, mockedUserMessageLogger );

        sut.splitFileIntoChunks( inputStreamToBeSplitted, PATH );

        Mockito.verify( mockedUserMessageLogger ).logUserMessage( Mockito.eq( UserMessageLoggerTypes.WARNING ),
                                                                  Mockito.anyString() );
        verifyChunkSize( expectedStream, listOutputStreamsOfChunks );
        verifyChunkContent( expectedStream.toByteArray(), listOutputStreamsOfChunks );
    }

    private void verifyChunkSize(ByteArrayOutputStream outputStreamToBeSplitted,
            List<ByteArrayOutputStream> listOutputStreamsOfChunks)
    {
        int totalSizeOfChunks = 0;

        for (ByteArrayOutputStream chunk : listOutputStreamsOfChunks)
        {
            totalSizeOfChunks += chunk.size();
        }

        Assert.assertEquals( outputStreamToBeSplitted.size(), totalSizeOfChunks );
    }

    private void verifyChunkContent(byte[] initialStreamToBeSplitted,
            List<ByteArrayOutputStream> listOutputStreamsOfChunks) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (ByteArrayOutputStream streamOfOneChunk : listOutputStreamsOfChunks)
        {
            out.write( streamOfOneChunk.toByteArray() );
        }

        Assert.assertArrayEquals( initialStreamToBeSplitted, out.toByteArray() );
    }

    private byte[] createMessage(MessageType messageType, int payloadLength) throws IOException
    {
        ByteArrayOutputStream messageToWrite = new ByteArrayOutputStream();
        Header header = createHeader( messageType, payloadLength );
        byte headerLengthToWrite = (byte)header.getSerializedSize();
        messageToWrite.write( headerLengthToWrite );
        header.writeTo( messageToWrite );
        byte[] testbytes = new byte[payloadLength];
        new Random().nextBytes( testbytes );
        messageToWrite.write( testbytes );
        return messageToWrite.toByteArray();
    }

    private Header createHeader(MessageType type, int payloadLength)
    {
        Header.Builder builder = Header.newBuilder();
        builder.setType( type );
        builder.setLength( payloadLength );
        builder.setTimestamp( System.currentTimeMillis() );
        builder.setVersionToken( VersionHandler.getVersionToken() );
        return builder.build();
    }

    private class SimpleFileWriterMocked extends SimpleFileWriter
    {
        private boolean closed = false;

        @Override
        public void startNewFile(String fullPath, String filename)
        {
            Assert.assertFalse( "Stream should not be closed", closed );
            File expectedLocationOfChunk = new File( PATH );
            File locationOfChunk = new File( fullPath );
            Assert.assertTrue( expectedLocationOfChunk.getParent().startsWith( locationOfChunk.getParent() ) );
        }

        @Override
        public void closeStream()
        {
            closed = true;
            Assert.assertFalse( "stream not closed", !closed );
        }

        @Override
        public void writeBytes(byte[] dataToWrite)
        {
            Assert.assertFalse( "stream closed too soon", closed );
            ByteArrayOutputStream outputStreamOfChunk = new ByteArrayOutputStream();
            try
            {
                outputStreamOfChunk.write( dataToWrite );
                listOutputStreamsOfChunks.add( outputStreamOfChunk );
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Assert.fail( "IOException" );
            }
        }
    }
}
