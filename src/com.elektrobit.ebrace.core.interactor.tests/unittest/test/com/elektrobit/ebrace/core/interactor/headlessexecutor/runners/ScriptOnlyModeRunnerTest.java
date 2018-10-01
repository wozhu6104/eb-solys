/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.ScriptOnlyModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global.GlobalScriptRunner;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;

public class ScriptOnlyModeRunnerTest
{
    private GlobalScriptRunner globalScriptRunner;
    private RaceScriptInfo script;
    private String methodName;
    private ScriptExecutorService scriptExecutionService;

    @Before
    public void setup()
    {
        scriptExecutionService = mock( ScriptExecutorService.class );
        globalScriptRunner = mock( GlobalScriptRunner.class );
        script = mock( RaceScriptInfo.class );
        methodName = "execute";
    }

    @Test
    public void paramsOkIfParamIsMinusSymbol() throws Exception
    {
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertTrue( "Expecting params ok, if global script runner params ok and data source is '-'.",
                    new ScriptOnlyModeRunner( scriptExecutionService, globalScriptRunner )
                            .paramsOk( "-", script, methodName ) );
    }

    @Test
    public void paramsNotOkIfParamIsNotMinusSymbol() throws Exception
    {
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertFalse( "Expecting params not ok, if global script runner params ok and data source is not '-'.",
                     new ScriptOnlyModeRunner( scriptExecutionService, globalScriptRunner )
                             .paramsOk( "###", script, methodName ) );
    }

    @Test
    public void paramsNotOkIfParamGlobalScriptRunnerParamsNotOk() throws Exception
    {
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( false );

        assertFalse( "Expecting params not ok, if global script runner params not ok.",
                     new ScriptOnlyModeRunner( scriptExecutionService, globalScriptRunner )
                             .paramsOk( "-", script, methodName ) );
    }

    @Test
    public void runDelegatedToGlobalScriptRunner() throws Exception
    {
        new ScriptOnlyModeRunner( scriptExecutionService, globalScriptRunner ).run( "", script, methodName );

        verify( globalScriptRunner ).run( script, methodName );
    }
}
