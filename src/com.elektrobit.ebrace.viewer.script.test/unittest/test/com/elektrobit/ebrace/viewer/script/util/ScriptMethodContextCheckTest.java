/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.viewer.script.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebrace.viewer.script.util.ScriptMethodContextCheck;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.script.external.Matches;

public class ScriptMethodContextCheckTest
{

    private static RuntimeEventChannelMock<String> channel1;
    private static RuntimeEventChannelMock<String> channel2;
    private static RuntimeEventChannelMock<String> channel3;

    @BeforeClass
    public static void setup()
    {
        channel1 = new RuntimeEventChannelMock<String>( "my.test.1" );
        channel2 = new RuntimeEventChannelMock<String>( "my.test.2" );
        channel3 = new RuntimeEventChannelMock<String>( "your.test.1" );
    }

    @Test
    public void testEquals()
    {

        RaceScriptMethod method = new RaceScriptMethod( "", "", "", TestScriptClass1.class.getMethods()[0] );

        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel1 ) );
        assertFalse( ScriptMethodContextCheck.isChannelMatching( method, channel2 ) );
        assertFalse( ScriptMethodContextCheck.isChannelMatching( method, channel3 ) );
    }

    @Test
    public void testWildcard1()
    {
        RaceScriptMethod method = new RaceScriptMethod( "", "", "", TestScriptWildcard1.class.getMethods()[0] );

        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel1 ) );
        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel2 ) );
        assertFalse( ScriptMethodContextCheck.isChannelMatching( method, channel3 ) );

    }

    @Test
    public void testWildcard2()
    {

        RaceScriptMethod method = new RaceScriptMethod( "", "", "", TestScriptWildcard2.class.getMethods()[0] );

        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel1 ) );
        assertFalse( ScriptMethodContextCheck.isChannelMatching( method, channel2 ) );
        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel3 ) );

    }

    @Test
    public void testWildcard3()
    {
        RaceScriptMethod method = new RaceScriptMethod( "", "", "", TestScriptWildcard3.class.getMethods()[0] );

        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel1 ) );
        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel2 ) );
        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel3 ) );

    }

    @Test
    public void testWildcard4()
    {
        RaceScriptMethod method = new RaceScriptMethod( "", "", "", TestScriptWildcard4.class.getMethods()[0] );

        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel1 ) );
        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel2 ) );
        assertTrue( ScriptMethodContextCheck.isChannelMatching( method, channel3 ) );

    }

}

interface TestScriptClass1
{
    public void testMethod1(@Matches(name = "my.test.1") RuntimeEventChannel<?> channel);
}

interface TestScriptWildcard1
{
    public void testWildCard1(@Matches(name = "my.test.*") RuntimeEventChannel<?> channel);
}

interface TestScriptWildcard2
{
    public void testWildCard1(@Matches(name = "*.test.1") RuntimeEventChannel<?> channel);
}

interface TestScriptWildcard3
{
    public void testWildCard1(@Matches(name = "*.test.*") RuntimeEventChannel<?> channel);
}

interface TestScriptWildcard4
{
    public void testWildCard1(@Matches(name = "*") RuntimeEventChannel<?> channel);
}
