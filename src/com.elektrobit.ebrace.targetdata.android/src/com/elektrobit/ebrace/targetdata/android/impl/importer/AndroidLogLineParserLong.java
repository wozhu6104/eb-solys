/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;

public class AndroidLogLineParserLong extends AndroidLogLineParserAbstract
{

    public AndroidLogLineParserLong(String _pattern, RuntimeEventAcceptor acceptor)
    {
        super( _pattern, acceptor );
    }

    @Override
    public char getLogLevel(Matcher matcher)
    {
        return matcher.group( 2 ).trim().split( " " )[2].trim().charAt( 0 );
    }

    @Override
    public String getValue(Matcher matcher, BufferedReader reader)
    {

        String result = "";

        try
        {
            reader.readLine();
            result = matcher.group( 2 ) + reader.readLine();

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean supportsJson()
    {
        return false;
    }

}
