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

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltExtendedHeader;

public class DltExtendedHeaderTest
{
    @Test
    public void parseLogTypeTrace() throws Exception
    {
        DltExtendedHeader extendedHeader = parseDltExtendedMessageHeader( "43012353595353595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertEquals( "DLT_TYPE_APP_TRACE", extendedHeader.getMessageType() );
    }

    @Test
    public void parseLogTypeNwTrace() throws Exception
    {
        DltExtendedHeader extendedHeader = parseDltExtendedMessageHeader( "45012353595353595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertEquals( "DLT_TYPE_NW_TRACE", extendedHeader.getMessageType() );
    }

    @Test
    public void parseLogTypeControl() throws Exception
    {
        DltExtendedHeader extendedHeader = parseDltExtendedMessageHeader( "47012353595353595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertEquals( "DLT_TYPE_CONTROL", extendedHeader.getMessageType() );
    }

    @Test
    public void numberOfArguments() throws Exception
    {
        DltExtendedHeader extendedHeader = parseDltExtendedMessageHeader( "43032353595353595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertEquals( 3, extendedHeader.getNumberOfArguments() );
    }

    @Test
    public void applicationID() throws Exception
    {
        DltExtendedHeader extendedHeader = parseDltExtendedMessageHeader( "43035241434553595354200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertEquals( "RACE", extendedHeader.getApplicationId() );
    }

    @Test
    public void contextID() throws Exception
    {
        DltExtendedHeader extendedHeader = parseDltExtendedMessageHeader( "43035241434552414345200220200d204d342053544152544544212120444c5401a3af1356b4c70e204545764d3d2020384545764d2020200920200292410123415352415352322002202018205b5357435f4d4e5d5357432053687574646f776e52657120444c5401a3af1356b4c70e204545764d3d0120394545764d202020092020" );
        assertEquals( "RACE", extendedHeader.getContextId() );
    }

    @Test
    public void setMessageType()
    {
        DltExtendedHeader header = new DltExtendedHeader();
        header.setVerbose( true );
        header.setMessageType( 0x03 );
        header.setNumberOfArguments( (short)1 );
        header.setApplicationId( "AB1" );
        header.setContextId( "BC1" );
    }

    private DltExtendedHeader parseDltExtendedMessageHeader(String extHeader) throws Exception
    {
        byte[] decimalsString = HexStringHelper.hexStringToByteArray( extHeader );
        return new DltExtendedHeader( decimalsString );
    }

}
