/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptInfoChangedListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;

public class RaceScriptChangedListenerTest
{
    private final String pathToRaceScripts = "resources/jars/";
    private final String pathToBundleRootFolder = FileHelper.getBundleRootFolderOfClass( this.getClass() );
    private final String pathToScript = (pathToBundleRootFolder + pathToRaceScripts
            + "SameMethodNameExampleScript.jar");
    private final CountDownLatch latch = new CountDownLatch( 1 );
    private final RaceScriptLoader raceScriptLoader = CoreServiceHelper.getRaceScriptLoader();
    private RaceScriptInfoChangedListener listener;
    private RaceScript scriptToRun;
    private final ScriptExecutorService scriptExecutorService = CoreServiceHelper.getScriptExecutorService();

    @Before
    public void setup()
    {
        scriptToRun = raceScriptLoader
                .loadRaceScript( new ScriptData( "SameMethodNameExampleScript", null, pathToScript ) );
        listener = new RaceScriptInfoChangedListener()
        {
            @Override
            public void scriptInfoChanged(RaceScript script)
            {
                latch.countDown();
            }

            @Override
            public void filterMethodsChanged(RaceScript script, List<RaceScriptMethod> filterMethods)
            {
            }
        };
    }

    @Test
    public void scriptChangedCalledAfterRegisterListener() throws Exception
    {
        raceScriptLoader.registerRaceScriptChangedListener( listener );

        scriptExecutorService.runScriptWithGlobalMethod( scriptToRun, "executeScript" );

        assertScriptChangedMethodCalled();
    }

    private void assertScriptChangedMethodCalled() throws InterruptedException
    {
        boolean result = latch.await( 50, TimeUnit.MILLISECONDS );
        if (!result)
        {
            fail( "scriptInfoChanged was not called on run global script." );
        }
    }

    @Test
    public void scriptChangedNotCalledAfterUnregisterListener() throws Exception
    {
        raceScriptLoader.registerRaceScriptChangedListener( listener );
        raceScriptLoader.unregisterRaceScriptChangedListener( listener );

        scriptExecutorService.runScriptWithGlobalMethod( scriptToRun, "executeScript" );

        assertScriptChangedMethodNotCalled();
    }

    private void assertScriptChangedMethodNotCalled() throws InterruptedException
    {
        boolean result = latch.await( 50, TimeUnit.MILLISECONDS );
        if (result)
        {
            fail( "scriptInfoChanged was called on run global script." );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void notAllowedToRegisterNullListener()
    {
        raceScriptLoader.registerRaceScriptChangedListener( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void notAllowedToUnregisterNullListener()
    {
        raceScriptLoader.unregisterRaceScriptChangedListener( null );
    }

}
