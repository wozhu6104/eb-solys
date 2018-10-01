/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.caller.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.app.racescriptexecutor.helper.AnnotationHelper;
import com.elektrobit.ebrace.app.racescriptexecutor.helper.ExecuteMethodHelper;
import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.FilterScriptCallerListener;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class FilterScriptCaller extends DefaultRaceScriptCaller
{
    private final List<RuntimeEvent<?>> eventsToFilter;
    private final FilterScriptCallerListener resultListener;
    private final SEARCH_MODE searchMode;
    private final List<RuntimeEvent<?>> resultEvents = new ArrayList<RuntimeEvent<?>>();
    private final Map<Object, Map<RowFormatter, List<Position>>> resultHighlightMap = new LinkedHashMap<Object, Map<RowFormatter, List<Position>>>();
    private HashMap<RowFormatter, List<Position>> highlightWholeLineEntry;

    public FilterScriptCaller(List<RuntimeEvent<?>> eventsToFilter, FilterScriptCallerListener resultListener,
            SEARCH_MODE searchMode, List<RowFormatter> allRowFormatters)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "eventsToFilter", eventsToFilter );
        RangeCheckUtils.assertReferenceParameterNotNull( "resultListener", resultListener );

        this.eventsToFilter = eventsToFilter;
        this.resultListener = resultListener;
        this.searchMode = searchMode;

        createHighlighWholeLineEntry( allRowFormatters );
    }

    private void createHighlighWholeLineEntry(List<RowFormatter> allRowFormatters)
    {
        highlightWholeLineEntry = new LinkedHashMap<RowFormatter, List<Position>>();
        List<Position> listWithFullPosition = new ArrayList<Position>();
        listWithFullPosition.add( Position.FULL_SELECTION );

        for (RowFormatter rowFormatter : allRowFormatters)
        {
            highlightWholeLineEntry.put( rowFormatter, listWithFullPosition );
        }
    }

    @Override
    public void callScript(String scriptName, Object raceScriptInstance, String executeMethod, Object... params)
            throws RuntimeException
    {
        List<RaceScriptMethod> allMethods = AnnotationHelper.getAllMethods( raceScriptInstance, scriptName );
        RaceScriptMethod methodToCall = findMethodToCall( executeMethod, allMethods );

        if (methodToCall != null)
        {
            for (RuntimeEvent<?> runtimeEvent : eventsToFilter)
            {
                Object result = callFilterMethod( scriptName, raceScriptInstance, methodToCall, runtimeEvent );
                if (result != null && result instanceof Boolean && (Boolean)result == true)
                {
                    handlePositiveResult( runtimeEvent );
                }
            }
        }

        if (searchMode == SEARCH_MODE.SEARCH)
        {
            resultListener.onScriptFilteringDone( eventsToFilter, resultHighlightMap );
        }
        else if (searchMode == SEARCH_MODE.FILTER)
        {
            resultListener.onScriptFilteringDone( resultEvents, resultHighlightMap );
        }
    }

    private RaceScriptMethod findMethodToCall(String executeMethod, List<RaceScriptMethod> allMethods)
    {
        if (!eventsToFilter.isEmpty())
        {
            List<Object> params = new ArrayList<Object>();
            params.add( eventsToFilter.get( 0 ) );

            RaceScriptMethod methodToCall = ExecuteMethodHelper.getMethodByNameAndParamTypes( executeMethod,
                                                                                              allMethods,
                                                                                              params );
            return methodToCall;
        }
        return null;
    }

    private Object callFilterMethod(String scriptName, Object raceScriptInstance, RaceScriptMethod methodToCall,
            RuntimeEvent<?> runtimeEvent) throws RuntimeException
    {
        try
        {
            Object result = ExecuteMethodHelper.executeMethod( scriptName,
                                                               methodToCall,
                                                               raceScriptInstance,
                                                               runtimeEvent );
            return result;
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            postClearResult();
            throw e;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            postClearResult();
            throw new RuntimeException( "Error in Script" );
        }
    }

    private void postClearResult()
    {
        resultEvents.clear();
        resultHighlightMap.clear();
        resultListener.onScriptFilteringDone( Collections.<RuntimeEvent<?>> emptyList(),
                                              Collections.<Object, Map<RowFormatter, List<Position>>> emptyMap() );
    }

    private void handlePositiveResult(RuntimeEvent<?> runtimeEvent)
    {
        resultEvents.add( runtimeEvent );
        resultHighlightMap.put( runtimeEvent, highlightWholeLineEntry );
    }
}
