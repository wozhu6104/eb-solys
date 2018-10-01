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
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.FileGlobalModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.FileLoadRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.global.GlobalScriptRunner;

public class FileGlobalModeRunnerTest
{
    private FileGlobalModeRunner fileGlobalModeRunner;
    private String pathToFile;
    private RaceScriptInfo script;
    private String methodName;
    private GlobalScriptRunner globalScriptRunner;
    private FileLoadRunner fileLoadRunner;

    @Before
    public void setup()
    {
        fileLoadRunner = mock( FileLoadRunner.class );
        globalScriptRunner = mock( GlobalScriptRunner.class );
        fileGlobalModeRunner = new FileGlobalModeRunner( fileLoadRunner, globalScriptRunner );
        pathToFile = "pathToFile";
        methodName = "execute";
        script = mock( RaceScriptInfo.class );
    }

    @Test
    public void paramsOkIfRunnerParamsOk() throws Exception
    {
        when( fileLoadRunner.paramsOk( pathToFile ) ).thenReturn( true );
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertTrue( fileGlobalModeRunner.paramsOk( pathToFile, script, methodName ) );
    }

    @Test
    public void paramsNOkIfRunnerParamsNOk() throws Exception
    {
        when( fileLoadRunner.paramsOk( pathToFile ) ).thenReturn( false );
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( false );

        assertFalse( fileGlobalModeRunner.paramsOk( pathToFile, script, methodName ) );
    }

    @Test
    public void paramsNOkIfFileLoadRunnerParamsNOk() throws Exception
    {
        when( fileLoadRunner.paramsOk( pathToFile ) ).thenReturn( false );
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( true );

        assertFalse( fileGlobalModeRunner.paramsOk( pathToFile, script, methodName ) );
    }

    @Test
    public void paramsNOkIfScriptOnlyModeRunnerParamsNOk() throws Exception
    {
        when( fileLoadRunner.paramsOk( pathToFile ) ).thenReturn( true );
        when( globalScriptRunner.paramsOk( script, methodName ) ).thenReturn( false );

        assertFalse( fileGlobalModeRunner.paramsOk( pathToFile, script, methodName ) );
    }

    @Test
    public void runOkIfRunnersOk() throws Exception
    {
        when( fileLoadRunner.run( pathToFile ) ).thenReturn( true );
        when( globalScriptRunner.run( script, methodName ) ).thenReturn( true );

        assertTrue( fileGlobalModeRunner.run( pathToFile, script, methodName ) );
    }

    @Test
    public void runNOkIfFileLoadRunnerNOk() throws Exception
    {
        when( fileLoadRunner.run( pathToFile ) ).thenReturn( false );
        when( globalScriptRunner.run( script, methodName ) ).thenReturn( true );

        assertFalse( fileGlobalModeRunner.run( pathToFile, script, methodName ) );
    }

    @Test
    public void runNOkIfGlobalScriptRunnerNOk() throws Exception
    {
        when( fileLoadRunner.run( pathToFile ) ).thenReturn( true );
        when( globalScriptRunner.run( script, methodName ) ).thenReturn( false );

        assertFalse( fileGlobalModeRunner.run( pathToFile, script, methodName ) );
    }

    @Test
    public void runNOkIfRunnersNOk() throws Exception
    {
        when( fileLoadRunner.run( pathToFile ) ).thenReturn( false );
        when( globalScriptRunner.run( script, methodName ) ).thenReturn( false );

        assertFalse( fileGlobalModeRunner.run( pathToFile, script, methodName ) );
    }
}
