/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.headlessexecutor;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessExecutorInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.api.AutomationModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.ConnectionModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.FileCallbackModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.FileGlobalModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.ScriptOnlyModeRunner;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.HeadlessParamsValidationRunnerHelper;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.NumberOfParamsValidator;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptsReloadedListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

public class HeadlessExecutorInteractionUseCaseImpl
        implements
            HeadlessExecutorInteractionUseCase,
            ScriptsReloadedListener,
            LoadFileProgressListener
{

    private final RaceScriptLoader raceScriptLoader;

    private final ScriptProjectBuilderService scriptProjectBuilderService;

    private final boolean waitForScriptCompilation;

    private final NumberOfParamsValidator numberOfParamsValidator;

    private List<RaceScriptInfo> scripts = Collections.emptyList();

    private final LoadFileService loadFileService;

    private final ScriptExecutorService scriptExecutorService;
    private final CountDownLatch waitForCompilationLatch;
    private final PreferencesService preferencesService;
    private final ConnectionService connectionService;
    private final ResourcesModelManager resourcesModelManager;

    public HeadlessExecutorInteractionUseCaseImpl(RaceScriptLoader raceScriptLoader,
            PreferencesService preferencesService, ScriptProjectBuilderService scriptProjectBuilderService,
            LoadFileService loadFileService, ScriptExecutorService scriptExecutorService,
            ConnectionService connectionService, ResourcesModelManager resourcesModelManager)
    {
        this( raceScriptLoader,
                preferencesService,
                scriptProjectBuilderService,
                loadFileService,
                scriptExecutorService,
                connectionService,
                resourcesModelManager,
                true,
                new NumberOfParamsValidator(),
                new CountDownLatch( 1 ),
                new CountDownLatch( 1 ) );
    }

    public HeadlessExecutorInteractionUseCaseImpl(RaceScriptLoader raceScriptLoader,
            PreferencesService preferencesService, ScriptProjectBuilderService scriptProjectBuilderService,
            LoadFileService loadFileService, ScriptExecutorService scriptExecutorService,
            ConnectionService connectionService, ResourcesModelManager resourcesModelManager,
            boolean waitForScriptCompilation, NumberOfParamsValidator numberOfParamsValidator,
            CountDownLatch waitForCompilationLatch, CountDownLatch waitForExecutionFinished)
    {
        this.raceScriptLoader = raceScriptLoader;
        this.preferencesService = preferencesService;
        this.scriptProjectBuilderService = scriptProjectBuilderService;
        this.connectionService = connectionService;
        this.resourcesModelManager = resourcesModelManager;
        this.waitForScriptCompilation = waitForScriptCompilation;
        this.numberOfParamsValidator = numberOfParamsValidator;
        this.loadFileService = loadFileService;
        this.scriptExecutorService = scriptExecutorService;
        this.waitForCompilationLatch = waitForCompilationLatch;

        raceScriptLoader.addScriptsReloadedListener( this );
    }

    @Override
    public boolean run(List<String> params)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "params", params );

        boolean result = true;

        numberOfParamsValidator.setParams( params );
        if (numberOfParamsValidator.validationFailed())
        {
            printValidationError( numberOfParamsValidator.errorMessage() );
            result = false;
        }
        else
        {
            setScriptsProjectLocationIfNeeded( params );

            if (buildScriptAndWaitCompilation())
            {
                final String scriptName = extractScriptName( params.get( 0 ) );
                final RaceScriptInfo script = findCompiledScript( scriptName );

                if (script == null)
                {
                    printValidationError( "Script compilation failed. Script " + scriptName + " couldn't be found." );
                    result = false;
                }
                else
                {
                    final String methodName = extractScriptMethod( params.get( 0 ) );
                    final String dataSource = params.get( 1 );

                    AutomationModeRunner scriptOnlyModeRunner = new ScriptOnlyModeRunner( scriptExecutorService );

                    ConnectionModeRunner connectionModeRunner = new ConnectionModeRunner( resourcesModelManager,
                                                                                          connectionService,
                                                                                          scriptExecutorService );

                    AutomationModeRunner fileGlobalModeRunner = new FileGlobalModeRunner( loadFileService,
                                                                                          scriptExecutorService );

                    AutomationModeRunner fileCallbackModeRunner = new FileCallbackModeRunner( loadFileService,
                                                                                              scriptExecutorService );

                    if (!runFirstMatchingRunner( script,
                                                 methodName,
                                                 dataSource,
                                                 Arrays.asList( scriptOnlyModeRunner,
                                                                connectionModeRunner,
                                                                fileGlobalModeRunner,
                                                                fileCallbackModeRunner ) ))
                    {
                        printValidationError( "EB solys execution failed or no run mode found, because parameter are not valid." );
                        result = false;
                    }

                }

            }
            else
            {
                printValidationError( "Script compilation failed or did not finished with 60 secs. Will not run the script." );
                result = false;
            }

        }

        if (!result)
        {
            printHelp();
        }

        return result;
    }

    private boolean runFirstMatchingRunner(final RaceScriptInfo script, final String methodName,
            final String dataSource, List<AutomationModeRunner> availableModeRunners)
    {
        for (AutomationModeRunner runner : availableModeRunners)
        {
            if (runner.paramsOk( dataSource, script, methodName ))
            {
                boolean runOk = runner.run( dataSource, script, methodName );
                return runOk;
            }
        }
        return false;
    }

    private String extractScriptMethod(String param)
    {
        return FilenameUtils.getExtension( param );
    }

    private void setScriptsProjectLocationIfNeeded(List<String> params)
    {
        if (!HeadlessParamsValidationRunnerHelper.isOnlyScriptName( params.get( 0 ) ))
        {
            String scriptsProjectPath = ProjectLocationExtractHelper
                    .getProjectLocationFromScriptPath( params.get( 0 ) + ScriptConstants.SCRIPT_EXTENSION );
            preferencesService.setScriptFolderPath( scriptsProjectPath );
        }
    }

    private String extractScriptName(String param)
    {
        return FilenameUtils.removeExtension( new File( param ).getName() );
    }

    private RaceScriptInfo findCompiledScript(String scriptName)
    {
        for (RaceScriptInfo nextScript : scripts)
        {
            if (nextScript.getName().equals( scriptName ))
            {
                return nextScript;
            }
        }
        return null;
    }

    private boolean buildScriptAndWaitCompilation()
    {
        System.out.println( "--- Script compilation started ---" );
        boolean result = true;
        scriptProjectBuilderService.buildProject();
        if (waitForScriptCompilation)
        {
            try
            {
                final int timeInSecsToWaitForScriptCompilation = 60;
                result = waitForCompilationLatch.await( timeInSecsToWaitForScriptCompilation, TimeUnit.SECONDS );
            }
            catch (InterruptedException e)
            {
                result = false;
            }
        }
        raceScriptLoader.removeScriptsReloadedListener( this );
        System.out.println( "--- Script compilation stopped ---" );
        return result;
    }

    private void printValidationError(String errorMessage)
    {
        System.out.println();
        System.out.println( "ERROR: " + errorMessage );
    }

    @Override
    public void onScriptsReloaded(List<RaceScriptInfo> scripts)
    {
        this.scripts = scripts;
        waitForCompilationLatch.countDown();
    }

    @Override
    public void onLoadFileStarted(String pathToFile)
    {
        System.out.println( "--- File loading started: " + pathToFile + " ---" );
        System.out.print( "[ " );
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
        if (percentDone % 20 == 0)
        {
            System.out.print( "" + percentDone + "% " );
        }
    }

    @Override
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        System.out.println( "]" );
        System.out.println( "--- File loading stopped ---" );
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

    public void printHelp()
    {
        System.out.println();
        System.out.println();
        System.out.println( "----------------------------------" );
        System.out.println( "| EB solys automation mode help. |" );
        System.out.println( "----------------------------------" );
        System.out.println();
        System.out
                .println( "Usage: eb-solys-automation[.exe] script-path[.script-method] [data-source] [script-parameters]" );
        System.out.println();
        System.out.println( "script-path (mandatory):" );
        System.out
                .println( "    - Absolute path to EB solys script file without suffix '.xtend'. E.g. '/home/user/scripts/src/MyScript'." );
        System.out.println( "    - Only script name. Script is used from workspace. E.g. 'MyScript'." );
        System.out.println();
        System.out.println( "script-method (optional):" );
        System.out
                .println( "    - Add '.script-name' to script-path to execute a certain method. E.g. '/home/user/scripts/src/MyScript.execute2'." );
        System.out.println();
        System.out.println( "data-source (mandatory):" );
        System.out.println( "    - '-' if no data-source is needed." );
        System.out.println( "    - Absolute or relative path to file. E.g. '/home/user/files/input.bin'." );
        System.out.println( "    - Hostname and IP to running EB solys target-agent. E.g. '192.168.2.2:1234'." );
        System.out.println();
        System.out.println( "script-parameters (optional):" );
        System.out
                .println( "    - list of key value pair, that is used by the script. E.g. 'buildID=1234 date=15/08/2017'." );
    }

}
