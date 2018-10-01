/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.internal.racefile;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebrace.targetdata.importer.internal.racefile.RaceFileData;
import com.elektrobit.ebrace.targetdata.importer.internal.racefile.RaceStreamScanner;

public class RaceStreamScannerTest
{
    @SuppressWarnings("unused")
    @Test
    public void countMessagesTest() throws Exception
    {
        RaceStreamScanner scanner = new RaceStreamScanner( createDummyFile() );

        int count = 0;
        RaceFileData nextHeader = null;
        while ((nextHeader = scanner.next()) != null)
        {
            count++;
        }

        Assert.assertEquals( 10000, count );
    }

    private File createDummyFile()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken() );
        for (int i = 0; i < 10000; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp, MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN, new byte[0] );
        }

        return builder.createFile( fileName );
    }
}
