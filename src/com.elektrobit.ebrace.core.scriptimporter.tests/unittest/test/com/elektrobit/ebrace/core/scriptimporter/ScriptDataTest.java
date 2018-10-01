/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.scriptimporter;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.SystemUtils;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;

public class ScriptDataTest
{
    @Test
    public void preinstalledScriptWindowsSyntaxCorrect() throws Exception
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            ScriptData scriptData = new ScriptData( "Example",
                                                    "e:\\tmp\\RaceScripts\\src\\api\\Example.xtend",
                                                    "e:\\tmp\\scripts\\Example.jar" );
            assertTrue( scriptData.isPreinstalledScript() );
        }
    }

    @Test
    public void preinstalledScriptUnixSyntaxCorrect() throws Exception
    {
        ScriptData scriptData = new ScriptData( "Example",
                                                "/tmp/RaceScripts/src/api/Example.xtend",
                                                "/tmp/scripts/Example.jar" );
        assertTrue( scriptData.isPreinstalledScript() );
    }

    @Test
    public void userScriptWindowsSyntaxCorrect() throws Exception
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            ScriptData scriptData = new ScriptData( "Example",
                                                    "e:\\tmp\\RaceScripts\\src\\Example.xtend",
                                                    "e:\\tmp\\scripts\\Example.jar" );
            assertTrue( scriptData.isUserScript() );
        }
    }

    @Test
    public void userScriptUnixSyntaxCorrect() throws Exception
    {
        ScriptData scriptData = new ScriptData( "Example",
                                                "/tmp/RaceScripts/src/Example.xtend",
                                                "/tmp/scripts/Example.jar" );
        assertTrue( scriptData.isUserScript() );
    }
}
