/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.de.systemticks.ebrace.eventhooks.regextochannelhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class InstCommHookRegexTest
{
    @Test
    public void testName() throws Exception
    {
        String expr = "(?<source>([A-Za-z]|[0-9]|[\\.])+)->(?<dest>([A-Za-z]|[0-9]|[\\.])+)\\|(?<type>(REQ|RES)+)\\|(?<payload>.*)";
        String example = "APP1.MOD1->APP2.MOD2|REQ|{\"method\":\"setSpeed\",\"speed\":100}";

        Pattern pattern = Pattern.compile( expr );
        Matcher matcher = pattern.matcher( example );
        matcher.find();

        assertEquals( "APP1.MOD1", matcher.group( "source" ) );
        assertEquals( "APP2.MOD2", matcher.group( "dest" ) );
        assertEquals( "REQ", matcher.group( "type" ) );
        assertEquals( "{\"method\":\"setSpeed\",\"speed\":100}", matcher.group( "payload" ) );

    }

    @Test
    public void bla() throws Exception
    {
        String expr = "([A-Za-z]|[0-9])+";
        String example = "hello1234";

        Pattern pattern = Pattern.compile( expr );
        Matcher matcher = pattern.matcher( example );
        assertTrue( matcher.matches() );
    }
}
