/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application.usagestatslogger;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogParamParser;

public class JvmStartupTimeParser implements UseStatLogParamParser
{

    @Override
    public String parse(Object[] args)
    {
        String jvmStartUpTimeFormatted = new SimpleDateFormat( "MM-dd-yyyy_HH:mm:ss.SSS" )
                .format( new Date( ManagementFactory.getRuntimeMXBean().getStartTime() ) );

        return "{" + "\"JvmStartupTime\":\"" + jvmStartUpTimeFormatted + "\"" + "}";
    }

}
