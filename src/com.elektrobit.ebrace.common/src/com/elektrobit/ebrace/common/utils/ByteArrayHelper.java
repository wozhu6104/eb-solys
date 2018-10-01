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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteArrayHelper
{
    public static long bytesToLong(byte[] bytes, ByteOrder order)
    {
        byte[] buffer = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < bytes.length; i++)
        {
            buffer[order.equals( ByteOrder.LITTLE_ENDIAN ) ? 7 - i : i] = bytes[i];
        }
        return ByteBuffer.wrap( buffer ).getLong();
    }

    public static byte[] appendBytes(byte[] destination, byte[] appendix)
    {
        byte[] combined = new byte[destination.length + appendix.length];
        System.arraycopy( destination, 0, combined, 0, destination.length );
        System.arraycopy( appendix, 0, combined, destination.length, appendix.length );
        return combined;
    }

    public static void setBytesInRange(int start, int end, byte[] source, byte[] newContent)
    {
        for (int i = start, j = 0; i <= end && j < newContent.length; i++, j++)
        {
            source[i] = newContent[j];
        }
    }

    public static byte[] intToByteArray(int number, boolean mostSignificantByteFirst)
    {
        return mostSignificantByteFirst ? intToByteArrayMostSignificantByteFirst( number ) : intToByteArray( number );
    }

    public static int byteArrayToInt(byte[] number, boolean mostSignificantByteFirst)
    {
        return mostSignificantByteFirst ? byteArrayToIntMostSignificantByteFirst( number ) : byteArrayToInt( number );
    }

    private static byte[] intToByteArray(int number)
    {
        return new byte[]{(byte)((number >> 24) & 0xFF), (byte)((number >> 16) & 0xFF), (byte)((number >> 8) & 0xFF),
                (byte)(number & 0xFF)};
    }

    private static byte[] intToByteArrayMostSignificantByteFirst(int number)
    {
        return new byte[]{(byte)(number & 0xFF), (byte)((number >> 8) & 0xFF), (byte)((number >> 16) & 0xFF),
                (byte)((number >> 24) & 0xFF)};
    }

    private static int byteArrayToInt(byte[] number)
    {
        return number[3] & 0xFF | (number[2] & 0xFF) << 8 | (number[1] & 0xFF) << 16 | (number[0] & 0xFF) << 24;
    }

    private static int byteArrayToIntMostSignificantByteFirst(byte[] number)
    {
        return number[0] & 0xFF | (number[1] & 0xFF) << 8 | (number[2] & 0xFF) << 16 | (number[3] & 0xFF) << 24;
    }

    public static byte[] shortToByteArray(short number, boolean mostSignificantByteFirst)
    {
        return mostSignificantByteFirst
                ? shortToByteArrayMostSignificantByteFirst( number )
                : shortToByteArray( number );
    }

    public static short byteArrayToShort(byte[] number, boolean mostSignificantByteFirst)
    {
        return mostSignificantByteFirst
                ? byteArrayToShortMostSignificantByteFirst( number )
                : byteArrayToShort( number );
    }

    private static byte[] shortToByteArray(short number)
    {
        return new byte[]{(byte)((number >> 8) & 0xFF), (byte)(number & 0xFF)};
    }

    private static byte[] shortToByteArrayMostSignificantByteFirst(short number)
    {
        return new byte[]{(byte)(number & 0xFF), (byte)((number >> 8) & 0xFF)};
    }

    private static short byteArrayToShort(byte[] number)
    {
        return (short)(number[1] & 0xFF | (number[0] & 0xFF) << 8);
    }

    private static short byteArrayToShortMostSignificantByteFirst(byte[] number)
    {
        return (short)(number[0] & 0xFF | (number[1] & 0xFF) << 8);
    }
}
