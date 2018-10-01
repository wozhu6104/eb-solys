/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.RaceScriptHelper;

public class RaceScriptHelperTest
{
    @Test
    public void scriptNameFromPathWinSlash() throws Exception
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // This path is only valid on Windows.
            // That's why we execute this test only on Windows.
            String name = RaceScriptHelper.getScriptNameFromPath( "C:\\folder\\AnotherFolder\\myscript.jar" );
            Assert.assertEquals( "myscript", name );
        }
    }

    @Test
    public void scriptNameFromPathLinSlash() throws Exception
    {
        String name = RaceScriptHelper.getScriptNameFromPath( "/folder/AnotherFolder/myscript.jar" );
        Assert.assertEquals( "myscript", name );
    }

    @Test
    public void runJarPathCorrectOnWindows() throws Exception
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // This path is only valid on Windows.
            // That's why we execute this test only on Windows.
            assertEquals( "e:\\tmp\\myscript-run.jar",
                          RaceScriptHelper.createRunJarFilePath( "e:\\tmp\\myscript.jar" ) );
        }

    }

    @Test
    public void runJarPathCorrectOnLinux() throws Exception
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            assertEquals( "/tmp/myscript-run.jar", RaceScriptHelper.createRunJarFilePath( "/tmp/myscript.jar" ) );
        }

    }
}
