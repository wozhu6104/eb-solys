/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.logger;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class Log4jUtils
{
    private static boolean notInitialized = true;

    public synchronized static final void initializeLog4j()
    {
        if (notInitialized)
        {
            // URL urlToLog4JPropertiesFile = FileLocator.find( Platform.getBundle( "org.apache.log4j" ),
            // new Path( "log4j.properties" ),
            // null );

            String path = System.getProperty( "log4j.configuration" );

            PropertyConfigurator.configure( path );
            notInitialized = false;
        }
    }

    public static void initialSimpleConsoleLogger(final String logPackagePrefix, final String logLevel)
    {
        Properties log4JConsoleProperties = new Properties();
        log4JConsoleProperties.put( "log4j.rootLogger", "warn, default" );
        log4JConsoleProperties.put( "log4j.appender.default", "org.apache.log4j.ConsoleAppender" );
        log4JConsoleProperties.put( "log4j.appender.default.layout", "org.apache.log4j.PatternLayout" );
        log4JConsoleProperties.put( "log4j.appender.default.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n" );
        log4JConsoleProperties.put( "log4j.logger." + logPackagePrefix, logLevel );

        PropertyConfigurator.configure( log4JConsoleProperties );
    }
}
