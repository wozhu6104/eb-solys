/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetadapter.communicator.helper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetagent.protocol.frame.TargetAgentProtocolFrame.Header;

public class ProtoMsgCacheTestHelper
{
    public static Header createProtoBufHeader()
    {
        Header header = Header.newBuilder().setLength( 1 ).setTimestamp( 1000 ).setType( MessageType.MSG_TYPE_DBUS )
                .setVersionToken( 4915 ).build();
        return header;
    }

    public static void writeProtoBufMsg(OutputStream outputStream) throws IOException
    {
        Header header = createProtoBufHeader();

        outputStream.write( header.toByteArray().length );
        outputStream.write( header.toByteArray() );
        outputStream.write( new byte[1] );
        outputStream.flush();
    }

    public static void deleteRecordingsFolder()
    {
        File recordingsFolder = new File( "recordings" );
        if (recordingsFolder.exists())
        {
            if (recordingsFolder.isDirectory())
            {
                for (File recordingsFile : recordingsFolder.listFiles())
                {
                    recordingsFile.delete();
                }
            }
            recordingsFolder.delete();
        }
    }
}
