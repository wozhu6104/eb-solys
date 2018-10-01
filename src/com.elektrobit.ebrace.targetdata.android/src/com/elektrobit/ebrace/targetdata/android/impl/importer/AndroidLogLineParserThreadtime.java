/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.importer;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import com.elektrobit.ebrace.common.utils.JsonHelper;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

public class AndroidLogLineParserThreadtime extends AndroidLogLineParserAbstract
{

    public AndroidLogLineParserThreadtime(String _pattern, RuntimeEventAcceptor acceptor)
    {
        super( _pattern, acceptor );
    }

    @Override
    public char getLogLevel(Matcher matcher)
    {
        return matcher.group( LOG_LEVEL_GROUP ).charAt( 0 );
    }

    @Override
    public String getValue(Matcher matcher, BufferedReader reader)
    {
        return asJson( matcher );
    }

    @Override
    public List<String> getColumns()
    {
        return Arrays.asList( "PID", "TID", "Tag", "Value" );
    }

    private String getTag(String group)
    {
        return (group.substring( 0, group.length() - 1 )).trim();
    }

    private final int PID_GROUP = 2;
    private final int TID_GROUP = 3;
    private final int LOG_LEVEL_GROUP = 4;
    private final int TAG_GROUP = 5;
    private final int VALUE_GROUP = 6;

    @Override
    public String asJson(Matcher matcher)
    {
        String value = matcher.group( VALUE_GROUP );
        if (JsonHelper.isJson( value ))
        {
            return "{\"PID\":\"" + matcher.group( PID_GROUP ) + "\", \"TID\":\"" + matcher.group( TID_GROUP )
                    + "\", \"Tag\":\"" + getTag( matcher.group( TAG_GROUP ) ) + "\", \"Value\":" + value + "}";
        }

        else
        {
            return "{\"PID\":\"" + matcher.group( PID_GROUP ) + "\", \"TID\":\"" + matcher.group( TID_GROUP )
                    + "\", \"Tag\":\"" + getTag( matcher.group( TAG_GROUP ) ) + "\", \"Value\":\""
                    + value.replace( "\"", "\\\"" ) + "\"}";

        }
    }

    @Override
    public boolean supportsJson()
    {
        return true;
    }

}
