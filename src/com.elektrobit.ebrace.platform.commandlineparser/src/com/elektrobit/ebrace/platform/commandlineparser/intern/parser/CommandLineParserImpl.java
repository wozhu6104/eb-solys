/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.platform.commandlineparser.intern.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;

@Component(immediate = true)
public class CommandLineParserImpl implements CommandLineParser
{
    private final String separater = "=";
    private final Map<String, CommandLineArg> map = new HashMap<String, CommandLineArg>();

    public CommandLineParserImpl()
    {
        this( Arrays.asList( Platform.getCommandLineArgs() ) );
    }

    public CommandLineParserImpl(List<String> commandLineArgs)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "Command line arguments", commandLineArgs );

        parseCommandLineArgs( commandLineArgs );
    }

    private void parseCommandLineArgs(List<String> commandLineArgs)
    {
        for (String nextCommandLineArg : commandLineArgs)
        {
            CommandLineArg commandLineArg = parseCommandLineArg( nextCommandLineArg );
            map.put( commandLineArg.getCommandLineArgKey(), commandLineArg );
        }
    }

    private CommandLineArg parseCommandLineArg(String nextCommandLineArg)
    {
        String[] splitCommandLineArg = nextCommandLineArg.split( separater );

        CommandLineArg commandLineArg = null;

        if (splitCommandLineArg.length == 1)
        {
            String commandLineArgKey = splitCommandLineArg[0];
            commandLineArg = new CommandLineArg( commandLineArgKey );
        }
        else if (splitCommandLineArg.length == 2)
        {
            String commandLineArgKey = splitCommandLineArg[0];
            String commandLineArgValue = splitCommandLineArg[1];

            commandLineArg = new CommandLineArg( commandLineArgKey, commandLineArgValue );
        }
        else
        {
            throw new IllegalArgumentException( "Couldn't parse command line arg: " + nextCommandLineArg );
        }

        return commandLineArg;
    }

    @Override
    public boolean hasArg(String key)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "Command line arg", key );

        return map.containsKey( key );
    }

    @Override
    public String getValue(String key) throws ValueOfArgumentMissingException
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "Command line arg", key );

        if (!map.containsKey( key ))
        {
            throw new ValueOfArgumentMissingException( "Didn't find a command line arg with key " + key );
        }

        return map.get( key ).getCommandLineArgValue();
    }

}
