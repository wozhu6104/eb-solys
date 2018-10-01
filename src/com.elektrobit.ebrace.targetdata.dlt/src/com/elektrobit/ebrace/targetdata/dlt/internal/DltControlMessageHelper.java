/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;

public class DltControlMessageHelper
{
    public static final int SERVICE_ID__SET_LOG_LEVEL = 0x01;
    public static final int SERVICE_ID__SET_DEFAULT_LOG_LEVEL = 0x11;
    public static final int SERVICE_ID__SET_TRACE_STATUS = 0x02;
    public static final int SERVICE_ID__SET_DEFAULT_TRACE_STATUS = 0x12;
    public static final int SERVICE_ID__GET_LOG_INFO = 0x03;
    public static final int SERVICE_ID__GET_DEFAULT_LOG_LEVEL = 0x04;
    public static final int SERVICE_ID__GET_DEFAULT_TRACE_STATUS = 0x15;
    public static final int SERVICE_ID__STORE_CONFIGURATION = 0x05;
    public static final int SERVICE_ID__RESET_TO_FACTORY_DEFAULT = 0x06;
    public static final int SERVICE_ID__SET_COMMUNICATION_INTERFACE_STATUS = 0x07;
    public static final int SERVICE_ID__GET_COMMUNICATION_INTERFACE_STATUS = 0x16;
    public static final int SERVICE_ID__GET_COMMUNICATION_INTERFACE_NAMES = 0x17;
    public static final int SERVICE_ID__SET_COMMUNICATION_MAXIMUM_BANDWIDTH = 0x08;
    public static final int SERVICE_ID__GET_COMMUNICATION_MAXIMUM_BANDWIDTH = 0x18;
    public static final int SERVICE_ID__SET_VERBOSE_MODE = 0x09;
    public static final int SERVICE_ID__SET_MESSAGE_FILTERING = 0x0A;
    public static final int SERVICE_ID__GET_MESSAGE_FILTERING_STATUS = 0x1A;
    public static final int SERVICE_ID__SET_TIMING_PACKETS = 0x0B;
    public static final int SERVICE_ID__GET_LOCAL_TIME = 0x0C;
    public static final int SERVICE_ID__SET_USE_ECU_ID = 0x0D;
    public static final int SERVICE_ID__GET_USE_ECU_ID = 0x1D;
    public static final int SERVICE_ID__SET_USE_SESSION_ID = 0x0E;
    public static final int SERVICE_ID__GET_USE_SESSION_ID = 0x1C;
    public static final int SERVICE_ID__SET_USE_TIMESTAMP = 0x0F;
    public static final int SERVICE_ID__GET_USE_TIMESTAMP = 0x1D;
    public static final int SERVICE_ID__SET_USE_EXTENDED_HEADER = 0x10;
    public static final int SERVICE_ID__GET_USE_EXTENDED_HEADER = 0x1E;
    public static final int SERVICE_ID__GET_SOFTWARE_VERSION = 0x13;
    public static final int SERVICE_ID__MESSAGE_BUFFER_OVERFLOW = 0x14;

    public static final byte[] comInterface = "remo".getBytes();

    public static DltMessage createControlMessageSetLogLevel(String ecuId, String appIdSrc, String ctxIdSrc,
            long timestamp, String appId, String ctxId, int logLevel)
    {
        List<byte[]> params = new ArrayList<byte[]>();
        params.add( appId.getBytes() );
        params.add( ctxId.getBytes() );
        params.add( new byte[]{(byte)logLevel} );
        params.add( comInterface );
        return createControlMessage( ecuId, appIdSrc, ctxIdSrc, timestamp, SERVICE_ID__SET_LOG_LEVEL, params );
    }

    public static DltMessage createControlMessageSetDefaultLogLevel(String ecuId, String appId, String ctxId,
            long timestamp, int logLevel)
    {
        List<byte[]> params = new ArrayList<byte[]>();
        params.add( new byte[]{(byte)logLevel} );
        params.add( comInterface );
        return createControlMessage( ecuId, appId, ctxId, timestamp, SERVICE_ID__SET_DEFAULT_LOG_LEVEL, params );
    }

    public static DltMessage createControlMessageGetSoftwareVersion(String ecuId, String appId, String ctxId,
            long timestamp)
    {
        return createControlMessage( ecuId, appId, ctxId, timestamp, SERVICE_ID__GET_SOFTWARE_VERSION, null );
    }

    public static OutgoingMessage createControlMessageSetTraceStatus(String ecuId, String appIdSrc, String ctxIdSrc,
            long timestamp, String appId, String ctxId, int traceStatus)
    {
        List<byte[]> params = new ArrayList<byte[]>();
        params.add( appId.getBytes() );
        params.add( ctxId.getBytes() );
        params.add( new byte[]{(byte)traceStatus} );
        params.add( comInterface );
        return createControlMessage( ecuId, appIdSrc, ctxIdSrc, timestamp, SERVICE_ID__SET_TRACE_STATUS, params );
    }

    public static OutgoingMessage createControlMessageGetLogInfo(String ecuId, String appIdSrc, String ctxIdSrc,
            long timestamp, int options, String appId, String ctxId)
    {
        List<byte[]> params = new ArrayList<byte[]>();
        params.add( new byte[]{(byte)options} );
        params.add( appId.getBytes() );
        params.add( ctxId.getBytes() );
        params.add( comInterface );
        return createControlMessage( ecuId, appIdSrc, ctxIdSrc, timestamp, SERVICE_ID__GET_LOG_INFO, params );
    }

    public static DltMessage createControlMessage(String ecuId, String appId, String ctxId, long timestamp,
            int serviceId, List<byte[]> payloadItems)
    {
        DltStandardHeader standardHeader = new DltStandardHeader();
        standardHeader.setExtendedHeaderBit();
        DltExtendedHeader extendedHeader = new DltExtendedHeader();
        DltMessage controlMessage = new DltMessage();
        standardHeader.setEcuId( ecuId );
        standardHeader.setTimestamp( ByteBuffer.allocate( 4 ).putInt( (int)timestamp ).array() );

        extendedHeader.setVerbose( true ); // TODO needed?
        extendedHeader.setMessageType( DltExtendedHeader.DLT_TYPE_CONTROL );
        extendedHeader.setMessageTypeInfo( DltExtendedHeader.DLT_CONTROL_REQUEST );
        extendedHeader.setNumberOfArguments( (short)(1 + (payloadItems != null ? payloadItems.size() : 0)) );
        extendedHeader.setApplicationId( appId );
        extendedHeader.setContextId( ctxId );

        controlMessage.setStandardHeader( standardHeader );
        controlMessage.setExtendedHeader( extendedHeader );
        controlMessage.addPayloadItem( serviceId );
        if (payloadItems != null)
        {
            for (byte[] item : payloadItems)
            {
                controlMessage.addPayloadItem( item );
            }
        }
        return controlMessage;
    }
}
