/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.script.execution;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.FilterScriptCallerListener;
import com.elektrobit.ebrace.core.interactor.api.script.InjectedParamsCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class RunScriptInteractionUseCaseImpl implements RunScriptInteractionUseCase
{

    private final ScriptExecutorService scriptExecutorService;

    public RunScriptInteractionUseCaseImpl(ScriptExecutorService scriptExecutorService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptExecutorService", scriptExecutorService );
        this.scriptExecutorService = scriptExecutorService;
    }

    @Override
    public void runScriptWithGlobalMethod(RaceScriptInfo script, String methodName)
    {
        scriptExecutorService.runScriptWithGlobalMethod( script, methodName );
    }

    @Override
    public void runScriptWithGlobalMethodAndParams(RaceScriptInfo script, String methodName, Object... parameters)
    {
        scriptExecutorService.runScriptWithGlobalMethodAndParams( script, methodName, parameters );
    }

    @Override
    public void runScriptWithRuntimeEventPreselection(RaceScriptInfo script, String methodName, RuntimeEvent<?> event)
    {
        scriptExecutorService.runScriptWithRuntimeEventPreselection( script, methodName, event );
    }

    @Override
    public void runScriptWithTimeMarkerPreselection(RaceScriptInfo script, String timeMarkerMethodName,
            TimeMarker timeMarker)
    {
        scriptExecutorService.runScriptWithTimeMarkerPreselection( script, timeMarkerMethodName, timeMarker );
    }

    @Override
    public void runScriptWithChannelPreselection(RaceScriptInfo script, String methodName,
            RuntimeEventChannel<?> channel)
    {
        scriptExecutorService.runScriptWithChannelPreselection( script, methodName, channel );
    }

    @Override
    public void runScriptWithChannelListPreselection(RaceScriptInfo script, String methodName,
            List<RuntimeEventChannel<?>> channels)
    {
        scriptExecutorService.runScriptWithChannelListPreselection( script, methodName, channels );
    }

    @Override
    public void runScriptWithTimeMarkerListPreselection(RaceScriptInfo script, String methodName,
            List<TimeMarker> timeMarkers)
    {
        scriptExecutorService.runScriptWithTimeMarkerListPreselection( script, methodName, timeMarkers );
    }

    @Override
    public void runScriptWithRuntimeEventsPreselection(RaceScriptInfo script, String methodName,
            List<RuntimeEvent<?>> events)
    {
        scriptExecutorService.runScriptWithRuntimeEventsPreselection( script, methodName, events );
    }

    @Override
    public void runCallbackScript(RaceScriptInfo script, String methodName)
    {
        scriptExecutorService.runCallbackScript( script, methodName );
    }

    @Override
    public void runFilterScript(RaceScriptInfo script, RaceScriptMethod method, List<RuntimeEvent<?>> eventsToFilter,
            FilterScriptCallerListener resultListener, SEARCH_MODE searchMode, List<RowFormatter> allRowFormatters)
    {
        scriptExecutorService
                .runFilterScript( script, method, eventsToFilter, resultListener, searchMode, allRowFormatters );
    }

    @Override
    public void stopScript(RaceScriptInfo script)
    {
        scriptExecutorService.stopScript( script );
    }

    @Override
    public void setInjectedParamsCallback(InjectedParamsCallback callback)
    {
        scriptExecutorService.setInjectedParamsCallback( callback );
    }

}
