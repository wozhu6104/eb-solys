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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.resources.api.model.connection.ConnectionModelImpl;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.ProtoMsgCacheDataAvailableListener;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.StreamCacheImpl;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.TargetAgentMessageReader;

import test.com.elektrobit.ebrace.targetadapter.communicator.helper.ProtoMsgCacheTestHelper;

public class FileRecordedTest implements ProtoMsgCacheDataAvailableListener
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private StreamCacheImpl protoMsgCacheImpl;
    private PipedInputStream inputStream;
    private OutputStream outputStream;
    private File recordingsFolder;
    private ConnectionType connectionType;

    @Before
    public void setup() throws Exception
    {
        inputStream = new PipedInputStream();
        outputStream = new PipedOutputStream( inputStream );
        recordingsFolder = new File( "recordings" );

        connectionType = new ConnectionType()
        {

            @Override
            public String getName()
            {
                return "testName";
            }

            @Override
            public String getExtension()
            {
                return "testExtension";
            }

            @Override
            public int getDefaultPort()
            {
                return 1234;
            }
        };
    }

    @Test
    public void recordingsFileWritten() throws Exception
    {
        configureAndStartProtoMsgCache( new ConnectionModelImpl( "Connection",
                                                                 "127.0.0.1",
                                                                 1234,
                                                                 true,
                                                                 connectionType,
                                                                 null,
                                                                 null ) );

        ProtoMsgCacheTestHelper.writeProtoBufMsg( outputStream );

        assertEquals( 1, recordingsFolder.listFiles().length );
    }

    private void configureAndStartProtoMsgCache(ConnectionModel connectionModel) throws IOException
    {
        protoMsgCacheImpl = new StreamCacheImpl( connectionModel,
                                                 new TargetAgentMessageReader(),
                                                 testFolder.newFile().getPath(),
                                                 this );
        protoMsgCacheImpl.start( inputStream );
    }

    @Test
    public void recordingFileNotWritten() throws Exception
    {
        configureAndStartProtoMsgCache( new ConnectionModelImpl( "Connection",
                                                                 "127.0.0.1",
                                                                 1234,
                                                                 false,
                                                                 null,
                                                                 null,
                                                                 null ) );

        ProtoMsgCacheTestHelper.writeProtoBufMsg( outputStream );

        assertFalse( "Expecting recordings folder only if files are written.", recordingsFolder.exists() );
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
