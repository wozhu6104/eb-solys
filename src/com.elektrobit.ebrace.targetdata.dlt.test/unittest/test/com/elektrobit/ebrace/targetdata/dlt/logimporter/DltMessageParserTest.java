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
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.BytesFromStreamReaderImpl;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageParseException;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageWithStorageHeaderParser;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;

public class DltMessageParserTest
{

    private DltMessage parseDltMessage(String hexString) throws Exception
    {
        byte[] decimalsString = HexStringHelper.hexStringToByteArray( hexString );
        InputStream stream = new ByteArrayInputStream( decimalsString );
        DltMessageWithStorageHeaderParser dltMessageParser = new DltMessageWithStorageHeaderParser( Mockito
                .mock( DltChannelFromLogInfoCreator.class ) );
        BytesFromStreamReaderImpl readerImpl = new BytesFromStreamReaderImpl( stream );
        return dltMessageParser.readNextMessage( readerImpl );
    }

    @Test
    public void parseValidVerboseMessage() throws Exception
    {
        DltMessage msg = parseDltMessage( "444c5401a3af1356b4c70e204545764d3d20202d4545764d202020202020202041012353595353595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertNotNull( msg );
        assertNotNull( msg.getExtendedHeader() );
        assertNotNull( msg.getStandardHeader() );
        assertEquals( "EEvM", msg.getStandardHeader().getECUId() );
        assertEquals( 53897628, msg.getStandardHeader().getTimeStamp() );
        assertEquals( "#SYS", msg.getExtendedHeader().getApplicationId() );
        assertEquals( "SYST", msg.getExtendedHeader().getContextId() );
        assertEquals( "DLT_TYPE_LOG", msg.getExtendedHeader().getMessageType() );
        assertEquals( 1, msg.getExtendedHeader().getNumberOfArguments() );
        assertNotNull( msg.getPayload() );
    }

    @Test(expected = DltMessageParseException.class)
    public void parseValidNonExtHeaderMessage() throws Exception
    {

        parseDltMessage( "444c5401f2ae1356bc220f204545764d3c3a20144545764d2020201301879912222d2020" );
    }

}
