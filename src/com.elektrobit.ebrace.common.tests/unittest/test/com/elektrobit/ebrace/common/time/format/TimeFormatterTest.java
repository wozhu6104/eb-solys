/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.time.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;

public class TimeFormatterTest
{
    @Test
    public void testFormatWithMilis() throws Exception
    {
        TimeFormatter timeFormatter = new TimeFormatter( "dd/MM/yyyy HH:mm:ss:SSS" );

        assertEquals( "01/01/1970 00:00:01:000", timeFormatter.formatMicros( 1000000 ) );
    }

    @Test
    public void testFormatWithMicros() throws Exception
    {
        TimeFormatter timeFormatter = new TimeFormatter( "dd/MM/yyyy HH:mm:ss:SSSSSS" );

        String expected = "01/01/1970 00:00:00:001000";
        String result = timeFormatter.formatMicros( 1000 );
        assertEquals( expected, result );
    }

    @Test
    public void testFormatWithMicrosAndSec() throws Exception
    {
        TimeFormatter timeFormatter = new TimeFormatter( "dd/MM/yyyy HH:mm:ss:SSSSSS" );

        String expected = "01/01/1970 00:00:01:234567";
        String result = timeFormatter.formatMicros( 1234567 );
        assertEquals( expected, result );
    }

    @Test
    public void testFormatWithZeroMicros() throws Exception
    {
        TimeFormatter timeFormatter = new TimeFormatter( "dd/MM/yyyy HH:mm:ss:SSSSSS" );

        String expected = "01/01/1970 00:00:00:000000";
        String result = timeFormatter.formatMicros( 0 );
        assertEquals( expected, result );
    }

    @Test
    public void testFormatMilisFormat() throws Exception
    {
        TimeFormatter timeFormatter = new TimeFormatter( TimeFormatter.TIMESTAMP_MILLISECONDS );

        String expected = "1";
        String result = timeFormatter.formatMicros( 1234 );
        assertEquals( expected, result );
    }

    @Test
    public void testFormatMicrosFormat() throws Exception
    {
        TimeFormatter timeFormatter = new TimeFormatter( TimeFormatter.TIMESTAMP_MICROSECONDS );

        String expected = "1234";
        String result = timeFormatter.formatMicros( 1234 );
        assertEquals( expected, result );
    }
}
