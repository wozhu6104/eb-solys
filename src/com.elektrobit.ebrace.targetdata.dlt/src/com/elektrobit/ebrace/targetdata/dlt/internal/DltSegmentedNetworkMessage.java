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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.extern.log4j.Log4j;

@Log4j
public class DltSegmentedNetworkMessage
{
    private final static int NR_OF_ITEMS_IN_HEADER = 6;
    private static HashMap<Integer, DltNetworkMessage> networkMessage = new HashMap<Integer, DltNetworkMessage>();

    public static boolean isNetworkMessage(DltMessage dltMsg)
    {
        boolean retVal = false;
        if (isNetworkMessagePayload( dltMsg ) || isNetworkMessageHeader( dltMsg ) || isNetworkMessageEnd( dltMsg ))
        {
            retVal = true;
        }
        return retVal;
    }

    private static boolean isNetworkMessagePayload(DltMessage dltMsg)
    {
        return dltMsg.getPayload().toString().contains( "NWCH" );
    }

    private static boolean isNetworkMessageHeader(DltMessage dltMsg)
    {
        return dltMsg.getPayload().toString().contains( "NWST" );
    }

    private static boolean isNetworkMessageEnd(DltMessage dltMsg)
    {
        return dltMsg.getPayload().toString().contains( "NWEN" );
    }

    public static String handleSegmentedMessage(DltMessage dltMsg)
    {
        if (isNetworkMessageHeader( dltMsg ))
        {
            parseNwMsgHeader( dltMsg );
        }
        else if (isNetworkMessagePayload( dltMsg ))
        {
            DltNetworkMessage nwMessage = parseNwMsgPayload( dltMsg );
            return evaluateMessageComplete( nwMessage );
        }

        return null;
    }

    private static void parseNwMsgHeader(DltMessage dltMsg)
    {
        if (dltMsg.getPayload().size() == NR_OF_ITEMS_IN_HEADER)
        {
            int msgHandle = DbusNwTraceTokens.getMessageHandle( dltMsg );
            DltNetworkMessage previousHeader = networkMessage.get( msgHandle );
            if (previousHeader == null)
            {
                previousHeader = new DltNetworkMessage();
                networkMessage.put( msgHandle, previousHeader );
            }
            previousHeader.setHandle( msgHandle );
            previousHeader.setHeader( DbusNwTraceTokens.getMessageHeader( dltMsg ) );
            previousHeader.setPayloadSize( DbusNwTraceTokens.getPayloadSize( dltMsg ) );
            previousHeader.setNumberOfChunks( DbusNwTraceTokens.getNumberOfChunks( dltMsg ) );
            previousHeader.setChunkMaxSize( DbusNwTraceTokens.getChunkMaxSize( dltMsg ) );
        }
        else
        {
            log.warn( "DLT Network Message: header incomplete" );
        }
    }

    private static DltNetworkMessage parseNwMsgPayload(DltMessage dltMsg)
    {
        DltNetworkMessage nwMessage = networkMessage.get( DbusNwTraceTokens.getMessageHandle( dltMsg ) );
        if (nwMessage != null)
        {
            if (Integer.parseInt( DbusNwTraceTokens.getMessageHeader( dltMsg ) ) > nwMessage.getNumberOfChunks())
            {
                log.error( "Current chunk is out of the expected range" + "current "
                        + Integer.parseInt( DbusNwTraceTokens.getMessageHeader( dltMsg ) ) + "expected    "
                        + nwMessage.getNumberOfChunks() );
                return null;
            }

            nwMessage.getChunks().put( Integer.parseInt( DbusNwTraceTokens.getMessageHeader( dltMsg ) ), dltMsg );
        }
        else
        {
            nwMessage = new DltNetworkMessage();
            nwMessage.setHandle( DbusNwTraceTokens.getMessageHandle( dltMsg ) );
            nwMessage.getChunks().put( Integer.parseInt( DbusNwTraceTokens.getMessageHeader( dltMsg ) ), dltMsg );
            networkMessage.put( nwMessage.getHandle(), nwMessage );
        }
        return nwMessage;
    }

    private static String evaluateMessageComplete(DltNetworkMessage nwMessage)
    {
        String returnValue = "";

        if (nwMessage != null && nwMessage.getChunks().size() >= nwMessage.getNumberOfChunks())
        {
            for (DltMessage crtChunk : nwMessage.getChunks().values())
            {
                returnValue += DbusNwTraceTokens.getMessagePayload( crtChunk );
            }
            log.info( "complete network message received: " + returnValue );
            return returnValue;
        }
        return null;
    }

    public static List<DltMessage> retrieveAllTokens(DltMessage nwMessage)
    {
        DltNetworkMessage nw = networkMessage.get( DbusNwTraceTokens.getMessageHandle( nwMessage ) );

        if (nw != null)
        {
            return new ArrayList<DltMessage>( nw.getChunks().values() );

        }
        else
        {
            return null;
        }
    }

    public static void disposeMessage(DltMessage nwMessage)
    {
        DltNetworkMessage nw = networkMessage.get( DbusNwTraceTokens.getMessageHandle( nwMessage ) );

        if (nw != null)
        {
            networkMessage.remove( DbusNwTraceTokens.getMessageHandle( nwMessage ) );
        }

    }

    public static boolean isLastToken(DltMessage dltMsg)
    {
        DltNetworkMessage nwMessage = networkMessage.get( DbusNwTraceTokens.getMessageHandle( dltMsg ) );
        if (nwMessage != null && (nwMessage.getNumberOfChunks() == nwMessage.getChunks().size()))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public static List<DltMessage> queryRemainingData()
    {
        ArrayList<DltMessage> rez = new ArrayList<DltMessage>();
        for (Entry<Integer, DltNetworkMessage> entry : networkMessage.entrySet())
        {
            rez.addAll( (entry.getValue().getChunks().values()) );
        }
        return rez;

    }

    public static void clear()
    {
        networkMessage.clear();
    }
}
