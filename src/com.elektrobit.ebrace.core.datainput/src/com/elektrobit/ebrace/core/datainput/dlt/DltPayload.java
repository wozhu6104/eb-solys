/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datainput.dlt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.utils.HexStringHelper;

import lombok.extern.log4j.Log4j;

/**
 * 
 * @author tomo3867 https://www.autosar.org/fileadmin/files/releases/4-2/software-architecture/system-services/standard/
 *         AUTOSAR_SWS_DiagnosticLogAndTrace.pdf
 */
@Log4j
public class DltPayload
{
    private ByteOrder order = null;
    private int remainingLength = 0;

    static byte[] SHORT_BUFFER = new byte[2];
    static byte[] INT_BUFFER = new byte[4];
    static byte[] LONG_BUFFER = new byte[8];
    static byte[] FLOAT_BUFFER = new byte[4];
    static byte[] DOUBLE_BUFFER = new byte[8];

    final static byte[] RAW_LENGTH_BUFFER = new byte[2];
    final static byte[] STR_LENGTH_BUFFER = new byte[2];

    final static int READ_LIMIT = 256;
    final static int STRING_TERMINATION = 0;

    final static int TYPE_LENGTH_UNDEFINED = 0x00;
    final static int TYPE_LENGTH_8_BIT = 0x01;
    final static int TYPE_LENGTH_16_BIT = 0x02;
    final static int TYPE_LENGTH_32_BIT = 0x03;
    final static int TYPE_LENGTH_64_BIT = 0x04;
    final static int TYPE_LENGTH_128_BIT = 0x05;

    final static int TYPE_LENGTH_MASK = 0x000F;

    // Argument Type Info
    final static int TYPE_BOOL = 0x0010;
    final static int TYPE_SINT = 0x0020;
    final static int TYPE_UINT = 0x0040;
    final static int TYPE_FLOA = 0x0080;
    final static int TYPE_ARAY = 0x0100;
    final static int TYPE_STRG = 0x0200;
    final static int TYPE_RAWD = 0x0400;
    final static int TYPE_VARI = 0x0800;
    final static int TYPE_FIXP = 0x1000;
    final static int TYPE_TRAI = 0x2000;
    final static int TYPE_STRU = 0x4000;

    final static int TYPE_MASK = 0x7FF0;

    final BufferedInputStream payloadStream;

    public DltPayload(byte[] rawPayload)
    {
        payloadStream = new BufferedInputStream( new ByteArrayInputStream( rawPayload ) );
    }

    private int getLength(int argumentType)
    {
        return argumentType & TYPE_LENGTH_MASK;
    }

    private int getType(int argumentType)
    {
        return argumentType & TYPE_MASK;
    }

    private boolean hasVari(int tpyeInfo)
    {
        return (tpyeInfo & TYPE_VARI) == TYPE_VARI;
    }

    public List<String> getPayLoadItems(int numberOfArguments, boolean bigEndian, int _remainingLength)
            throws IOException
    {

        List<String> result = new ArrayList<String>();

        order = bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        remainingLength = _remainingLength;

        for (int i = 0; i < numberOfArguments; i++)
        {
            // First 4 Bytes of length and type info
            int typeInfo = readInt();
            int aLength = getLength( typeInfo );
            int aType = getType( typeInfo );
            // All further operations depend on the argument type
            switch (aType)
            {
                case TYPE_BOOL :
                    if (hasVari( typeInfo ))
                    {
                        readVari();
                    }
                    if (readByte() == 0)
                    {
                        result.add( "false" );
                    }
                    else
                    {
                        result.add( "true" );
                    }
                    break;
                case TYPE_SINT :
                    switch (aLength)
                    {
                        case TYPE_LENGTH_8_BIT :
                            result.add( Byte.toString( readByte() ) );
                            break;
                        case TYPE_LENGTH_16_BIT :
                            result.add( Short.toString( readShort() ) );
                            break;
                        case TYPE_LENGTH_32_BIT :
                            result.add( Integer.toString( readInt() ) );
                            break;
                        case TYPE_LENGTH_64_BIT :
                            result.add( Long.toString( readLong() ) );
                            break;
                    }
                    break;
                case TYPE_UINT :
                    switch (aLength)
                    {
                        case TYPE_LENGTH_8_BIT :
                            result.add( Byte.toString( readByte() ) );
                            break;
                        case TYPE_LENGTH_16_BIT :
                            result.add( Short.toString( readShort() ) );
                            break;
                        case TYPE_LENGTH_32_BIT :
                            result.add( Integer.toString( readInt() ) );
                            break;
                        case TYPE_LENGTH_64_BIT :
                            result.add( Long.toString( readLong() ) );
                            break;
                    }
                    break;
                case TYPE_FLOA :
                    switch (aLength)
                    {
                        case TYPE_LENGTH_32_BIT :
                            result.add( Float.toString( readFloat() ) );
                            break;
                        case TYPE_LENGTH_64_BIT :
                            result.add( Double.toString( readDouble() ) );
                            break;
                    }
                    break;
                case TYPE_ARAY :
                    log.warn( "*** TODO: TYPE_ARAY " );
                    break;
                case TYPE_STRG :
                    result.add( readString( typeInfo ) );
                    break;
                case TYPE_RAWD :
                    result.add( HexStringHelper.toHexString( readRaw( typeInfo ) ) );
                    break;
                case TYPE_VARI :
                    readVari();
                    break;
                case TYPE_FIXP :
                    log.warn( "*** TODO: TYPE_FIXP " );
                    break;
                case TYPE_TRAI :
                    result.add( readString( typeInfo ) );
                    break;
                case TYPE_STRU :
                    log.warn( "*** TODO: TYPE_STRU " );
                    break;
                default :
                    // FIXME
                    // This should not happen. Needs better handling, than skipping bytes
                    payloadStream.skip( remainingLength );
                    log.warn( "Unknown Payload Type: " + aType );
                    return result;
            }
        }

        return result;
    }

