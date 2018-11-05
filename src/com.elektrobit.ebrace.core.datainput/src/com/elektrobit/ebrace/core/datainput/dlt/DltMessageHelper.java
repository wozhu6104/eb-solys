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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elektrobit.ebrace.common.utils.HexStringHelper;

import lombok.extern.log4j.Log4j;

@Log4j
public class DltMessageHelper
{
    final static int STANDARD_HEADER_SIZE = 4;
    final static int EXTENDED_HEADER_SIZE = 10;
    final static byte[] expectedPattern = new byte[]{68, 76, 84, 1};

    private int offset = 0;

    public DltMessageHelper()
    {
    }

    public String createEvent(byte[] rawMessage)
    {
        try
        {
            DltMessage message = getNextMessage( new BufferedInputStream( new ByteArrayInputStream( rawMessage ) ) );
            message = parsePayload( message );
            return message.toJson();
        }
        catch (DltMessageParseException e)
        {
            return "";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public DltMessage getNextMessage(BufferedInputStream stream) throws IOException, DltMessageParseException
    {
        offset = 0;
        DltMessage dltMsg = new DltMessage();

        DltStandardHeader standardHeader = parseStandardHeader( stream );
        dltMsg.setStandardHeader( standardHeader );
        if (null != standardHeader)
        {
            // Check if an extended header is available (coded within Standard Header)
            if (standardHeader.hasExtendedHeader())
            {
                DltExtendedHeader extendedHeader = parseExtendedHeader( stream );
                dltMsg.setExtendedHeader( extendedHeader );

                if (null != extendedHeader && standardHeader.getMessageLength() > offset)
                {
                    byte[] bytes = new byte[standardHeader.getMessageLength() - offset];
                    stream.read( bytes );
                    dltMsg.setPayloadBuffer( bytes );
                }
                else
                {
                    log.error( "Parsing extended header failed" );
                    dltMsg = null;
                }
            }
            else
            {
                log.error( "No extended header - skipping" );
                int toBeSkipped = standardHeader.getMessageLength() - offset;
                byte[] skippedBytes = new byte[toBeSkipped];
                stream.read( skippedBytes );
                dltMsg = null;
            }
        }
        else
        {
            log.error( "Invalid DLT message" );
            dltMsg = null;
        }
        return dltMsg;
    }

    public DltMessage getNextMessageAfterStorageHeader(BufferedInputStream bytesReader)
            throws IOException, DltMessageParseException
    {
        DltMessage dltMsg;

        if (locateNextStorageHeader( bytesReader ))
        {
            dltMsg = getNextMessage( bytesReader );
        }
        else
        {
            dltMsg = null;
        }

        return dltMsg;
    }

    private boolean locateNextStorageHeader(BufferedInputStream bytesReader) throws IOException
    {
        boolean headerFound = false;
        boolean eofReached = false;
        List<Byte> dropList = new ArrayList<Byte>();
        byte[] asciiChar = new byte[1];
        int patternIt = 0;

        while (!headerFound)
        {
            bytesReader.read( asciiChar );
            eofReached = bytesReader.available() == 0;
            if (eofReached)
            {
                break;
            }

            if (isExpectedCharFound( asciiChar[0], expectedPattern[patternIt], dropList ))
            {
                patternIt++;
            }
            else
            {
                patternIt = 0;
            }
            if (patternIt == 4)
            {
                headerFound = true;
                if (dropList.size() > 0)
                {
                    log.warn( "Following content was dropped" + dropList );
                }
                // storage header is 16 bytes long (the search pattern +
                // an 8 byte timestamp + 4 bytes ecu id)
                byte[] skippedBytes = new byte[12];
                bytesReader.read( skippedBytes );
                eofReached = bytesReader.available() == 0;
            }
        }
        return !eofReached;
    }

    private boolean isExpectedCharFound(int actual, int expected, List<Byte> dropList) throws IOException
    {
        boolean retVal = false;

        if (actual == expected)
        {
            retVal = true;
        }
        else
        {
            dropList.add( (byte)actual );
        }
        return retVal;
    }

    private DltMessage parsePayload(DltMessage message) throws IOException
    {
        boolean isVerbose = message.getExtendedHeader().isVerbose();
        int numberOfArguments = message.getExtendedHeader().getNumberOfArguments();
        String messageType = message.getExtendedHeader().getMessageType();
        String messageTypeInfo = message.getExtendedHeader().getMessageTypeInfo();
        boolean bigEndian = message.getStandardHeader().isPayloadInBigEndian();
        int messageLength = message.getStandardHeader().getMessageLength() - offset;
        BufferedInputStream bytesReader = new BufferedInputStream( new ByteArrayInputStream( message
                .getPayloadBuffer() ) );
        byte[] rawPayload = message.getPayloadBuffer();

        if (isVerbose)
        {
            DltPayload payload = new DltPayload( rawPayload );

            message.setPayload( payload.getPayLoadItems( numberOfArguments, bigEndian, messageLength ) );
        }
        else
        {
            if (messageType.equals( "DLT_TYPE_CONTROL" ))
            {
                String messageTypeInfoDirection = messageTypeInfo.equals( "DLT_CONTROL_RESPONSE" )
                        ? "RESPONSE"
                        : "REQUEST";

                String parsedNonVerboseMessage = parseControlMessage( bytesReader, messageLength );

                message.setPayload( Arrays.asList( messageTypeInfoDirection + " " + parsedNonVerboseMessage ) );
            }
            else
            {
                message.setPayload( Arrays.asList( parseNonVerboseMessage( bytesReader, messageLength ) ) );
            }
        }

        return message;
    }

    private DltStandardHeader parseStandardHeader(BufferedInputStream stream)
            throws IOException, DltMessageParseException
    {
        byte[] standardHeaderBuffer = new byte[STANDARD_HEADER_SIZE];
        stream.read( standardHeaderBuffer );
        DltStandardHeader standardHeader = new DltStandardHeader( standardHeaderBuffer );
        // Get the message length (coded within the standard header)
        // The length includes: The standard header + (optional) extended header + (optional) payload
        int len = standardHeader.getMessageLength();
        offset += standardHeaderBuffer.length;
        if (len == 0)
        {
            throw new DltMessageParseException( "the length of the dlt message is zero " );
        }

        retrieveEcuId( standardHeader, stream );

        retrieveSessionId( standardHeader, stream );

        retrieveTimestamp( standardHeader, stream );

        return standardHeader;
    }

    private DltExtendedHeader parseExtendedHeader(BufferedInputStream stream) throws IOException
    {
        byte[] extendedHeaderBuffer = new byte[EXTENDED_HEADER_SIZE];
        stream.read( extendedHeaderBuffer );

        DltExtendedHeader extendedHeader = new DltExtendedHeader( extendedHeaderBuffer );

        offset += extendedHeaderBuffer.length;

        return extendedHeader;
    }

    private void retrieveTimestamp(DltStandardHeader standardHeader, BufferedInputStream stream) throws IOException
    {
        if (standardHeader.hasTimeStamp())
        {
            byte[] timestamp = new byte[4];
            stream.read( timestamp );
            standardHeader.setTimestamp( timestamp );
            offset += timestamp.length;
        }
    }

    private void retrieveEcuId(DltStandardHeader standardHeader, BufferedInputStream stream) throws IOException
    {
        if (standardHeader.hasECUId())
        {
            byte[] ecu = new byte[4];
            stream.read( ecu );
            standardHeader.setEcuId( new String( ecu, StandardCharsets.US_ASCII ) );
            offset += ecu.length;
        }
    }

    private void retrieveSessionId(DltStandardHeader standardHeader, BufferedInputStream stream) throws IOException
    {
        if (standardHeader.hasSessionId())
        {
            byte[] sessionId = new byte[4];
            stream.read( sessionId );

            standardHeader.setSessionId( sessionId );
            offset += sessionId.length;
        }
    }

    private String parseControlMessage(BufferedInputStream bytesReader, int bytes) throws IOException
    {

        byte[] serviceId = new byte[4];
        bytesReader.read( serviceId );
        String result = "";
        int lengthOfPayload = bytes - 4;

        byte responseCode = (byte)bytesReader.read();
        lengthOfPayload -= 1;

        if (serviceId[0] == 0x01)
        {
            result += "Set_LogLevel" + printableResponseCode( responseCode );
        }
        else if (serviceId[0] == 0x11)
        {
            result += "Set_DefaultLogLevel";
        }
        else if (serviceId[0] == 0x02)
        {
            result += "Set_TraceStatus" + printableResponseCode( responseCode );
        }
        else if (serviceId[0] == 0x12)
        {
            result += "Set_DefaultTraceStatus";
        }
        else if (serviceId[0] == 0x03)
        {
            result += "Get_LogInfo" + printableResponseCode( responseCode );
            if (lengthOfPayload > 0)
            {
                byte[] logInfoTypeBytes = new byte[lengthOfPayload];
                bytesReader.read( logInfoTypeBytes );
                lengthOfPayload = 0;

                DltLogInfoType dltLogInfoType = new DltLogInfoType( DltLogInfoType.ResponseCode.get( responseCode ),
                                                                    logInfoTypeBytes,
                                                                    true );
                // dltChannelFromLogInfoCreator.createChannelsForMessage( dltLogInfoType );
            }
        }
        else if (serviceId[0] == 0x04)
        {
            result += "Get_DefaultLogLevel";
        }
        else if (serviceId[0] == 0x15)
        {
            result += "Get_DefaultTraceStatus";
        }
        else if (serviceId[0] == 0x05)
        {
            result += "Store_Config";
        }
        else if (serviceId[0] == 0x06)
        {
            result += "ResetToFactoryDefault";
        }
        else if (serviceId[0] == 0x07)
        {
            result += "SetComInterfaceStatus";
        }
        else if (serviceId[0] == 0x16)
        {
            result += "GetComInterfaceStatus";
        }
        else if (serviceId[0] == 0x17)
        {
            result += "GetComInterfaceNames";
        }
        else if (serviceId[0] == 0x08)
        {
            result += "SetComInterfaceMaxBandwidth";
        }
        else if (serviceId[0] == 0x18)
        {
            result += "GetComInterfaceMaxBandwidth";
        }
        else if (serviceId[0] == 0x09)
        {
            result += "SetVerboseMode";
        }
        else if (serviceId[0] == 0x19)
        {
            result += "GetVerboseModeStatus";
        }
        else if (serviceId[0] == 0x0A)
        {
            result += "SetMessageFilterering";
        }
        else if (serviceId[0] == 0x1A)
        {
            result += "GetMessageFiltereringStatus";
        }
        else if (serviceId[0] == 0x0B)
        {
            result += "SetTimingPackets";
        }
        else if (serviceId[0] == 0x0C)
        {
            result += "GetLocalTime";
        }
        else if (serviceId[0] == 0x0D)
        {
            result += "SetUseECUID";
        }
        else if (serviceId[0] == 0x1B)
        {
            result += "GetUseECUID";
        }
        else if (serviceId[0] == 0x0F)
        {
            result += "UseTimestamp";
        }
        else if (serviceId[0] == 0x1D)
        {
            result += "GetUseTimestamp";
        }
        else if (serviceId[0] == 0x10)
        {
            result += "SetUseExtendedHeader";
        }
        else if (serviceId[0] == 0x1E)
        {
            result += "GetUseExtendedHeader";
        }
        else if (serviceId[0] == 0x13)
        {
            result += "GetSoftwareVersion";

            byte[] skippedBytes = new byte[4];
            bytesReader.read( skippedBytes );
            lengthOfPayload -= 4;
            if (lengthOfPayload > 0)
            {
                byte[] nonVerboseMessageAsBytes = new byte[lengthOfPayload];
                bytesReader.read( nonVerboseMessageAsBytes );
                lengthOfPayload = 0;
                result += printableResponseCode( responseCode ) + " " + new String( nonVerboseMessageAsBytes );
            }
        }
        else if (serviceId[0] == 0x14)
        {
            result += "MessageBufferOverflow";
        }
        else if (serviceId[0] == 0xFF)
        {
            result += "CallSW-CInjection";
        }
        else
        {
            log.warn( "ServiceName with ServiceId: " + serviceId[0] + " unsupported. " );
        }

        if (lengthOfPayload > 0)
        {
            byte[] nonVerboseMessageAsBytes = new byte[lengthOfPayload];
            bytesReader.read( nonVerboseMessageAsBytes );
            result += " " + new String( nonVerboseMessageAsBytes );
        }

        return result;
    }

    private String printableResponseCode(byte responseCode)
    {
        String printableResponseCode = "";
        if (responseCode == 0)
        {
            printableResponseCode = "OK";
        }
        else if (responseCode == 1)
        {
            printableResponseCode = "NOT_SUPPORTED";
        }
        else if (responseCode == 2)
        {
            printableResponseCode = "ERROR";
        }
        else
        {
            printableResponseCode = "code " + String.format( "%02x", responseCode );
        }
        return " [" + printableResponseCode + "]";
    }

    private String parseNonVerboseMessage(BufferedInputStream bytesReader, int length) throws IOException
    {
        byte[] payload = new byte[length];
        bytesReader.read( payload );
        return HexStringHelper.toHexString( payload );
    }

}
