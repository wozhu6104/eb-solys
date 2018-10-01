/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.usagestatslogger.impl;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogHandler;

@Component(immediate = true, enabled = true)
public class Log4JLogHandler implements UseStatLogHandler
{
    private final Logger logger = Logger.getLogger( "usagestats" );

    @Override
    public void log(String usageStatsType)
    {
        logger.info( usageStatsType );
    }

}
