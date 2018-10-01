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

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogHandler;

public class Log4JLogHandler implements UseStatLogHandler
{
    private final Logger logger = Logger.getLogger( "usagestats" );

    @Override
    public void log(String usageStatsType)
    {
        System.out.println( "LOG | " + usageStatsType + " | " + System.currentTimeMillis() );
        logger.info( usageStatsType );
    }

}
