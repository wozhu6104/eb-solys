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

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.BytesFromStreamReaderImpl;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageType;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageTypeInterpreter;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageWithStorageHeaderParser;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;

public class DltMessageTypeInterpreterTest
{

    @Test
    public void messageTypeerTest() throws Exception
    {
        DltMessage dltMsg = parseDltMessage( "444c5401a3af1356b4c70e204545764d3d20202d4545764d202020202020202041012353595353595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );

        assertEquals( DltMessageType.DLT_MESSAGE_TYPE_OTHER, DltMessageTypeInterpreter.getMessageType( dltMsg ) );
    }

    @Test
    public void messageTypeCpuTest() throws Exception
    {
        DltMessage dltMsg = parseDltMessage( "00444C54015D040E586A370B00454576413DBB013945457641000000DE000189A241035359530050524F432300000001000000000200000500737461740000020000060131202873797374656D642920532030203120312030202D3120343139343536302034343138203135383535203634203232203131203532203420383220323020302031203020342034393032393132203739372034323934393637323935203332373638203930363136382032313235363130373230203231323536303930363820313939343335353038342030203637313137333132332034303936203132363020323134383836383231322030203020313720302030203020343220302030203934313338342039393133383820393935333238203231323536313039303820323132353631" );

        assertEquals( DltMessageType.DLT_MESSAGE_TYPE_CPU_INFO, DltMessageTypeInterpreter.getMessageType( dltMsg ) );
    }

    @Test
    public void messageTypeMemTest() throws Exception
    {
        DltMessage dltMsg = parseDltMessage( "444C54015D040E5892A40D00454576413D94004E45457641000000DE00027EFF41035359530050524F432300000001000000000200000600737461746D00000200001A003134383920383339203433382032313420302037303820300A00444C54015D040E5892A40D00454576413D95004345457641000000DE00027F0041035359530050524F432300000002000000000200000600737461746D00000200000F00302030203020302030203020300A00444C54015D040E5892A40D00454576413D96004345457641000000DE00027F0141035359530050524F432300000003000000000200000600737461746D00000200000F00302030203020302030203020300A00444C54015D040E5892A40D00454576413D97004345457641000000DE00027F0241035359530050" );

        assertEquals( DltMessageType.DLT_MESSAGE_TYPE_MEM_INFO, DltMessageTypeInterpreter.getMessageType( dltMsg ) );
    }

    @Test
    public void messageTypeDbusTest() throws Exception
    {
        DltMessage dltMsg = parseDltMessage( "444C54015D040E5892A40D00454576413D94004E45457641000000DE00027EFF41035359530050524F432300000001000000000200000600737461746D00000200001A003134383920383339203433382032313420302037303820300A00444C54015D040E5892A40D00454576413D95004345457641000000DE00027F0041035359530050524F432300000002000000000200000600737461746D00000200000F00302030203020302030203020300A00444C54015D040E5892A40D00454576413D96004345457641000000DE00027F0141035359530050524F432300000003000000000200000600737461746D00000200000F00302030203020302030203020300A00444C54015D040E5892A40D00454576413D97004345457641000000DE00027F0241035359530050" );

        assertEquals( DltMessageType.DLT_MESSAGE_TYPE_MEM_INFO, DltMessageTypeInterpreter.getMessageType( dltMsg ) );
    }

    private DltMessage parseDltMessage(String hexString) throws Exception
    {
        byte[] decimalsString = HexStringHelper.hexStringToByteArray( hexString );
        InputStream stream = new ByteArrayInputStream( decimalsString );
        DltMessageWithStorageHeaderParser dltMessageParser = new DltMessageWithStorageHeaderParser( Mockito
                .mock( DltChannelFromLogInfoCreator.class ) );
        BytesFromStreamReaderImpl readerImpl = new BytesFromStreamReaderImpl( stream );
        return dltMessageParser.readNextMessage( readerImpl );

    }

}
