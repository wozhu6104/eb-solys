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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author tomo3867 https://www.autosar.org/fileadmin/files/releases/4-2/software-architecture/system-services/standard/
 *         AUTOSAR_SWS_DiagnosticLogAndTrace.pdf
 */

public class DltExtendedHeader
{
    public static int LENGTH = 10;
    private byte[] extendedHeader = new byte[LENGTH];

    // Extended Header
    final static int VERB = 0x01;

    final static int MSG_TYPE_BIT_MASK = 0x0E;
    final static int MSG_TYPE_INFO_BIT_MASK = 0xF0;

    // bit 1-3
    final static int DLT_TYPE_LOG = 0x00;
    final static int DLT_TYPE_APP_TRACE = 0x01;
    final static int DLT_TYPE_NW_TRACE = 0x02;
    final static int DLT_TYPE_CONTROL = 0x03;
    final static List<String> msgTypes = Arrays
            .asList( "DLT_TYPE_LOG", "DLT_TYPE_APP_TRACE", "DLT_TYPE_NW_TRACE", "DLT_TYPE_CONTROL" );

    // bit 4-7 (depending on bit 1-3 -> DLT_TYPE_LOG)
    public final static Map<Integer, String> logInfo = initializeLogLevels();

    // bit 4-7 (depending on bit 1-3 -> DLT_TYPE_APP_TRACE)
    final static int DLT_TRACE_VARIABLE = 0x01;
    final static int DLT_TRACE_FUNCTION_IN = 0x02;
    final static int DLT_TRACE_FUNCTION_OUT = 0x03;
    final static int DLT_TRACE_STATE = 0x04;
    final static int DLT_TRACE_VFB = 0x05;
    final static List<String> appTraceInfo = Arrays.asList( "DLT_TRACE_VARIABLE",
                                                            "DLT_TRACE_FUNCTION_IN",
                                                            "DLT_TRACE_FUNCTION_OUT",
                                                            "DLT_TRACE_STATE",
                                                            "DLT_TRACE_VFB" );

    // bit 4-7 (depending on bit 1-3 -> DLT_TYPE_NW_TRACE)
    final static int DLT_NW_TRACE_IPC = 0x01;
    final static int DLT_NW_TRACE_CAN = 0x02;
    final static int DLT_NW_TRAE_FLEXRAY = 0x03;
    final static int DLT_NW_TRACE_MOST = 0x04;
    final static List<String> nwTraceInfo = Arrays
            .asList( "DLT_NW_TRACE_IPC", "DLT_NW_TRACE_CAN", "DLT_NW_TRAE_FLEXRAY", "DLT_NW_TRACE_MOST" );

    // bit 4-7 (depending on bit 1-3 -> DLT_TYPE_CONTROL)
    final static int DLT_CONTROL_REQUEST = 0x01;
    final static int DLT_CONTROL_RESPONSE = 0x02;
    final static int DLT_CONTROL_TIME = 0x03;
    final static List<String> ctrlInfo = Arrays
            .asList( "DLT_CONTROL_REQUEST", "DLT_CONTROL_RESPONSE", "DLT_CONTROL_TIME" );

    public DltExtendedHeader(byte[] extendedHeader)
    {
        this.extendedHeader = extendedHeader;
    }

    private static Map<Integer, String> initializeLogLevels()
    {
        Map<Integer, String> returnMap = new HashMap<Integer, String>();
        returnMap.put( -1, "default" );
        returnMap.put( 0, "off" );
        returnMap.put( 1, "DLT_LOG_FATAL" );
        returnMap.put( 2, "DLT_LOG_ERROR" );
        returnMap.put( 3, "DLT_LOG_WARN" );
        returnMap.put( 4, "DLT_LOG_INFO" );
        returnMap.put( 5, "DLT_LOG_DEBUG" );
        returnMap.put( 6, "DLT_LOG_VERBOSE" );
        return returnMap;
    }

    public DltExtendedHeader()
    {

    }

    public void setApplicationId(String appId)
    {
        setBytesInRange( 2, 5, appId.getBytes() );
    }

    public String getApplicationId()
    {
        return new String( Arrays.copyOfRange( extendedHeader, 2, 6 ), StandardCharsets.US_ASCII ).trim();
    }

    public void setContextId(String contextId)
    {
        setBytesInRange( 6, 9, contextId.getBytes() );
    }

    private void setBytesInRange(int start, int end, byte[] newContent)
    {
        for (int i = start, j = 0; i <= end && j < newContent.length; i++, j++)
        {
            extendedHeader[i] = newContent[j];
        }
    }

    public String getContextId()
    {
        return new String( Arrays.copyOfRange( extendedHeader, 6, 10 ), StandardCharsets.US_ASCII ).trim();
    }

    public void setVerbose(boolean verbosity)
    {
        if (verbosity)
        {
            extendedHeader[0] |= VERB;
        }
        else
        {
            extendedHeader[0] &= ~VERB;
        }
    }

    public boolean isVerbose()
    {
        return (extendedHeader[0] & VERB) == VERB;
    }

    public void setMessageType(int messageType)
    {
        extendedHeader[0] |= (messageType << 1);
    }

    public String getMessageType()
    {
        int key = (extendedHeader[0] & MSG_TYPE_BIT_MASK) >> 1;
        if (key >= msgTypes.size())
        {
            return "UNDEFINED_MESSAGE_TYPE";
        }
        return msgTypes.get( key );
    }

    public void setNumberOfArguments(short number)
    {
        extendedHeader[1] = (byte)number;
    }

    public short getNumberOfArguments()
    {
        return extendedHeader[1];
    }

    public void setMessageTypeInfo(int messageTypeInfo)
    {
        extendedHeader[0] |= (messageTypeInfo << 4);
    }

    public String getMessageTypeInfo()
    {
        int key = (extendedHeader[0] & MSG_TYPE_BIT_MASK) >> 1;
        int info = (extendedHeader[0] & MSG_TYPE_INFO_BIT_MASK) >> 4;
        int idx = info - 1;

        switch (key)
        {
            case DLT_TYPE_LOG :
                if (idx < 0 || idx >= logInfo.size())
                {
                    return "UNDEFINED_LOG_LEVEL_INFO";
                }
                return logInfo.get( info );
            case DLT_TYPE_APP_TRACE :
                if (idx < 0 || idx >= appTraceInfo.size())
                {
                    return "UNDEFINED_APP_TRACE_INFO";
                }
                return appTraceInfo.get( idx );
            case DLT_TYPE_NW_TRACE :
                if (idx < 0 || idx >= nwTraceInfo.size())
                {
                    return "UNDEFINED_NW_TRACE_INFO";
                }
                return nwTraceInfo.get( idx );
            case DLT_TYPE_CONTROL :
                if (idx < 0 || idx >= ctrlInfo.size())
                {
                    return "UNDEFINED_CONTROL_INFO";
                }
                return ctrlInfo.get( idx );
            default :
                return "Error: No suitable message info found";
        }
    }

    public byte[] getBytes()
    {
        return extendedHeader;
    }

}
