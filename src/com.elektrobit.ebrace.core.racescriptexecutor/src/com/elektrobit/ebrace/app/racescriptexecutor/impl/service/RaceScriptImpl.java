/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import com.elektrobit.ebrace.app.racescriptexecutor.caller.impl.CallbackScriptCaller;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebsolys.script.external.Console;
import com.elektrobit.ebsolys.script.external.ScriptContext;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(of = {"pathToBinJarFile"})
@ToString(of = {"pathToBinJarFile", "scriptName"})
public class RaceScriptImpl implements RaceScript
{
    @Getter
    private final String pathToBinJarFile;
    private final String scriptName;
    @Getter
    private final ScriptContext raceScriptContext;
    @Getter
    private final Console console;
    @Setter
    private volatile boolean scriptRunning = false;
    @Setter
    private volatile boolean runningAsCallbackScript = false;
    @Setter
    @Getter
    private Thread scriptExecutionThread;

    @Setter
    private List<Method> allMethods;
    @Setter
    private List<RaceScriptMethod> globalContextMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> callbackMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> timeMarkerContextMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> channelContextMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> runtimeEventListContextMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> remainingExecuteMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> remainingFilterMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> filterMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> channelListContextMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> timeMarkerListContextMethods = Collections.emptyList();
    @Setter
    private List<RaceScriptMethod> runtimeEventContextMethods = Collections.emptyList();
    @Setter
    private List<String> injectedParams = Collections.emptyList();

    @Getter
    @Setter
    private CallbackScriptCaller callbackScriptCaller = null;

    private final boolean preinstalled;
    private final String sourcePath;

    public RaceScriptImpl(final ScriptContext raceScriptContext, final String pathToBinJarFile, final String scriptName,
            final Console console, boolean preinstalled, String sourcePath)
    {
        this.raceScriptContext = raceScriptContext;
        this.scriptName = scriptName;
        this.pathToBinJarFile = pathToBinJarFile;
        this.console = console;
        this.preinstalled = preinstalled;
        this.sourcePath = sourcePath;
        clearMethodLists();
    }

    public void clearMethodLists()
    {
        allMethods = Collections.emptyList();
        globalContextMethods = Collections.emptyList();
        callbackMethods = Collections.emptyList();
        timeMarkerContextMethods = Collections.emptyList();
        channelContextMethods = Collections.emptyList();
        runtimeEventListContextMethods = Collections.emptyList();
        channelListContextMethods = Collections.emptyList();
        remainingExecuteMethods = Collections.emptyList();
        remainingFilterMethods = Collections.emptyList();
        filterMethods = Collections.emptyList();
        timeMarkerListContextMethods = Collections.emptyList();
        runtimeEventContextMethods = Collections.emptyList();
        injectedParams = Collections.emptyList();
    }

    public boolean isScriptRunning()
    {
        return scriptRunning;
    }

    @Override
    public boolean runtimeEventsWaitingToBeProcessedByCallback()
    {
        if (callbackScriptCaller == null)
        {
            return false;
        }
        else
        {
            return callbackScriptCaller.runtimeEventsWaitingToBePosted();
        }
    }

    @Override
    public List<RaceScriptMethod> getFilterMethods()
    {
        return filterMethods;
    }

    @Override
    public String getName()
    {
        return scriptName;
    }

    @Override
    public boolean isRunning()
    {
        return scriptRunning;
    }

    @Override
    public boolean isRunningAsCallbackScript()
    {
        return runningAsCallbackScript;
    }

    @Override
    public List<RaceScriptMethod> getGlobalMethods()
    {
        return globalContextMethods;
    }

    @Override
    public List<RaceScriptMethod> getCallbackMethods()
    {
        return callbackMethods;
    }

    @Override
    public List<RaceScriptMethod> getTimeMarkerMethods()
    {
        return timeMarkerContextMethods;
    }

    @Override
    public List<RaceScriptMethod> getChannelMethods()
    {
        return channelContextMethods;
    }

    @Override
    public List<RaceScriptMethod> getRuntimeEventListMethods()
    {
        return runtimeEventListContextMethods;
    }

    @Override
    public List<RaceScriptMethod> getChannelListMethods()
    {
        return channelListContextMethods;
    }

    @Override
    public boolean isLoadSuccessful()
    {
        return allMethods.size() != 0;
    }

    @Override
    public int numberExecutableMethods()
    {
        int executableMethodsCount = timeMarkerContextMethods.size() + channelContextMethods.size()
                + runtimeEventListContextMethods.size() + globalContextMethods.size() + channelListContextMethods.size()
                + callbackMethods.size();
        return executableMethodsCount;
    }

    @Override
    public List<RaceScriptMethod> getRuntimeEventMethods()
    {
        return runtimeEventContextMethods;
    }

    @Override
    public List<RaceScriptMethod> getTimeMarkerListMethods()
    {
        return timeMarkerListContextMethods;

    }

    @Override
    public boolean isPreinstalled()
    {
        return preinstalled;
    }

    @Override
    public String getSourcePath()
    {
        return sourcePath;
    }

    @Override
    public List<String> getInjectedParameterNames()
    {
        return injectedParams;
    }

    @Override
    public boolean hasInjectedParameters()
    {
        return !injectedParams.isEmpty();
    }

}
