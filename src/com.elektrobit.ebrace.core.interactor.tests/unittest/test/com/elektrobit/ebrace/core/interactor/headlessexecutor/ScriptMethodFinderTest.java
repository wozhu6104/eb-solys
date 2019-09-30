/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global.GlobalScriptRunPart;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.helper.ScriptMethodFinder;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;

public class ScriptMethodFinderTest
{
    private RaceScriptMethod scriptMethodExecute;
    private RaceScriptInfo scriptInfo;
    private GlobalScriptRunPart scriptMethodFinder;
    private RaceScriptMethod scriptMethodDummy;

    @Before
    public void setup()
    {
        scriptMethodFinder = new GlobalScriptRunPart( mock( ScriptExecutorService.class ), new ScriptMethodFinder() );
        scriptInfo = mock( RaceScriptInfo.class );
        scriptMethodExecute = new RaceScriptMethod( "execute", "", "HelloWorld", null, "" );
        scriptMethodDummy = new RaceScriptMethod( "dummy", "", "HelloWorld", null, "" );
        when( scriptInfo.getGlobalMethods() ).thenReturn( Arrays.asList( scriptMethodDummy, scriptMethodExecute ) );
    }

    @Test
    public void findExistingGlobalMethod() throws Exception
    {
        assertEquals( scriptMethodExecute, scriptMethodFinder.extractScriptMethod( scriptInfo, "execute" ) );
    }

    @Test
    public void nullIfGlobalMethodNotFound() throws Exception
    {
        assertNull( "Expecting null if method not there.",
                    scriptMethodFinder.extractScriptMethod( scriptInfo, "methodNotThere" ) );
    }

    @Test
    public void firstGlobalMethodIfMethodNameEmpty() throws Exception
    {
        assertEquals( scriptMethodDummy, scriptMethodFinder.extractScriptMethod( scriptInfo, "" ) );
    }
}
