/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.script;

import java.util.List;

public interface RaceScriptInfo
{
    public String getName();

    public boolean isRunning();

    public boolean isRunningAsCallbackScript();

    public List<RaceScriptMethod> getGlobalMethods();

    public List<RaceScriptMethod> getCallbackMethods();

    public List<RaceScriptMethod> getTimeMarkerMethods();

    public List<RaceScriptMethod> getChannelMethods();

    public List<RaceScriptMethod> getRuntimeEventListMethods();

    public List<RaceScriptMethod> getChannelListMethods();

    public List<RaceScriptMethod> getRuntimeEventMethods();

    public List<RaceScriptMethod> getTimeMarkerListMethods();

    public List<RaceScriptMethod> getFilterMethods();

    public List<String> getInjectedParameterNames();

    public int numberExecutableMethods();

    public boolean isLoadSuccessful();

    public boolean isPreinstalled();

    public String getSourcePath();

    public boolean hasInjectedParameters();

}
