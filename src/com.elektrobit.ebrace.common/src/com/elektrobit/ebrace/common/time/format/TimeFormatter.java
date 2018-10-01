/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.time.format;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeFormatter
{
    public final static String TIMESTAMP_MILLISECONDS = "TIMESTAMP_MILLISECONDS";

    public final static String TIMESTAMP_MICROSECONDS = "TIMESTAMP_MICROSECONDS";

    private final static String MICROS_PATTERN = "SSSSSS";

    private final static String MICROS_MARKER = "'MICROS_HERE'";
    private final static String MICROS_PROCESSED_MARKER = "MICROS_HERE";

    private SimpleDateFormat formatter = null;
    private String formatString = null;

    public TimeFormatter(String format)
    {
        format = format.replace( MICROS_PATTERN, MICROS_MARKER );

        if (format.equalsIgnoreCase( TIMESTAMP_MILLISECONDS ))
        {
            this.formatString = TIMESTAMP_MILLISECONDS;
            return;
        }

        if (format.equalsIgnoreCase( TIMESTAMP_MICROSECONDS ))
        {
            this.formatString = TIMESTAMP_MICROSECONDS;
            return;
        }

        formatter = new SimpleDateFormat( format );
    }

    public String formatMicros(long timestampUS)
    {
        if (formatter != null)
        {
            String result = formatMillis( timestampUS / 1000 );
            result = addMicrosIfRequired( result, timestampUS );
            return result;
        }
        else if (formatString != null)
        {
            return formatWithCustomString( timestampUS );
        }
        else
            return "";
    }

    private String formatMillis(long millis)
    {
        Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "GMT-0:00" ) );
        calendar.setTimeInMillis( millis );
        Date date = calendar.getTime();
        formatter.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        String formatted = formatter.format( date );

        return formatted;
    }

    private String addMicrosIfRequired(String formatedTimestamp, long timestampUS)
    {
        if (formatedTimestamp.contains( MICROS_PROCESSED_MARKER ))
        {
            String microsValue = String.valueOf( timestampUS % 1000000L );
            microsValue = addLeadingZeros( microsValue, 6 );
            return formatedTimestamp.replace( MICROS_PROCESSED_MARKER, microsValue );
        }
        else
            return formatedTimestamp;
    }

    private String addLeadingZeros(final String s, int expectedLength)
    {
        String result = s;
        while (result.length() < expectedLength)
        {
            result = '0' + result;
        }
        return result;
    }

    private String formatWithCustomString(long timestampUS)
    {
        if (formatString.equals( TIMESTAMP_MICROSECONDS ))
        {
            return String.valueOf( timestampUS );
        }
        if (formatString.equals( TIMESTAMP_MILLISECONDS ))
        {
            return String.valueOf( timestampUS / 1000 );
        }
        return "";
    }
}
