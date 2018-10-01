/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.dlt.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltLogInfoType;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltLogInfoType.ResponseCode;

public class DltLogInfoTypeTest
{
    @Test
    public void createLogInfoMessageNoMatchingContextIDs() throws Exception
    {
        byte[] payloadWithNoData = HexStringHelper.hexStringToByteArray( "72656d6f0000" );
        DltLogInfoType dltLogInfoType = new DltLogInfoType( ResponseCode.NO_MATCHING_CTX, payloadWithNoData, true );
        assertEquals( "remo", dltLogInfoType.getComInterface() );
    }

    @Test
    public void createLogInfoMessage() throws Exception
    {
        byte[] payloadWithNoData = HexStringHelper
                .hexStringToByteArray( "010053494e41010053494e43ffff1500737464696e2061646170746f7220636f6e746578741900737464696e2061646170746f72206170706c69636174696f6e72656d6f" );
        DltLogInfoType dltLogInfoType = new DltLogInfoType( DltLogInfoType.ResponseCode.get( 7 ),
                                                            payloadWithNoData,
                                                            true );
        assertEquals( "SINA", dltLogInfoType.getChannels().get( 0 ).applicationID );
        assertEquals( "SINC", dltLogInfoType.getChannels().get( 0 ).contextID );
    }

}
