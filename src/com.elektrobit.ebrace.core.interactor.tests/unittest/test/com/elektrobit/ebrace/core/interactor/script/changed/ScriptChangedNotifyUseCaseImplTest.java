/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.script.changed;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyCallback;
import com.elektrobit.ebrace.core.interactor.script.changed.ScriptChangedNotifyUseCaseImpl;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;

public class ScriptChangedNotifyUseCaseImplTest
{

    private RaceScriptLoader scriptLoader;
    private ScriptChangedNotifyCallback callback;

    @Before
    public void setup()
    {
        scriptLoader = mock( RaceScriptLoader.class );
        callback = mock( ScriptChangedNotifyCallback.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCallbackNotAllowed()
    {
        new ScriptChangedNotifyUseCaseImpl( scriptLoader, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRaceScriptLoaderNotAllowed()
    {
        new ScriptChangedNotifyUseCaseImpl( null, callback );
    }

    @Test
    public void listenerRegistered() throws Exception
    {
        ScriptChangedNotifyUseCaseImpl usecase = new ScriptChangedNotifyUseCaseImpl( scriptLoader, callback );

        verify( scriptLoader ).registerRaceScriptChangedListener( usecase );
    }

    @Test
    public void listenerUnregistered() throws Exception
    {
        ScriptChangedNotifyUseCaseImpl usecase = new ScriptChangedNotifyUseCaseImpl( scriptLoader, callback );

        usecase.unregister();

        verify( scriptLoader ).unregisterRaceScriptChangedListener( usecase );
    }

    @Test
    public void callbeckCalledOnScriptChanged() throws Exception
    {
        ScriptChangedNotifyUseCaseImpl usecase = new ScriptChangedNotifyUseCaseImpl( scriptLoader, callback );
        RaceScript script = mock( RaceScript.class );
        usecase.scriptInfoChanged( script );

        verify( callback ).scriptInfoChanged( script );
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRaceScriptNotAllowed()
    {
        new ScriptChangedNotifyUseCaseImpl( scriptLoader, callback ).scriptInfoChanged( null );
    }
}
