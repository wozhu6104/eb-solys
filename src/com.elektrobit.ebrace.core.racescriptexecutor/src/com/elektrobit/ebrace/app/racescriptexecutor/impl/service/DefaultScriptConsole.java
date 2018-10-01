/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import com.elektrobit.ebsolys.script.external.Console;

public class DefaultScriptConsole implements Console
{

    @Override
    public void print(final String message)
    {
        System.out.print( message );
    }

    @Override
    public void println(final String message)
    {
        System.out.println( message );
    }

    @Override
    public void println()
    {
        System.out.println();
    }

    @Override
    public void clear()
    {
    }

}
