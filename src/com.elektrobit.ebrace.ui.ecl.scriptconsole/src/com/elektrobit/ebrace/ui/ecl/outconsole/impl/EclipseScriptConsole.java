/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.outconsole.impl;

import org.eclipse.ui.console.MessageConsoleStream;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.viewer.console.ConsoleAdministrator;
import com.elektrobit.ebsolys.script.external.Console;

public class EclipseScriptConsole implements Console
{
    private MessageConsoleStream messageConsoleStream = null;
    private final String consoleName;

    public EclipseScriptConsole(final String consoleName)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "Name of ui console", consoleName );

        this.consoleName = consoleName;
    }

    @Override
    public void print(final String message)
    {
        findAndPinCorrespondingConsole();
        getMessageConsoleStream().print( message );
    }

    @Override
    public void println(final String message)
    {
        findAndPinCorrespondingConsole();
        getMessageConsoleStream().println( message );
    }

    @Override
    public void println()
    {
        findAndPinCorrespondingConsole();
        getMessageConsoleStream().println();
    }

    @Override
    public void clear()
    {
        if (messageConsoleStream != null)
        {
            messageConsoleStream.getConsole().clearConsole();
        }
    }

    private void findAndPinCorrespondingConsole()
    {
        ConsoleAdministrator.displayConsole( consoleName );
        getMessageConsoleStream().getConsole().activate();
    }

    private MessageConsoleStream getMessageConsoleStream()
    {
        if (messageConsoleStream == null)
        {
            messageConsoleStream = ConsoleAdministrator.getMessageConsoleStreamOfConsoleName( consoleName );
        }
        return messageConsoleStream;
    }
}
