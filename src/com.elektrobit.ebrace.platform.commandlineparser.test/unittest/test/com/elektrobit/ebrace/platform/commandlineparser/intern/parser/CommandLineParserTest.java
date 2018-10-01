/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.platform.commandlineparser.intern.parser;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.logger.Log4jUtils;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;
import com.elektrobit.ebrace.platform.commandlineparser.intern.parser.CommandLineParserImpl;

public class CommandLineParserTest
{
    @Before
    public void setup()
    {
        Log4jUtils.initialSimpleConsoleLogger( "com.elektrobit.ebrace", "info" );
    }

    @Test
    public void parseCommandLine() throws ValueOfArgumentMissingException
    {
        String[] commandLineArgs = {"-f=file.text", "-p=8080", "-u=123.456.789", "-o"};
        CommandLineParser commandLineParser = new CommandLineParserImpl( Arrays.asList( commandLineArgs ) );

        Assert.assertTrue( commandLineParser.hasArg( "-f" ) );
        Assert.assertTrue( commandLineParser.hasArg( "-p" ) );
        Assert.assertTrue( commandLineParser.hasArg( "-u" ) );
        Assert.assertTrue( commandLineParser.hasArg( "-o" ) );

        Assert.assertEquals( "file.text", commandLineParser.getValue( "-f" ) );
        Assert.assertEquals( "8080", commandLineParser.getValue( "-p" ) );
        Assert.assertEquals( "123.456.789", commandLineParser.getValue( "-u" ) );
        Assert.assertEquals( "", commandLineParser.getValue( "-o" ) );
    }

    @Test
    public void commandLineArgumentNotAvailable()
    {
        String[] commandLineArgs = {"-f=file.text"};
        CommandLineParser commandLineParser = new CommandLineParserImpl( Arrays.asList( commandLineArgs ) );

        try
        {
            commandLineParser.getValue( "-notAvailable" );
            Assert.fail( "Expected to get an IllegalArgumentException." );
        }
        catch (ValueOfArgumentMissingException e)
        {
            Assert.assertTrue( true );
        }
    }

    @Test
    public void wrongCommandLineArg()
    {
        String[] commandLineArgs = {"-wrongArg=arg1=arg2"};
        try
        {
            new CommandLineParserImpl( Arrays.asList( commandLineArgs ) );
            Assert.fail( "Expected to get an IllegalArgumentException." );
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue( true );
        }
    }

}
