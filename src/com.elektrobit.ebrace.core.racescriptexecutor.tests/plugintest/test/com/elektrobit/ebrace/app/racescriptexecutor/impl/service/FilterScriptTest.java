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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.RaceScriptLoaderImpl;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.FilterScriptCallerListener;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMock;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class FilterScriptTest implements FilterScriptCallerListener
{
    private final static String PATH_TO_RACE_SCRIPTS = "resources/jars/";
    private RaceScriptInfo raceScript;
    private List<RuntimeEvent<?>> events;
    private RowFormatter mockedRowFormatter;
    private CountDownLatch latch = null;

    private List<RuntimeEvent<?>> resultEvents = null;
    private Map<?, Map<RowFormatter, List<Position>>> resultPositions = null;
    private ScriptExecutorService scriptExecutorService;
    private ResourcesModelManager resourcesModelManager;

    @Before
    public void setup()
    {
        latch = new CountDownLatch( 1 );

        String pathToScript = (PATH_TO_RACE_SCRIPTS + "FilterScript.jar");

        RaceScriptLoaderImpl raceScriptLoader = (RaceScriptLoaderImpl)CoreServiceHelper.getRaceScriptLoader();
        scriptExecutorService = CoreServiceHelper.getScriptExecutorService();

        resourcesModelManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
                .getService();

        List<ScriptData> allScriptsToLoad = new ArrayList<>();
        allScriptsToLoad.add( ScriptTestHelper.mockScriptData( "FilterScript", "FilterScript.xtend", pathToScript ) );

        raceScriptLoader.onScriptsBuildAndExported( allScriptsToLoad );
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "FilterScript",
                                                                                     resourcesModelManager );
        raceScript = scriptModel.getScriptInfo();

        createEvents();
    }

    private void createEvents()
    {
        events = new ArrayList<RuntimeEvent<?>>();
        events.add( new RuntimeEventMock<Integer>( 8 ) );
        events.add( new RuntimeEventMock<Integer>( 9 ) );
        events.add( new RuntimeEventMock<Integer>( 11 ) );
        events.add( new RuntimeEventMock<Integer>( 12 ) );
    }

    @Ignore
    @Test
    public void testFilterEventsBiggerThan10CheckResultEvents() throws Exception
    {
        filter( SEARCH_MODE.FILTER );

        Assert.assertEquals( 2, resultEvents.size() );
        Assert.assertEquals( resultEvents.get( 0 ).getValue(), 11 );
        Assert.assertEquals( resultEvents.get( 1 ).getValue(), 12 );
    }

    @Test
    public void testFilterEventsBiggerThan10CheckResultHighlighting() throws Exception
    {
        filter( SEARCH_MODE.FILTER );

        Set<?> matchingEvents = resultPositions.keySet();
        List<?> matchingEventsList = new ArrayList<Object>( matchingEvents );

        Assert.assertEquals( 2, matchingEventsList.size() );
        Assert.assertEquals( ((RuntimeEvent<?>)matchingEventsList.get( 0 )).getValue(), 11 );
        Assert.assertEquals( ((RuntimeEvent<?>)matchingEventsList.get( 1 )).getValue(), 12 );
    }

    @Test
    public void testSearchEventsBiggerThan10CheckResultEvents() throws Exception
    {
        filter( SEARCH_MODE.SEARCH );

        Assert.assertEquals( 4, resultEvents.size() );
        Assert.assertEquals( resultEvents.get( 0 ).getValue(), 8 );
        Assert.assertEquals( resultEvents.get( 1 ).getValue(), 9 );
        Assert.assertEquals( resultEvents.get( 2 ).getValue(), 11 );
        Assert.assertEquals( resultEvents.get( 3 ).getValue(), 12 );
    }

    @Test
    public void testSearchEventsBiggerThan10CheckResultHighlighting() throws Exception
    {
        filter( SEARCH_MODE.SEARCH );

        Set<?> matchingEvents = resultPositions.keySet();
        List<?> matchingEventsList = new ArrayList<Object>( matchingEvents );

        Assert.assertEquals( 2, matchingEventsList.size() );
        Assert.assertEquals( ((RuntimeEvent<?>)matchingEventsList.get( 0 )).getValue(), 11 );
        Assert.assertEquals( ((RuntimeEvent<?>)matchingEventsList.get( 1 )).getValue(), 12 );
    }

    private void filter(SEARCH_MODE searchMode)
    {
        List<RaceScriptMethod> filterMethods = raceScript.getFilterMethods();
        Assert.assertEquals( 1, filterMethods.size() );

        RaceScriptMethod filterMethod = filterMethods.get( 0 );
        mockedRowFormatter = Mockito.mock( RowFormatter.class );
        scriptExecutorService.runFilterScript( raceScript,
                                               filterMethod,
                                               events,
                                               this,
                                               searchMode,
                                               Arrays.asList( mockedRowFormatter ) );

        waitMax3SecForScriptExecution();
    }

    private void waitMax3SecForScriptExecution()
    {
        try
        {
            latch.await( 3, TimeUnit.SECONDS );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onScriptFilteringDone(List<RuntimeEvent<?>> result,
            Map<?, Map<RowFormatter, List<Position>>> searchPositionList)
    {
        this.resultEvents = result;
        this.resultPositions = searchPositionList;
        latch.countDown();
    }
}
