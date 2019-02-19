/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.newapiimpl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.utils.HexStringHelper;
import com.elektrobit.ebrace.targetadapter.communicator.api.BytesFromStreamReader;
import com.elektrobit.ebrace.targetadapter.communicator.api.MessageReader;
import com.elektrobit.ebrace.targetdata.dlt.internal.BytesFromStreamReaderImpl;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltExtendedHeader;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltLogInfoType;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageParseException;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessageWithStorageHeaderParser;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltPayload;
import com.elektrobit.ebrace.targetdata.dlt.internal.DltStandardHeader;
import com.elektrobit.ebrace.targetdata.dlt.internal.connection.DltChannelFromLogInfoCreator;
import com.elektrobit.ebrace.targetdata.dlt.newapi.DltMessageService;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class DltMessageServiceImpl implements MessageReader<DltMessage>, DltMessageService
{
    final static int STANDARD_HEADER_SIZE = 4;
    final static int EXTENDED_HEADER_SIZE = 10;

    private int offset = 0;
    private DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator;
    private DltMessageWithStorageHeaderParser dltMessageWithStorageHaderParser;

    public DltMessageServiceImpl(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "dltChannelFromLogInfoCreator", dltChannelFromLogInfoCreator );
        this.dltChannelFromLogInfoCreator = dltChannelFromLogInfoCreator;
    }

    public DltMessageServiceImpl()
    {
    }

    @Reference
    public void bindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = dltChannelFromLogInfoCreator;
    }

    public void unbindDltChannelFromLogInfoCreator(DltChannelFromLogInfoCreator dltChannelFromLogInfoCreator)
    {
        this.dltChannelFromLogInfoCreator = null;
    }

    @Override
    public String createEvent(byte[] message)
    {
        try
        {
            return readNextMessage( new BytesFromStreamReaderImpl( new ByteArrayInputStream( message ) ) ).toJson();
        }
        catch (DltMessageParseException e)
        {
            return "";
        }
    }

    @Override
    public byte[] tokenizeNextMessageFileHeader(BufferedInputStream stream) throws IOException
    {
        return dltMessageWithStorageHaderParser.readNextMessageUnChecked( new BytesFromStreamReaderImpl( stream ) )
                .toByteArrayWithUnparsedPayload();
    }

    @Override
    public byte[] tokenizeNextMessageStreamHeader(BufferedInputStream stream) throws IOException
    {
        return readNextMessageUnChecked( new BytesFromStreamReaderImpl( stream ) ).toByteArrayWithUnparsedPayload();
    }

    @Override
    public DltMessage readNextMessage(BytesFromStreamReader bytesReader)
    {
        try
        {
            return parsePayload( readNextMessageUnChecked( bytesReader ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public DltMessage readNextMessageUnChecked(BytesFromStreamReader bytesReader)
            throws DltMessageParseException, IOException
    {
        offset = 0;
        DltMessage dltMsg = new DltMessage();

        DltStandardHeader standardHeader = parseStandardHeader( bytesReader );
        dltMsg.setStandardHeader( standardHeader );
        if (null != standardHeader)
        {
            // Check if an extended header is available (coded within Standard Header)
            if (standardHeader.hasExtendedHeader())
            {
                DltExtendedHeader extendedHeader = parseExtendedHeader( bytesReader );
                dltMsg.setExtendedHeader( extendedHeader );

                if (null != extendedHeader)
                {
                    dltMsg.setPayloadBuffer( bytesReader.readNBytes( standardHeader.getMessageLength() - offset ) );
                }
                else
                {
                    throw new DltMessageParseException( "No Extended Header" );
                }
            }
            else
            {
                int messageLength = standardHeader.getMessageLength();
                ignoreBytes( bytesReader,
                             messageLength - offset,
                             "No handling for messages with Standard header only" );
            }
        }
        return dltMsg;
    }

    private DltMessage parsePayload(DltMessage message) throws IOException
    {
        boolean isVerbose = message.getExtendedHeader().isVerbose();
        int numberOfArguments = message.getExtendedHeader().getNumberOfArguments();
        String messageType = message.getExtendedHeader().getMessageType();
        String messageTypeInfo = message.getExtendedHeader().getMessageTypeInfo();
        boolean bigEndian = message.getStandardHeader().isPayloadInBigEndian();
        int messageLength = message.getStandardHeader().getMessageLength() - offset;
        BytesFromStreamReaderImpl bytesReader = new BytesFromStreamReaderImpl( new ByteArrayInputStream( message
                .getPayloadBuffer() ) );

        if (isVerbose)
        {
            DltPayload payload = new DltPayload( bytesReader );

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

    private DltStandardHeader parseStandardHeader(BytesFromStreamReader bytesReader)
            throws IOException, DltMessageParseException
    {
        // is.read( standardHeaderBuffer );
        byte[] standardHeaderBuffer = bytesReader.readNBytes( STANDARD_HEADER_SIZE );
        DltStandardHeader standardHeader = new DltStandardHeader( standardHeaderBuffer );
        // Get the message length (coded within the standard header)
        // The length includes: The standard header + (optional) extended header + (optional) payload
        int len = standardHeader.getMessageLength();
        offset += standardHeaderBuffer.length;
        if (len == 0)
        {
            throw new DltMessageParseException( "the length of the dlt message is zero " );

        }

        retrieveEcuId( standardHeader, bytesReader );

        retrieveSessionId( standardHeader, bytesReader );

        retrieveTimestamp( standardHeader, bytesReader );

        return standardHeader;
    }

    private DltExtendedHeader parseExtendedHeader(BytesFromStreamReader bytesReader) throws IOException
    {
        byte[] extendedHeaderBuffer = bytesReader.readNBytes( EXTENDED_HEADER_SIZE );

        DltExtendedHeader extendedHeader = new DltExtendedHeader( extendedHeaderBuffer );

        offset += extendedHeaderBuffer.length;

        return extendedHeader;
    }

    private void retrieveTimestamp(DltStandardHeader standardHeader, BytesFromStreamReader bytesReader)
            throws IOException
    {
        if (standardHeader.hasTimeStamp())
        {
            byte[] timestamp = bytesReader.readNBytes( 4 );
            standardHeader.setTimestamp( timestamp );
            offset += timestamp.length;
        }
    }

    private void retrieveEcuId(DltStandardHeader standardHeader, BytesFromStreamReader bytesReader) throws IOException
    {
        if (standardHeader.hasECUId())
        {
            byte[] ecu = bytesReader.readNBytes( 4 );
            standardHeader.setEcuId( new String( ecu, StandardCharsets.US_ASCII ) );
            offset += ecu.length;
        }

    }

    private void retrieveSessionId(DltStandardHeader standardHeader, BytesFromStreamReader bytesReader)
            throws IOException
    {
        if (standardHeader.hasSessionId())
        {
            byte[] sessionId = bytesReader.readNBytes( 4 );

            standardHeader.setSessionId( sessionId );
            offset += sessionId.length;
        }
    }

    private void ignoreBytes(BytesFromStreamReader bytesReader, int bytes, String errorMsg)
            throws DltMessageParseException, IOException
    {
        if (bytes < 0)
        {
            throw new DltMessageParseException( "Invalid length" );
        }

        throw new DltMessageParseException( errorMsg );
    }

    private String parseNonVerboseMessage(BytesFromStreamReader bytesReader, int bytes)
    {
        byte[] payload = bytesReader.readNBytes( bytes );
        return HexStringHelper.toHexString( payload );
    }

    private String parseControlMessage(BytesFromStreamReader bytesReader, int bytes)
    {

        byte[] serviceId = bytesReader.readNBytes( 4 );
        String result = "";
        int lengthOfPayload = bytes - 4;

        byte[] responseCode = bytesReader.readNBytes( 1 );
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
                byte[] logInfoTypeBytes = bytesReader.readNBytes( lengthOfPayload );
                lengthOfPayload = 0;
                try
                {
                    DltLogInfoType dltLogInfoType = new DltLogInfoType( DltLogInfoType.ResponseCode
                            .get( responseCode[0] ), logInfoTypeBytes, true );
                    //FIXME rage_linux_live_demo_dlt_json_api
                    //Does we need the code around?
                    // dltChannelFromLogInfoCreator.createChannelsForMessage( dltLogInfoType );
                }
                catch (Exception e)
                {
                    log.warn( "Couldn't parse Get_LogInfo message." );
                }

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

            bytesReader.readNBytes( 4 );
            lengthOfPayload -= 4;
            if (lengthOfPayload > 0)
            {
                byte[] nonVerboseMessageAsBytes = bytesReader.readNBytes( lengthOfPayload );
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
            System.out.println( "ServiceName with ServiceId: " + serviceId[0] + " unsupported. " );
        }

        if (lengthOfPayload > 0)
        {
            byte[] nonVerboseMessageAsBytes = bytesReader.readNBytes( lengthOfPayload );
            result += " " + new String( nonVerboseMessageAsBytes );
        }

        return result;
    }

    private String printableResponseCode(byte[] responseCode)
    {
        String printableResponseCode = "";
        if (responseCode[0] == 0)
        {
            printableResponseCode = "OK";
        }
        else if (responseCode[0] == 1)
        {
            printableResponseCode = "NOT_SUPPORTED";
        }
        else if (responseCode[0] == 2)
        {
            printableResponseCode = "ERROR";
        }
        else
        {
            printableResponseCode = "code " + String.format( "%02x", responseCode[0] );
        }
        return " [" + printableResponseCode + "]";
    }
}
