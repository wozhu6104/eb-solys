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

public class DbusNwTraceTokens
{
    public static int getMessageHandle(DltMessage nwMsg)
    {
        return Integer.parseInt( nwMsg.getPayload().get( 1 ) );
    }

    public static String getMessageHeader(DltMessage nwMsg)
    {
        return nwMsg.getPayload().get( 2 );
    }

    public static long getPayloadSize(DltMessage nwMsg)
    {
        return Long.parseLong( nwMsg.getPayload().get( 3 ) );
    }

    public static int getNumberOfChunks(DltMessage nwMsg)
    {
        return Integer.parseInt( nwMsg.getPayload().get( 4 ) );
    }

    public static int getChunkMaxSize(DltMessage nwMsg)
    {
        return Integer.parseInt( nwMsg.getPayload().get( 5 ) );
    }

    public static String getMessagePayload(DltMessage nwMsg)
    {
        return nwMsg.getPayload().get( 3 );
    }
}
