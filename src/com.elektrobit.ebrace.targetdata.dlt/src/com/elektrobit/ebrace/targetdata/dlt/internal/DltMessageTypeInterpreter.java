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

import java.util.HashMap;

public class DltMessageTypeInterpreter
{
    private final static String EXPECTED_APP_ID = "SYS";
    private final static String EXPECTED_PER_PROC_CTX_ID = "PROC";
    private final static String PER_PROC_CPU_FILE_NAME = "stat";
    private final static String PER_PROC_MEMORY_FILE_NAME = "statm";
    private final static HashMap<String, String> dbusSources = new HashMap<String, String>()
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put( "DBUS", "ALL" );
            put( "DBSY", "DBSY" );
            put( "DBCN", "DBCN" );
        }
    };

    public static DltMessageType getMessageType(DltMessage dltMsg)
    {
        DltMessageType retVal = DltMessageType.DLT_MESSAGE_TYPE_OTHER;
        if (isProcStatMessage( dltMsg ))
        {
            return DltMessageType.DLT_MESSAGE_TYPE_CPU_INFO;
        }
        else if (isProcStatmMessage( dltMsg ))
        {
            return DltMessageType.DLT_MESSAGE_TYPE_MEM_INFO;
        }
        else if (isDbusMessage( dltMsg ))
        {
            return DltMessageType.DLT_MESSAGE_TYPE_DBUS;
        }
        return retVal;
    }

    private static boolean isProcStatMessage(DltMessage dltMsg)
    {
        boolean result = false;
        if (EXPECTED_PER_PROC_CTX_ID.equals( dltMsg.getExtendedHeader().getContextId() )
                && EXPECTED_APP_ID.equals( dltMsg.getExtendedHeader().getApplicationId() ))
        {
            if (dltMsg.getPayload().get( 1 ).startsWith( PER_PROC_CPU_FILE_NAME )
                    && !dltMsg.getPayload().get( 1 ).startsWith( PER_PROC_MEMORY_FILE_NAME ))
            {
                result = true;
            }
        }
        return result;
    }

    private static boolean isProcStatmMessage(DltMessage dltMsg)
    {
        boolean result = false;
        if (EXPECTED_PER_PROC_CTX_ID.equals( dltMsg.getExtendedHeader().getContextId() )
                && EXPECTED_APP_ID.equals( dltMsg.getExtendedHeader().getApplicationId() ))
        {

            if (dltMsg.getPayload().get( 1 ).startsWith( PER_PROC_MEMORY_FILE_NAME ))
            {
                result = true;

            }
        }
        return result;
    }

    private static boolean isDbusMessage(DltMessage dltMsg)
    {
        boolean result = false;

        String ctxID = dbusSources.get( dltMsg.getExtendedHeader().getApplicationId() );
        if (ctxID != null && ctxID.equals( dltMsg.getExtendedHeader().getContextId() ))
        {
            return true;
        }

        return result;
    }

}
