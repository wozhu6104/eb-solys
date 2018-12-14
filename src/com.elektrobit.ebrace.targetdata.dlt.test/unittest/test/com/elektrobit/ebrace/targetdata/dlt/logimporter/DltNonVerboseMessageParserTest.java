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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.BytesFromStreamReaderImpl;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;
import com.elektrobit.ebrace.targetdata.dlt.newapiimpl.DltMessageServiceImpl;

public class DltNonVerboseMessageParserTest
{

    @Test
    public void parseGetSoftwareVersionRequest() throws Exception
    {
        List<String> payload = parseDltMessage( "3500001A4543550001CA8F54160141505000434F4E0013000000" ).getPayload();
        String expectedResult = "REQUEST GetSoftwareVersion";
        assertEquals( expectedResult, payload.get( 0 ) );
    }

    @Test
    public void parseSetLogLevelRequest() throws Exception
    {
        List<String> payload = parseDltMessage( "350000274543550001CA8F54160541505000434F4E000100000053494E4153494E430472656D6F" )
                .getPayload();
        String expectedResult = "REQUEST Set_LogLevel 53 49 4E 41 53 49 4E 43 04 72 65 6D 6F";
        assertEquals( expectedResult, payload.get( 0 ) );
    }

    @Test
    public void parseSetDefaultLogLevelRequest() throws Exception
    {
        List<String> payload = parseDltMessage( "3500001F4543550001CA8F54160341505000434F4E00110000000372656D6F" )
                .getPayload();
        String expectedResult = "REQUEST Set_DefaultLogLevel 03 72 65 6D 6F";
        assertEquals( expectedResult, payload.get( 0 ) );
    }

    @Test
    public void parseSetTraceStatusResponse() throws Exception
    {
        List<String> payload = parseDltMessage( "35000020454355310a1e12bd26014441310044433100020f0000000100000000" )
                .getPayload();
        String expectedResult = "RESPONSE Set_TraceStatus 00 01 00 00 00 00";
        assertEquals( expectedResult, payload.get( 0 ) );
    }

    private DltMessage parseDltMessage(String hexString) throws Exception
    {
        byte[] decimalsString = HexStringHelper.hexStringToByteArray( hexString );
        InputStream stream = new ByteArrayInputStream( decimalsString );
        DltMessageServiceImpl dltMessageParser = new DltMessageServiceImpl( Mockito
                .mock( DltChannelFromLogInfoCreator.class ) );
        BytesFromStreamReaderImpl readerImpl = new BytesFromStreamReaderImpl( stream );
        return dltMessageParser.readNextMessage( readerImpl );
    }

}
