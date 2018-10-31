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

import com.elektrobit.ebrace.common.utils.ByteArrayHelper;
import com.elektrobit.ebrace.common.utils.StringHelper;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.google.gson.JsonObject;

import lombok.Data;

@Data
public class DltMessage
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

    public byte[] serialize()
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

    public void setPayloadBuffer(byte[] payloadBuffer)
    {
        this.payloadBuffer = payloadBuffer;
    }

    public String toJson()
    {
        JsonEventValue value = constructJsonEventValue();
        JsonEvent event = new JsonEvent( (long)standardHeader.getTimeStamp(),
                                         "trace.dlt." + extendedHeader.getApplicationId() + "."
                                                 + extendedHeader.getContextId(),
                                         value,
                                         null,
                                         null );
        return event.toString();
    }

    public JsonEventValue constructJsonEventValue()
    {
        JsonObject value = new JsonObject();
        value.addProperty( "appId", extendedHeader.getApplicationId() );
        value.addProperty( "contextId", extendedHeader.getContextId() );
        value.addProperty( "numArgs", extendedHeader.getNumberOfArguments() );
        value.addProperty( "logLevel", StringHelper.extractLast( extendedHeader.getMessageTypeInfo(), "_" ) );
        return new JsonEventValue( constructPayload().toString(), value );
    }

    private JsonObject constructPayload()
    {
        JsonObject payloadObject = new JsonObject();
        int i = 0;
        for (String param : payload)
        {
            payloadObject.addProperty( "" + i++, param );
        }
        return payloadObject;
    }
}
