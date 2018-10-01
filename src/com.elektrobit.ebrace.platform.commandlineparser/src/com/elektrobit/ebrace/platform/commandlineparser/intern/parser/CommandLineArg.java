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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public final class CommandLineArg
{
    private final String commandLineArgKey;
    private final String commandLineArgValue;

    public CommandLineArg(String commandLineArgKey)
    {
        this( commandLineArgKey, "" );
    }

    public CommandLineArg(String commandLineArgKey, String commandLineArgValue)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "Command line arg key", commandLineArgKey );
        RangeCheckUtils.assertReferenceParameterNotNull( "Command line arg value", commandLineArgValue );

        this.commandLineArgKey = commandLineArgKey;
        this.commandLineArgValue = commandLineArgValue;
    }

    public String getCommandLineArgKey()
    {
        return commandLineArgKey;
    }

    public String getCommandLineArgValue()
    {
        return commandLineArgValue;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commandLineArgKey == null) ? 0 : commandLineArgKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommandLineArg other = (CommandLineArg)obj;
        if (commandLineArgKey == null)
        {
            if (other.commandLineArgKey != null)
                return false;
        }
        else if (!commandLineArgKey.equals( other.commandLineArgKey ))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "CommandLineArg [commandLineArgKey=" + commandLineArgKey + ", commandLineArgValue=" + commandLineArgValue
                + "]";
    }

}
