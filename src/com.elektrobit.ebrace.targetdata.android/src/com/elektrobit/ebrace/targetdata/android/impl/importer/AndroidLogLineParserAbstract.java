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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.common.collect.ImmutableMap;

public abstract class AndroidLogLineParserAbstract
{

    protected DateFormat formatter = new SimpleDateFormat( "MM-dd HH:mm:ss.S" );
    protected DateFormat formatterWithYear = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
    protected String pattern = null;
    protected RuntimeEventAcceptor runtimeEventAcceptor = null;
    private final int TO_MICROSECONDS = 1000;
    private final int TIMESTAMP_DELTA = 5 * 60 * 1000 * TO_MICROSECONDS; // 5 minutes
    private final int TIMESTAMP_GROUP = 1;
    protected final String CHANNEL_PREFIX = "trace.android.logs";
    Map<Character, String> logLevelMap = ImmutableMap.<Character, String> builder().put( 'A', "assert" )
            .put( 'F', "assert" ).put( 'D', "debug" ).put( 'E', "error" ).put( 'I', "info" ).put( 'V', "verbose" )
            .put( 'W', "warning" ).build();

    public abstract char getLogLevel(Matcher matcher);

    public abstract String getValue(Matcher matcher, BufferedReader reader);

    public AndroidLogLineParserAbstract(String _pattern, RuntimeEventAcceptor acceptor)
    {
        pattern = _pattern;
        runtimeEventAcceptor = acceptor;
    }

    public boolean acceptMessage(long timestamp, char logLevel, String value)
    {
        RuntimeEventChannel<String> channel = getChannel( logLevel );

        if (timestamp > -1 && channel != null)
        {
            runtimeEventAcceptor.acceptEventMicros( timestamp, channel, null, value );
            return true;
        }
        return false;
    }

    public RuntimeEventChannel<String> getChannel(char logLevel)
    {
        return toChannel( CHANNEL_PREFIX + "." + logLevelMap.get( logLevel ) );
    }

    private RuntimeEventChannel<String> toChannel(String channelname)
    {

        if (supportsJson())
        {
            return runtimeEventAcceptor.createOrGetRuntimeEventChannel( channelname, Unit.TEXT, "", getColumns() );
        }
        else
        {
            return runtimeEventAcceptor.createOrGetRuntimeEventChannel( channelname, Unit.TEXT, "" );
        }

    }

    public long getTimeStamp(Matcher matcher)
    {
        try
        {
            return formatter.parse( matcher.group( TIMESTAMP_GROUP ) ).getTime() * TO_MICROSECONDS;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public long getTimeStampWithPatchedYear(Matcher matcher, String year)
    {
        try
        {
            return formatterWithYear.parse( year + "-" + matcher.group( TIMESTAMP_GROUP ) ).getTime() * TO_MICROSECONDS;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public Matcher getMatcher(String line)
    {
        return Pattern.compile( pattern ).matcher( line.trim() );
    }

    public boolean processLine(String line, BufferedReader reader, Timestamp timestamp)
    {
        Matcher m = getMatcher( line );
        if (m.matches())
        {
            // log line comes from target adaptor and has an additional outer time-stamp
            // Compare the outer time-stamp (coming from target agent) with the inner time-stamp (coming from logcat)
            // and refuse logs that are older than a given delta
            if (timestamp != null)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis( timestamp.getTimeInMillis() );
                long timestampInLogCatMessage = getTimeStampWithPatchedYear( m,
                                                                             Integer.toString( cal
                                                                                     .get( Calendar.YEAR ) ) );
                long outerTimeStamp = timestamp.getTimeInMillis() * TO_MICROSECONDS;

                if (Math.abs( timestampInLogCatMessage - outerTimeStamp ) < TIMESTAMP_DELTA)
                {
                    return acceptMessage( timestampInLogCatMessage, getLogLevel( m ), getValue( m, reader ) );
                }
                else
                {
                    return false;
                }

            }

            // log line comes from importer
            else
            {
                return acceptMessage( getTimeStamp( m ), getLogLevel( m ), getValue( m, reader ) );
            }
        }
        else
        {
            return false;
        }
    }

    public abstract boolean supportsJson();

    public List<String> getColumns()
    {
        return null;
    }

    public String asJson(Matcher matcher)
    {
        return "";
    }

}
