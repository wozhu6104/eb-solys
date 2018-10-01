/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.script.execution;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.FilterScriptCallerListener;
import com.elektrobit.ebrace.core.interactor.api.script.InjectedParamsCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public interface ScriptRunCalls
{
    public void runScriptWithGlobalMethod(RaceScriptInfo script, String methodName);

    public void runScriptWithGlobalMethodAndParams(RaceScriptInfo scriptInfo, String methodName, Object... parameters);

    public void runScriptWithRuntimeEventPreselection(RaceScriptInfo script, String methodName, RuntimeEvent<?> event);

    public void runScriptWithTimeMarkerPreselection(RaceScriptInfo script, String timeMarkerMethodName,
            TimeMarker timeMarker);

    public void runScriptWithChannelPreselection(RaceScriptInfo script, String methodName,
            RuntimeEventChannel<?> channel);

    public void runScriptWithChannelListPreselection(RaceScriptInfo script, String methodName,
            List<RuntimeEventChannel<?>> channels);

    public void runScriptWithTimeMarkerListPreselection(RaceScriptInfo script, String methodName,
            List<TimeMarker> timeMarkers);

    public void runScriptWithRuntimeEventsPreselection(RaceScriptInfo script, String methodName,
            List<RuntimeEvent<?>> events);

    public void runCallbackScript(RaceScriptInfo script, String methodName);

    public void runFilterScript(RaceScriptInfo script, RaceScriptMethod method, List<RuntimeEvent<?>> eventsToFilter,
            FilterScriptCallerListener resultListener, SEARCH_MODE searchMode, List<RowFormatter> allRowFormatters);

    public void stopScript(RaceScriptInfo script);

    public void setInjectedParamsCallback(InjectedParamsCallback callback);
}
