/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.targetdata.api.json;

import lombok.Data;

@Data
public class JsonEvent
{
    private final Long uptime;
    private final String channel;
    private final JsonEventValue value;
    private final Long duration;
    private final JsonEventEdge edge;

    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer( "{" );
        buf.append( "\"uptime\" : " ).append( uptime ).append( "," );
        buf.append( "\"channel\" : \"" ).append( channel ).append( "\"," );
        buf.append( "\"value\" : " ).append( value.toString() ).append( "," );
        buf.append( "\"duration\" : " ).append( duration ).append( "}" );

        return buf.toString();

        // return toGenericJson();
    }

    // private String toGenericJson()
    // {
    // StringBuffer buf = new StringBuffer( "{" );
    // buf.append( "\"timestamp\" : " ).append( uptime ).append( "," );
    // buf.append( "\"channel\" : \"" ).append( channel ).append( "\"," );
    // buf.append( "\"value\" : " ).append( "\"\"" ).append( "," );
    // buf.append( "\"details\" : " ).append( value.getDetails().toString() ).append( "}" );
    //
    // return buf.toString();
    // }
}
