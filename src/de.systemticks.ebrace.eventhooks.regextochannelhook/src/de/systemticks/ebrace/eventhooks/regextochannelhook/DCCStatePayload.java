/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package de.systemticks.ebrace.eventhooks.regextochannelhook;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.elektrobit.ebrace.common.utils.HexStringHelper;

public class DCCStatePayload
{
    public enum DCCState {
        Inactive, Active, Init, TakeoverRequestWaiting, MinimumRiskManeuver, Unknown
    }

    private final String interfaceName;
    private final String payload;
    private DCCState state = DCCState.Unknown;
    private double driverVelocity;
    private double robotVelocity;
    private double targetVelocity;

    public DCCStatePayload(String interfaceName, String payload)
    {
        this.interfaceName = interfaceName;
        this.payload = payload;

        parseValues( payload );

    }

    private void parseValues(String payload)
    {
        String[] hexBytes = payload.split( "," );

        for (int i = 0; i < hexBytes.length; i++)
        {
            hexBytes[i] = hexBytes[i].trim();
        }

        int dccStateId = HexStringHelper.convertHexIDToUInt8( hexBytes[hexBytes.length - 25] );
        switch (dccStateId)
        {
            case 0 :
                state = DCCState.Inactive;
                break;
            case 1 :
                state = DCCState.Active;
                break;
            case 2 :
                state = DCCState.Init;
                break;
            case 3 :
                state = DCCState.TakeoverRequestWaiting;
                break;
            case 4 :
                state = DCCState.MinimumRiskManeuver;
                break;

            default :
                state = DCCState.Unknown;
                break;
        }

        // FIXME Check if between 0 and 255
        byte byte0 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 24] ) & 0xFF);
        byte byte1 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 23] ) & 0xFF);
        byte byte2 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 22] ) & 0xFF);
        byte byte3 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 21] ) & 0xFF);
        byte byte4 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 20] ) & 0xFF);
        byte byte5 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 19] ) & 0xFF);
        byte byte6 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 18] ) & 0xFF);
        byte byte7 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 17] ) & 0xFF);

        byte[] driverVeloBytes = new byte[]{byte0, byte1, byte2, byte3, byte4, byte5, byte6, byte7};

        driverVelocity = ByteBuffer.wrap( driverVeloBytes ).order( ByteOrder.BIG_ENDIAN ).getDouble();

        // FIXME Check if between 0 and 255
        byte0 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 16] ) & 0xFF);
        byte1 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 15] ) & 0xFF);
        byte2 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 14] ) & 0xFF);
        byte3 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 13] ) & 0xFF);
        byte4 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 12] ) & 0xFF);
        byte5 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 11] ) & 0xFF);
        byte6 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 10] ) & 0xFF);
        byte7 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 9] ) & 0xFF);

        byte[] robotVeloBytes = new byte[]{byte0, byte1, byte2, byte3, byte4, byte5, byte6, byte7};

        robotVelocity = ByteBuffer.wrap( robotVeloBytes ).order( ByteOrder.BIG_ENDIAN ).getDouble();

        byte0 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 8] ) & 0xFF);
        byte1 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 7] ) & 0xFF);
        byte2 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 6] ) & 0xFF);
        byte3 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 5] ) & 0xFF);
        byte4 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 4] ) & 0xFF);
        byte5 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 3] ) & 0xFF);
        byte6 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 2] ) & 0xFF);
        byte7 = (byte)(Integer.parseInt( hexBytes[hexBytes.length - 1] ) & 0xFF);

        byte[] targetVeloBytes = new byte[]{byte0, byte1, byte2, byte3, byte4, byte5, byte6, byte7};

        targetVelocity = ByteBuffer.wrap( targetVeloBytes ).order( ByteOrder.BIG_ENDIAN ).getDouble();
    }

    public String getPayload()
    {
        return payload;
    }

    public String getInterfaceName()
    {
        return interfaceName;
    }

    public DCCState getDCCState()
    {
        return state;
    }

    public double getDriverVelocity()
    {
        return driverVelocity;
    }

    public double getRobotVelocity()
    {
        return robotVelocity;
    }

    public double getTargetVelocity()
    {
        return targetVelocity;
    }
}
