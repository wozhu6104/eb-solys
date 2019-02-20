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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.utils.ByteArrayHelper;
import com.elektrobit.ebrace.common.utils.StringHelper;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebrace.targetadapter.communicator.api.OutgoingMessage;
import com.google.gson.JsonObject;

import lombok.Data;

@Data
public class DltMessage implements OutgoingMessage
{
    /*
     * Every DLT message has a 16 Byte storage header (optional), 4, 8, 12 or 16 Byte standard header (mandatory) 10
     * Byte extended header (optional - depends on flag in standard header)
     */

    private DltStandardHeader standardHeader;

    private DltExtendedHeader extendedHeader;

    byte[] payloadBuffer;

    private List<String> payload = new ArrayList<String>();

    private List<byte[]> outPayload = new ArrayList<byte[]>();

    public byte[] marshal()
    {
        byte[] payloadBytes = joinPayloadEntries();

        standardHeader.setMessageLength( (short)(standardHeader.getLength() + DltExtendedHeader.LENGTH
                + payloadBytes.length) );
        ByteBuffer buffer = ByteBuffer.allocate( standardHeader.getMessageLength() );
        buffer.put( standardHeader.getBytes() );
        buffer.put( extendedHeader.getBytes() );
        buffer.put( payloadBytes );
        return buffer.array();
    }

    private byte[] joinPayloadEntries()
    {
        byte[] payload = new byte[0];
        for (byte[] item : outPayload)
        {
            payload = ByteArrayHelper.appendBytes( payload, item );
        }
        return payload;
    }

    public void addPayloadItem(int serviceId)
    {
        byte[] bytes = ByteBuffer.allocate( 4 ).order( ByteOrder.LITTLE_ENDIAN ).putInt( serviceId ).array();
        outPayload.add( bytes );
    }

    public void addPayloadItem(byte[] item)
    {
        outPayload.add( item );
    }

    @Override
    public byte[] toByteArray()
    {
        return marshal();
    }

    public void setPayloadBuffer(byte[] payloadBuffer)
    {
        this.payloadBuffer = payloadBuffer;
    }

    public byte[] toByteArrayWithUnparsedPayload()
    {
        standardHeader.setMessageLength( (short)(standardHeader.getLength() + DltExtendedHeader.LENGTH
                + payloadBuffer.length) );
        ByteBuffer buffer = ByteBuffer.allocate( standardHeader.getMessageLength() );
        buffer.put( standardHeader.getBytes() );
        buffer.put( extendedHeader.getBytes() );
        buffer.put( payloadBuffer );
        return buffer.array();
    }

    @Override
    public String toString()
    {
        return "DltMessage";
    }

    public JsonEventValue constructJsonEventValue()
    {
        JsonObject value = new JsonObject();
        value.addProperty( "appId", extendedHeader.getApplicationId() );
        value.addProperty( "contextId", extendedHeader.getContextId() );
        value.addProperty( "numArgs", extendedHeader.getNumberOfArguments() );
        value.addProperty( "logLevel", StringHelper.extractLast( extendedHeader.getMessageTypeInfo(), "_" ) );
        JsonObject payloadObj = constructPayload();
        value.add( "payload", payloadObj );
        return new JsonEventValue( "", value );
    }

    private JsonObject constructPayload()
    {
        JsonObject payloadObject = new JsonObject();
        int i = 0;
        for (String param : payload)
        {

            payloadObject.addProperty( "" + i++, filterUnicodeWhitespaces( param ) );
        }
        return payloadObject;
    }

    private String filterUnicodeWhitespaces(String orig)
    {
        return orig.replaceAll( "\\\\u\\d{4}", "" );
    }

    // TODO To be changed when new json format is introduced
    public String toJson()
    {
        JsonEventValue value = constructJsonEventValue();
        JsonEvent event = new JsonEvent( (long)standardHeader.getTimeStamp(),
                                         "trace.dlt." + extendedHeader.getApplicationId() + "."
                                                 + extendedHeader.getContextId(),
                                         value,
                                         0l,
                                         null );
        return event.toString();
    }

}
