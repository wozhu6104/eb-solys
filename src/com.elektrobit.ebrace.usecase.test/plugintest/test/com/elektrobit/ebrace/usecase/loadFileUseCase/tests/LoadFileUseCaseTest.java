/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.usecase.loadFileUseCase.tests;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.common.utils.SimpleFileWriter;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderInnerMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessage;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessageEncoding;
import com.elektrobit.ebrace.protobuf.messagedefinitions.SocketReaderTAProto.SocketReaderMessageType;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class LoadFileUseCaseTest extends UseCaseBaseTest
{
    private static final long FIRST_MESSAGE_TIMESTAMP = 10002L;
    private static final long SECOND_MESSAGE_TIMESTAMP = 10003L;
    private static final String FIRST_MESSAGE_TEXT = "test text 1";
    private static final String SECOND_MESSAGE_TEXT = "test text 2";

    private static String fileToBeDeletedAfterTest = null;
    private RuntimeEventAcceptor runtimeEventAcceptor = null;

    @Before
    public void setRuntimeEventProvider()
    {
        GenericOSGIServiceTracker<RuntimeEventAcceptor> runtimeEventAcceptorTracker = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class );
        runtimeEventAcceptor = runtimeEventAcceptorTracker.getService();
        runtimeEventAcceptor.dispose();
    }

    @Test
    public void testLoadExistingFile() throws Exception
    {
        int version = VersionHandler.getVersionToken();
        String pathToFile = createSmallFile( version );

        OpenFileInteractionCallback mockedCallback = Mockito.mock( OpenFileInteractionCallback.class );
        OpenFileInteractionUseCase sut = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( mockedCallback );

        LoadFileProgressNotifyCallback mockedProgressCallback = Mockito.mock( LoadFileProgressNotifyCallback.class );
        UseCaseFactoryInstance.get().makeLoadFileProgressNotifyUseCase( mockedProgressCallback, pathToFile );

        sut.openFile( pathToFile );

        Mockito.verify( mockedCallback ).onFileLoadingStarted( pathToFile );

        Mockito.verify( mockedProgressCallback, Mockito.atLeast( 1 ) ).onLoadFileProgressChanged( Mockito.anyInt() );
        Mockito.verify( mockedProgressCallback, Mockito.atLeast( 1 ) )
                .onLoadFileDone( Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong() );

        List<RuntimeEvent<?>> events = runtimeEventAcceptor.getAllRuntimeEvents();

        Assert.assertEquals( 2, events.size() );
        Assert.assertEquals( FIRST_MESSAGE_TEXT, events.get( 0 ).getValue() );
        Assert.assertEquals( SECOND_MESSAGE_TEXT, events.get( 1 ).getValue() );
    }

    private String createSmallFile(int procolVersion)
    {
        String currentLocation = Platform.getLocation().toOSString();
        String fileDestinationPath = currentLocation + File.separator + "testTraceFileFolder";
        String fileName = "useCaseTestTrace.bin";

        SimpleFileWriter fileWriter = new SimpleFileWriter();
        fileWriter.startNewFile( fileDestinationPath, fileName );

        writeMessage( fileWriter,
                      FIRST_MESSAGE_TIMESTAMP,
                      MessageType.MSG_TYPE_SOCKET_READER_PLUGIN,
                      getProtoMessage( FIRST_MESSAGE_TEXT ),
                      procolVersion );

        writeMessage( fileWriter,
                      SECOND_MESSAGE_TIMESTAMP,
                      MessageType.MSG_TYPE_SOCKET_READER_PLUGIN,
                      getProtoMessage( SECOND_MESSAGE_TEXT ),
                      procolVersion );
        fileWriter.closeStream();

        String smallFile = fileDestinationPath + File.separator + fileName;
        fileToBeDeletedAfterTest = smallFile;
        return smallFile;
    }

    private void writeMessage(SimpleFileWriter fileWriter, long timestamp, MessageType messageType,
            byte[] messageContentBytes, int procolVersion)
    {
        Header.Builder builder = Header.newBuilder().setTimestamp( timestamp ).setType( messageType )
                .setLength( messageContentBytes.length ).setVersionToken( procolVersion );
        Header firstHeader = builder.build();
        byte[] firstHeaderByteArray = firstHeader.toByteArray();
        byte[] lengthByteArray = new byte[]{(byte)firstHeaderByteArray.length};

        fileWriter.writeBytes( lengthByteArray );
        fileWriter.writeBytes( firstHeaderByteArray );
        fileWriter.writeBytes( messageContentBytes );
    }

    private byte[] getProtoMessage(String traceText)
    {
        final byte[] bytesEncoded = Base64.encodeBase64( traceText.getBytes() );
        final String traceMessageAsUtf8 = new String( bytesEncoded );

        SocketReaderInnerMessage.Builder innerMessageBuilder = SocketReaderInnerMessage.newBuilder();
        innerMessageBuilder.setData( traceMessageAsUtf8 );
        innerMessageBuilder.setPortNo( 0 );

        SocketReaderMessage.Builder messageBuilder = SocketReaderMessage.newBuilder();
        messageBuilder.setMessage( innerMessageBuilder );
        messageBuilder.setType( SocketReaderMessageType.SOCKET_READER_MESSAGE );
        messageBuilder.setEncoding( SocketReaderMessageEncoding.Encoding_Base64 );

        return messageBuilder.build().toByteArray();
    }

    @Test
    public void testFileTooBig() throws Exception
    {
        String pathToFile = createBigFile();

        OpenFileInteractionCallback mockedCallback = Mockito.mock( OpenFileInteractionCallback.class );
        OpenFileInteractionUseCase sut = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( mockedCallback );

        sut.openFile( pathToFile );

        Mockito.verify( mockedCallback ).onFileTooBig( pathToFile );
    }

    private String createBigFile()
    {
        String currentLocation = Platform.getLocation().toOSString();
        String fileDestinationPath = currentLocation + File.separator + "testTraceFileFolder";
        String fileName = "toobigtesttrace.bin";

        SimpleFileWriter fileWriter = new SimpleFileWriter();
        fileWriter.startNewFile( fileDestinationPath, fileName );

        int fileSizeMB = 501;
        byte[] dataToWrite = new byte[fileSizeMB * 1024];
        new Random().nextBytes( dataToWrite );

        for (int i = 0; i < 1024; i++)
        {
            fileWriter.writeBytes( dataToWrite );
        }

        String bigFile = fileDestinationPath + File.separator + fileName;
        fileToBeDeletedAfterTest = bigFile;
        return bigFile;
    }

    @After
    public void cleanup()
    {
        if (fileToBeDeletedAfterTest != null)
        {
            new File( fileToBeDeletedAfterTest ).delete();
            fileToBeDeletedAfterTest = null;
        }
    }
}
