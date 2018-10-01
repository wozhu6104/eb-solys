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

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltControlMessageHelper;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;

public class DltControlMessageHelperTest
{
    private static final byte[] byteMessageGetSoftwareVersion = HexStringHelper
            .hexStringToByteArray( "3500001A4543550001CA8F54170141505000434F4E0013000000" );
    private static final byte[] byteMessageSetLogLevel = HexStringHelper
            .hexStringToByteArray( "350000274543550001CA8F54170541505000434F4E000100000053494E4153494E430472656D6F" );
    private static final byte[] byteMessageSetDefaultLogLevel = HexStringHelper
            .hexStringToByteArray( "3500001F4543550001CA8F54170341505000434F4E00110000000372656D6F" );
    private static final String ECU_ID = "ECU";
    private static final String APP_ID = "APP";
    private static final String CTX_ID = "CON";
    private static final String APP_ID_DEST = "SINA";
    private static final String CTX_ID_DEST = "SINC";
    private static final int TIMESTAMP = 30052180;

    @Test
    public void testCreateControlMessageGetSoftwareVersion()
    {
        DltMessage controlMessage = DltControlMessageHelper
                .createControlMessageGetSoftwareVersion( ECU_ID, APP_ID, CTX_ID, TIMESTAMP );

        byte[] marshalled = controlMessage.marshal();
        assertArrayEquals( marshalled, byteMessageGetSoftwareVersion );
    }

    @Test
    public void testCreateControlMessageSetLogLevel()
    {
        DltMessage controlMessage = DltControlMessageHelper
                .createControlMessageSetLogLevel( ECU_ID, APP_ID, CTX_ID, TIMESTAMP, APP_ID_DEST, CTX_ID_DEST, 0x04 );

        byte[] marshalled = controlMessage.marshal();
        assertArrayEquals( marshalled, byteMessageSetLogLevel );
    }

    @Test
    public void testCreateControlMessageSetDefaultLogLevel()
    {
        DltMessage controlMessage = DltControlMessageHelper
                .createControlMessageSetDefaultLogLevel( ECU_ID, APP_ID, CTX_ID, TIMESTAMP, 0x03 );

        byte[] marshalled = controlMessage.marshal();
        assertArrayEquals( marshalled, byteMessageSetDefaultLogLevel );
    }

}
