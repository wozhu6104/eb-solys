/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.script.changed;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyUseCase;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptInfoChangedListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;

public class ScriptChangedNotifyUseCaseImpl implements ScriptChangedNotifyUseCase, RaceScriptInfoChangedListener
{

    private ScriptChangedNotifyCallback callback;
    private final RaceScriptLoader raceScriptLoader;

    public ScriptChangedNotifyUseCaseImpl(RaceScriptLoader raceScriptLoader, ScriptChangedNotifyCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "raceScriptLoader", raceScriptLoader );
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );

        this.raceScriptLoader = raceScriptLoader;
        this.callback = callback;

        raceScriptLoader.registerRaceScriptChangedListener( this );
    }

    @Override
    public void unregister()
    {
        raceScriptLoader.unregisterRaceScriptChangedListener( this );
        callback = null;
    }

    @Override
    public void scriptInfoChanged(RaceScript script)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "Script", script );

        callback.scriptInfoChanged( script );
    }

    @Override
    public void filterMethodsChanged(RaceScript script, List<RaceScriptMethod> filterMethods)
    {
    }

}