    private byte readByte() throws IOException
    {
        remainingLength -= 1;
        return (byte)payloadStream.read();
    }

    private short readShort() throws IOException
    {
        payloadStream.read( SHORT_BUFFER );
        remainingLength -= SHORT_BUFFER.length;
        return ByteBuffer.wrap( SHORT_BUFFER ).order( order ).getShort();
    }

    private int readInt() throws IOException
    {
        payloadStream.read( INT_BUFFER );
        remainingLength -= INT_BUFFER.length;
        return ByteBuffer.wrap( INT_BUFFER ).order( order ).getInt();
    }

    private long readLong() throws IOException
    {
        payloadStream.read( LONG_BUFFER );
        remainingLength -= LONG_BUFFER.length;
        return ByteBuffer.wrap( LONG_BUFFER ).order( order ).getLong();
    }

    private float readFloat() throws IOException
    {
        payloadStream.read( FLOAT_BUFFER );
        remainingLength -= FLOAT_BUFFER.length;
        return ByteBuffer.wrap( FLOAT_BUFFER ).order( order ).getFloat();
    }

    private double readDouble() throws IOException
    {
        payloadStream.read( DOUBLE_BUFFER );
        remainingLength -= DOUBLE_BUFFER.length;
        return ByteBuffer.wrap( DOUBLE_BUFFER ).order( order ).getDouble();
    }

    private byte[] readRaw(int typeInfo) throws IOException
    {
        short length = readShort();
        byte[] _raw = new byte[length];
        payloadStream.read( _raw );
        remainingLength -= _raw.length;
        if (hasVari( typeInfo ))
        {
            readVari();
        }
        return _raw;
    }

    private String readVari() throws IOException
    {
        short paramNameLength = readShort();
        byte[] paramName = new byte[paramNameLength];
        if (paramNameLength > 0)
        {
            payloadStream.read( paramName );
            remainingLength -= paramNameLength;
        }
        return new String( paramName );
    }

    private String readString(int typeInfo) throws IOException
    {
        short inputStringLength = readShort();

        byte[] inputStringName = new byte[inputStringLength];
        if (hasVari( typeInfo ))
        {
            readVari();
        }
        payloadStream.read( inputStringName );
        remainingLength -= inputStringLength;

        if ((inputStringName[inputStringLength - 1]) == STRING_TERMINATION)
        {
            remainingLength -= inputStringLength;
            return new String( inputStringName );
        }
        else
        {
            // same handling as dlt-receive: assume string has at least stringLength bytes: if that is not the case
            // look for the string terminator
            // starting with stringLength+1

            ByteArrayOutputStream out = new ByteArrayOutputStream( inputStringLength );

            for (byte value; remainingLength > 0 && (value = (byte)payloadStream.read()) != STRING_TERMINATION;)
            {
                remainingLength -= 1;
                out.write( value );
            }

            return new String( inputStringName ) + new String( out.toByteArray(), StandardCharsets.UTF_8 );
        }
    }

}
