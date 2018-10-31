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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class DltLogInfoType
{
    // see Specification of Diagnostic Log and Trace V1.2.0 R4.0 Rev 3 paragraph 7.7.7.1.5
    public enum ResponseCode {
        NOT_SUPPORTED(1), ERROR(2), APP_CTX(3), APP_CTX_LOGLEVEL(4), APP_CTX_TRACESTATUS(
                5), APP_CTX_LOGLEVEL_TRACESTATUS(
                        6), APP_CTX_LOGLEVEL_TRACESTATUS_DESCRIPTION(7), NO_MATCHING_CTX(8), RESPONSE_DATA_OVERFLOW(9);

        private int responseCode;

        ResponseCode(int responseCode)
        {
            this.responseCode = responseCode;
        }

        public int getResponseCode()
        {
            return responseCode;
        }

        public static ResponseCode get(int i)
        {
            for (ResponseCode match : values())
            {
                if (match.responseCode == i)
                {
                    return match;
                }
            }
            return null;
        }
    }

    public class ChannelInfo
    {
        public String applicationID = "";
        public String contextID = "";
        public int logLevel = -1;
        public int traceStatus = -1;
        public String description = "";

        public ChannelInfo(String applicationID, String contextID, int logLevel, int traceStatus, String description)
        {
            this.applicationID = applicationID;
            this.contextID = contextID;
            this.logLevel = logLevel;
            this.traceStatus = traceStatus;
            this.description = description;
        }
    }

    private final List<ChannelInfo> channels = new ArrayList<ChannelInfo>();

    private String applicationID;
    private String contextID;
    private int logLevel = -1;
    private int traceStatus = -1;
    private String description = "";

    private String comInterface;

    public DltLogInfoType(ResponseCode responseCode, byte[] payload, boolean msbf)
    {
        switch (responseCode)
        {
            case APP_CTX_LOGLEVEL_TRACESTATUS :
                parsePayload( payload, false, msbf );
                break;
            case APP_CTX_LOGLEVEL_TRACESTATUS_DESCRIPTION :
                parsePayload( payload, true, msbf );
                break;
            case NO_MATCHING_CTX :
                parseComInterface( payload );
                break;
            case APP_CTX :
            case APP_CTX_LOGLEVEL :
            case APP_CTX_TRACESTATUS :
            case ERROR :
            case NOT_SUPPORTED :
            case RESPONSE_DATA_OVERFLOW :
            default :
                log.warn( "Unhandled response code given: " + responseCode.name() );
                break;
        }
    }

    public List<ChannelInfo> getChannels()
    {
        return channels;
    }

    private void parsePayload(byte[] payload, boolean hasDescription, boolean msbf)
    {

        ByteBuffer buffer = ByteBuffer.wrap( payload );
        buffer.order( msbf ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN );
        int remainingPayloadLength = payload.length;
        int nrOfAppIds = buffer.getShort();
        for (int i = 0; i < nrOfAppIds; i++)
        {
            byte[] appId = new byte[4];
            buffer.get( appId, 0, 4 );
            remainingPayloadLength -= 4;
            applicationID = new String( appId );

            int nrOfContextIds = buffer.getShort();
            remainingPayloadLength -= 2;
            for (int j = 0; j < nrOfContextIds; j++)
            {
                byte[] ctxId = new byte[4];
                buffer.get( ctxId, 0, 4 );
                remainingPayloadLength -= 4;
                contextID = new String( ctxId );

                logLevel = buffer.get();
                remainingPayloadLength -= 1;

                traceStatus = buffer.get();
                remainingPayloadLength -= 1;

                if (hasDescription)
                {
                    if (remainingPayloadLength > 0)
                    {
                        int lenContextDescription = buffer.getShort();
                        remainingPayloadLength -= 2;
                        byte[] descriptionBytes = new byte[lenContextDescription];
                        buffer.get( descriptionBytes, 0, lenContextDescription );
                        description = new String( descriptionBytes );
                        remainingPayloadLength -= lenContextDescription;
                    }
                }
                channels.add( new ChannelInfo( applicationID, contextID, logLevel, traceStatus, description ) );
            }
        }
        if (applicationID == null || contextID == null)
        {
            log.warn( "Invalid Application or Context ID given: '" + applicationID + "' '" + contextID + "'" );
        }

    }

    private void parseComInterface(byte[] payload)
    {
        comInterface = new String( payload ).trim();
    }

    public String getComInterface()
    {
        return comInterface;
    }
}
