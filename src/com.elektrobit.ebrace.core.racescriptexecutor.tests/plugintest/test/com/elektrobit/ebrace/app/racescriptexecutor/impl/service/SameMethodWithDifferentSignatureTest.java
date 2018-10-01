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

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.script.external.Console;

public class SameMethodWithDifferentSignatureTest implements Console
{
    private final String pathToRaceScripts = "resources/jars/";
    private final CountDownLatch latch = new CountDownLatch( 1 );
    private volatile String consoleMessage;
    private RaceScript raceScript;
    private ScriptExecutorService scriptExecutorService;

    @Before
    public void setup()
    {
        String pathToBundleRootFolder = FileHelper.getBundleRootFolderOfClass( this.getClass() );
        String pathToScript = (pathToBundleRootFolder + pathToRaceScripts + "SameMethodNameExampleScript.jar");

        RaceScriptLoader raceScriptLoader = CoreServiceHelper.getRaceScriptLoader();
        scriptExecutorService = CoreServiceHelper.getScriptExecutorService();

        raceScript = raceScriptLoader
                .loadRaceScript( new ScriptData( "SameMethodNameExampleScript", null, pathToScript ), this );
    }

    // @Test
    // public void hasRuntimeEventListMethodOneParameter() throws Exception
    // {
    // Assert.assertEquals( List.class,
    // raceScript.getRuntimeEventListMethods().get( 0 ).getMethod().getParameterTypes()[0] );
    // }

    @Test
    public void globalMethodCalledIfNoParam()
    {
        scriptExecutorService.runScriptWithGlobalMethod( raceScript, "executeScript" );

        waitMax1SecForScriptExecution();

        Assert.assertEquals( "Global", consoleMessage );
    }

    @Test
    public void preselectionListMethodCalledIfListIsParam()
    {
        scriptExecutorService.runScriptWithRuntimeEventsPreselection( raceScript,
                                                                      "executeScript",
                                                                      new ArrayList<RuntimeEvent<?>>() );

        waitMax1SecForScriptExecution();

        Assert.assertEquals( "List", consoleMessage );
    }

    @Test
    public void preselectionMarkerMethodCalledIfMarkerIsParam()
    {
        scriptExecutorService.runScriptWithTimeMarkerPreselection( raceScript,
                                                                   "executeScript",
                                                                   Mockito.mock( TimeMarker.class ) );

        waitMax1SecForScriptExecution();

        Assert.assertEquals( "Marker", consoleMessage );
    }

    @Test
    public void preselectionChannelMethodCalledIfChannelIsParam()
    {
        scriptExecutorService.runScriptWithChannelPreselection( raceScript,
                                                                "executeScript",
                                                                Mockito.mock( RuntimeEventChannel.class ) );
        waitMax1SecForScriptExecution();

        Assert.assertEquals( "Channel", consoleMessage );
    }

    @Test
    public void callbackMethodCalled()
    {
        scriptExecutorService.runCallbackScript( raceScript, "executeScriptCB" );
        RuntimeEventChannel<String> channel = CoreServiceHelper.getRuntimeEventAcceptor()
                .createOrGetRuntimeEventChannel( "DummyChannel", Unit.TEXT, "" );
        CoreServiceHelper.getRuntimeEventAcceptor().acceptEventMicros( 1000000, channel, null, "1" );

        waitMax1SecForScriptExecution();

        scriptExecutorService.stopScript( raceScript );

        Assert.assertEquals( "Callback", consoleMessage );
    }

    // We are using this to get notified,
    // when script was executed (println was called).
    private void waitMax1SecForScriptExecution()
    {
        try
        {
            latch.await( 1, TimeUnit.SECONDS );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void println()
    {
        Assert.fail( "Println method without parameter must not be called." );
    }

    @Override
    public void println(String message)
    {
        consoleMessage = message;
        latch.countDown();
    }

    @Override
    public void print(String message)
    {
        Assert.fail( "Print method must not be called." );
    }

    @Override
    public void clear()
    {
    }
}
