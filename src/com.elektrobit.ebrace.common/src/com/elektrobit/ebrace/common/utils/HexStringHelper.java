/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.math.BigInteger;

public class HexStringHelper
{
    public static Integer convertHexIDToUInt8(final String hexID)
    {
        final String value = hexID.replaceFirst( "0x", "" );

        if (value.length() > 2)
        {
            throw new IllegalArgumentException( "Hex value to big to converted to a Integer. Value was " + hexID );
        }

        return Integer.parseInt( value, 16 );
    }

    public static Integer convertHexIDToUInt16(final String hexID)
    {
        final String value = hexID.replaceFirst( "0x", "" );

        if (value.length() > 4)
        {
            throw new IllegalArgumentException( "Hex value to big to converted to an unsigned integer 16. Value was "
                    + hexID );
        }

        return Integer.parseInt( value, 16 );
    }

    public static Long convertHexIDToUInt32(final String hexID)
    {
        String value = hexID.replaceFirst( "0x", "" );

        if (value.length() > 8)
        {
            throw new IllegalArgumentException( "Hex value to big to converted to an unsigned integer 32. Value was "
                    + hexID );
        }

        return Long.parseLong( value, 16 );
    }

    public static BigInteger convertHexIDToUInt64(final String hexID)
    {
        String value = hexID.replaceFirst( "0x", "" );

        if (value.length() > 16)
        {
            throw new IllegalArgumentException( "Hex value to big to converted to a unsigned integer 64. Value was "
                    + hexID );
        }

        return new BigInteger( hexID, 16 );
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte)((Character.digit( s.charAt( i ), 16 ) << 4)
                    + Character.digit( s.charAt( i + 1 ), 16 ));
        }
        return data;
    }

    public static String convertHexToInt8(String input)
    {
        return convertHexToSignedValue( input, 2 );
    }

    public static String convertHexToInt16(String input)
    {
        return convertHexToSignedValue( input, 4 );
    }

    public static String convertHexToInt32(String input)
    {
        return convertHexToSignedValue( input, 8 );
    }

    private static String convertHexToSignedValue(String input, int length)
    {
        final String value = input.replaceFirst( "0x", "" );
        if (value.length() > length)
        {
            throw new IllegalArgumentException( "Hex value to big to converted. Value length was " + value.length()
                    + ". Expected max length was " + length + "." );
        }

        long s = Long.parseLong( value, 16 );
        if (length == 2)
        {
            return String.valueOf( (byte)s );
        }
        if (length == 4)
        {
            return String.valueOf( (short)s );
        }
        if (length == 8)
        {
            return String.valueOf( (int)s );
        }

        throw new IllegalArgumentException( "Input value to long." );
    }

    public static String readBitsFromTo(String hexValue, Integer bitPos, Integer bitSize)
    {
        String takeBitsFromTo = takeBitsFromTo( hexValue, bitPos, bitSize );
        String result = new BigInteger( takeBitsFromTo, 16 ).shiftRight( bitPos ).toString( 16 );

        return result;
    }

    public static String readBitsFromToReversed(String hexValue, Integer bitPos, Integer bitSize)
    {
        return readBitsFromTo( reverseHex( hexValue ), bitPos, bitSize );
    }

    public static String reverseHex(String originalHex)
    {
        if (originalHex.length() % 2 != 0)
        {
            originalHex = "0" + originalHex;
        }

        if (originalHex.length() == 2)
        {
            return originalHex;
        }

        int lengthInBytes = originalHex.length() / 2;
        char[] chars = new char[lengthInBytes * 2];
        for (int index = 0; index < lengthInBytes; index++)
        {
            int reversedIndex = lengthInBytes - 1 - index;
            chars[reversedIndex * 2] = originalHex.charAt( index * 2 );
            chars[reversedIndex * 2 + 1] = originalHex.charAt( index * 2 + 1 );
        }
        return new String( chars );
    }

    public static String takeBitsFromTo(String hexValue, Integer bitPos, Integer bitSize)
    {
        long result = 0;

        for (int i = bitPos; i < bitPos + bitSize; i++)
        {
            result += (long)Math.pow( 2, i );
        }

        BigInteger resultAsBigInt = BigInteger.valueOf( result );
        BigInteger hexAsBigInt = new BigInteger( hexValue, 16 );
        BigInteger res = resultAsBigInt.and( hexAsBigInt );

        return res.toString( 16 );
    }

    public static String removeNullTerminatorFromHexString(final String inputInHex)
    {
        if (inputInHex.length() % 2 == 1)
        {
            throw new IllegalArgumentException( "Hex string must be even. String was " + inputInHex + "." );
        }

        if (inputInHex.endsWith( "00" ))
        {
            return inputInHex.substring( 0, inputInHex.length() - 2 );
        }
        else
        {
            return inputInHex;
        }
    }

    public static String toHexString(byte[] buffer)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : buffer)
        {
            sb.append( String.format( "%02X ", b ) );
        }
        return sb.toString().trim();
    }
}
