/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt.logimporter;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.dbusmsgtoprotomsg.DbusMsgToProtoMsg;

public class DltSegmentedNetworkMessageTest
{
    @Test
    public void parseSegmentedMessageTest() throws Exception
    {
        DbusMsgToProtoMsg parser = new DbusMsgToProtoMsg( null, null, null );

        List<String> nwMsgHeader = Arrays.asList( "NWST", "364640", "2062", "3", "1024" );

        List<String> nwMsgPart1 = Arrays
                .asList( "NWCH",
                         "364640",
                         "0",
                         "6c 01 00 01 ee 06 00 00 0f 02 00 00 0e 01 00 00 01 01 6f 00 3e 00 00 00 2f 6f 72 67 2f 67 65 6e 69 76 69 2f 6e 61 76 69 67 61 74 69 6f 6e 2f 69 63 6f 6e 68 61 6e 64 6c 65 72 2f 49 63 6f 6e 68 61 6e 64 6c 65 72 45 78 74 2f 4d 61 69 6e 49 6e 73 74 61 6e 63 65 00 00 06 01 73 00 3d 00 00 00 6f 72 67 2e 67 65 6e 69 76 69 2e 6e 61 76 69 67 61 74 69 6f 6e 2e 69 63 6f 6e 68 61 6e 64 6c 65 72 2e 49 63 6f 6e 68 61 6e 64 6c 65 72 45 78 74 2e 4d 61 69 6e 49 6e 73 74 61 6e 63 65 00 00 00 02 01 73 00 30 00 00 00 6f 72 67 2e 67 65 6e 69 76 69 2e 6e 61 76 69 67 61 74 69 6f 6e 2e 69 63 6f 6e 68 61 6e 64 6c 65 72 2e 49 63 6f 6e 68 61 6e 64 6c 65 72 45 78 74 00 00 00 00 00 00 00 00 03 01 73 00 0b 00 00 00 67 65 74 50 6f 69 49 63 6f 6e 73 00 00 00 00 00 08 01 67 00 10 61 28 69 28 69 69 69 62 29 61 28 75 75 29 73 29 00 00 00 07 01 73 00 05 00 00 00 3a 31 2e 32 32 00 00 00 e6 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 36 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 37 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 38 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 39 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 61 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 62 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 63 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00" );

        List<String> nwMsgPart2 = Arrays
                .asList( "NWCH",
                         "364640",
                         "1",
                         "00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 64 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 31 65 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 36 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 37 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 38 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 39 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 61 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 62 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 63 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66 66 66 66 30 30 30 30 30 30 30 30 30 30 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 41 00 00 00 50 6f 69 49 63 6f 6e 49 44 5b 5d 30 30 30 30 30 30 30 31 30 30 32 64 30 30 30 31 30 30 30 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 66 66 66 66 66" );
        List<String> nwMsgPart3 = Arrays.asList( "NWCH", "364640", "2", "66 66 66 30 30 30 30 30 30 30 30 30 30 00" );

        storeAndParse( parser, nwMsgHeader );
        storeAndParse( parser, nwMsgPart1 );
        storeAndParse( parser, nwMsgPart2 );

        assertTrue( storeAndParse( parser, nwMsgPart3 ) );

    }

    private boolean storeAndParse(DbusMsgToProtoMsg parser, List<String> header)
    {
        DltMessage headerMsg = new DltMessage();
        headerMsg.setPayload( header );
        return parser.parseDbusMessage( headerMsg );
    }
}
