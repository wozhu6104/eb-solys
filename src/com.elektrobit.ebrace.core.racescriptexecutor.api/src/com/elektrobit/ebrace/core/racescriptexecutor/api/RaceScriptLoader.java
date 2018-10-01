/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.racescriptexecutor.api;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebsolys.script.external.Console;

public interface RaceScriptLoader
{
    public RaceScript loadRaceScript(ScriptData nameOrPath);

    public RaceScript loadRaceScript(ScriptData nameOrPath, Console customConsole);

    public void registerRaceScriptChangedListener(RaceScriptInfoChangedListener listener);

    public void unregisterRaceScriptChangedListener(RaceScriptInfoChangedListener listener);

    public void addScriptsReloadedListener(ScriptsReloadedListener listener);

    public void removeScriptsReloadedListener(ScriptsReloadedListener listener);
}
