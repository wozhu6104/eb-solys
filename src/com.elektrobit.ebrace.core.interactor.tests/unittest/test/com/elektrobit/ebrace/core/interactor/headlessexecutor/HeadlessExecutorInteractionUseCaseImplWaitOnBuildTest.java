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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.HeadlessExecutorInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.NumberOfParamsValidator;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.targetadapter.communicator.api.ConnectionService;

public class HeadlessExecutorInteractionUseCaseImplWaitOnBuildTest
{
    private HeadlessExecutorInteractionUseCaseImpl useCase;

    private ScriptProjectBuilderService scriptProjectBuilder;
    private NumberOfParamsValidator numberOfParamsValidator;
    private RaceScriptLoader raceScriptLoader;
    private LoadFileService loadFileService;
    private ScriptExecutorService scriptExecutorService;
    private CountDownLatch latch;
    private ConnectionService connectionService;
    private ResourcesModelManager resourcesModelManager;

    @Before
    public void setup() throws Exception
    {
        scriptProjectBuilder = mock( ScriptProjectBuilderService.class );
        numberOfParamsValidator = mock( NumberOfParamsValidator.class );
        raceScriptLoader = mock( RaceScriptLoader.class );
        loadFileService = mock( LoadFileService.class );
        scriptExecutorService = mock( ScriptExecutorService.class );
        connectionService = mock( ConnectionService.class );
        resourcesModelManager = mock( ResourcesModelManager.class );
        latch = mock( CountDownLatch.class );
        useCase = new HeadlessExecutorInteractionUseCaseImpl( raceScriptLoader,
                                                              mock( PreferencesService.class ),
                                                              scriptProjectBuilder,
                                                              loadFileService,
                                                              scriptExecutorService,
                                                              connectionService,
                                                              resourcesModelManager,
                                                              true,
                                                              numberOfParamsValidator,
                                                              latch,
                                                              mock( CountDownLatch.class ) );
    }

    @Test
    public void waitingForLatch() throws Exception
    {
        List<String> params = Arrays.asList( "Script", "file" );

        when( numberOfParamsValidator.validationFailed() ).thenReturn( false );

        useCase.run( params );

        verify( latch ).await( 60, TimeUnit.SECONDS );
    }

    @Test
    public void latchCallonScriptsReloaded() throws Exception
    {
        useCase.onScriptsReloaded( Collections.emptyList() );

        verify( latch ).countDown();
    }

}
