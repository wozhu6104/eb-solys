/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt;

import java.util.Locale;

import org.eclipse.birt.chart.model.attribute.JavaDateFormatSpecifier;
import org.eclipse.birt.chart.model.attribute.impl.FormatSpecifierImpl;

import com.elektrobit.ebrace.common.time.format.TimeFormatter;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

public class ChartTimeFormatter extends FormatSpecifierImpl implements JavaDateFormatSpecifier
{
    private final String DEFAULT_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";
    private TimeFormatter timeFormatter = new TimeFormatter( DEFAULT_TIMESTAMP_FORMAT );
    private String pattern = DEFAULT_TIMESTAMP_FORMAT;

    @Override
    public String format(Calendar cal, Locale locale)
    {
        String formattedTime = format( cal );
        return formattedTime;
    }

    private String format(Calendar cal)
    {
        long microseconds = cal.getTime().getTime();
        String formattedTime = timeFormatter.formatMicros( microseconds );
        return formattedTime;
    }

    @Override
    public String format(Calendar cal, ULocale locale)
    {
        String formattedTime = format( cal );
        return formattedTime;
    }

    @Override
    public String getPattern()
    {
        throw new IllegalAccessError( "this method should not be used by birt - format will not be recognizable" );
    }

    @Override
    public void setPattern(String pattern)
    {
        this.pattern = pattern;
        timeFormatter = new TimeFormatter( pattern );
    }

    @Override
    public JavaDateFormatSpecifier copyInstance()
    {
        ChartTimeFormatter chartTimeFormatter = new ChartTimeFormatter();
        chartTimeFormatter.setPattern( pattern );
        return chartTimeFormatter;
    }
}
