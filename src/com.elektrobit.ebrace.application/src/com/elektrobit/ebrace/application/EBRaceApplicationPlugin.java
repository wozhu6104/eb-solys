/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application;

import java.net.MalformedURLException;
import java.net.URI;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.elektrobit.ebrace.application.usagestatslogger.JvmStartupTimeParser;
import com.elektrobit.ebrace.application.usagestatslogger.Log4JLogHandler;
import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogHandler;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;

import lombok.extern.log4j.Log4j;

@Log4j
public class EBRaceApplicationPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "com.elektrobit.ebrace.application"; //$NON-NLS-1$
    private static EBRaceApplicationPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start( context );
        plugin = this;
        registerUsageStatsLogger( context );
        initializeLog4j();
        logLoggerInfo();
    }

    @UseStatLog(value = UseStatLogTypes.LOGGING_STARTED, parser = JvmStartupTimeParser.class)
    private void logLoggerInfo()
    {
        log.info( "Logger started." );
        log.info( "Found Log4j.properties in com.elektrobit.ebrace.dev.log4properties." );
    }

    private void registerUsageStatsLogger(BundleContext context)
    {
        context.registerService( UseStatLogHandler.class, new Log4JLogHandler(), null );
    }

    private final void initializeLog4j()
    {

        URI urlToLog4JPropertiesFile = FileHelper.locateFileInBundle( "com.elektrobit.ebrace.dev.log4jproperties",
                                                                      "/log4j.properties" );

        try
        {
            PropertyConfigurator.configure( urlToLog4JPropertiesFile.toURL() );
        }
        catch (MalformedURLException e)
        {
            System.err.println( "Couldn't initialize logger, because log4j.properties file was not found!" );
        }
    }

    public static EBRaceApplicationPlugin getDefault()
    {
        return plugin;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

}
