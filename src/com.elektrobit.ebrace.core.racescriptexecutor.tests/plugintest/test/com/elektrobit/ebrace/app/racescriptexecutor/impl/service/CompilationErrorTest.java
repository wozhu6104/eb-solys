/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;

public class CompilationErrorTest
{
    private final static String PATH_TO_RACE_SCRIPTS = "resources/jars/";

    @Ignore
    @Test
    public void testNoExceptionThrown() throws Exception
    {
        String pathToBundleRootFolder = FileHelper.getBundleRootFolderOfClass( this.getClass() );
        String pathToScript = (pathToBundleRootFolder + PATH_TO_RACE_SCRIPTS + "ErrorScript.jar");

        RaceScriptLoader raceScriptLoader = CoreServiceHelper.getRaceScriptLoader();
        ScriptExecutorService scriptExecutorService = CoreServiceHelper.getScriptExecutorService();
        RaceScript raceScript = raceScriptLoader.loadRaceScript( new ScriptData( "ErrorScript", null, pathToScript ) );
        List<RaceScriptMethod> globalMethods = raceScript.getGlobalMethods();
        scriptExecutorService.runScriptWithGlobalMethod( raceScript, globalMethods.get( 0 ).getMethodName() );

        Thread.sleep( 1000 );// wait for script execution
    }
}
