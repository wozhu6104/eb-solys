/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.methodfinder.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.helper.ScriptMethodFinder;

public class GenericScriptMethodFinderTest
{
    private RaceScriptMethod firstScriptMethod;
    private RaceScriptMethod sndScriptMethod;

    @Before
    public void setup()
    {
        firstScriptMethod = createMethodWithName( "execute1" );
        sndScriptMethod = createMethodWithName( "execute2" );
    }

    private RaceScriptMethod createMethodWithName(String methodName)
    {
        RaceScriptMethod scriptMethod = mock( RaceScriptMethod.class );
        when( scriptMethod.getMethodName() ).thenReturn( methodName );
        return scriptMethod;
    }

    @Test
    public void getFirstScriptMethodIfMethodNameEmpty() throws Exception
    {
        RaceScriptMethod scriptMethod = new ScriptMethodFinder()
                .extractScriptMethod( Arrays.asList( firstScriptMethod, sndScriptMethod ), "" );
        assertEquals( firstScriptMethod, scriptMethod );
    }

    @Test
    public void getCorrectScriptMethodByMethodName() throws Exception
    {
        RaceScriptMethod scriptMethod = new ScriptMethodFinder()
                .extractScriptMethod( Arrays.asList( firstScriptMethod, sndScriptMethod ), "execute2" );
        assertEquals( sndScriptMethod, scriptMethod );
    }
}
