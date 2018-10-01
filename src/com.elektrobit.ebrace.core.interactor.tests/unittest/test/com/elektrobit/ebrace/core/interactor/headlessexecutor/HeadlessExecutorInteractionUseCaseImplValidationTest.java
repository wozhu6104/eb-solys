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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

public class HeadlessExecutorInteractionUseCaseImplValidationTest
{
    private HeadlessExecutorInteractionUseCaseImpl useCase;
    private ScriptProjectBuilderService scriptProjectBuilder;
    private NumberOfParamsValidator numberOfParamsValidator;
    private LoadFileService loadFileService;
    private ScriptExecutorService scriptExecutorService;
    private List<String> params;
    private ConnectionService connectionService;
    private ResourcesModelManager resourcesModelManager;

    @Before
    public void setup() throws Exception
    {
        scriptProjectBuilder = mock( ScriptProjectBuilderService.class );
        numberOfParamsValidator = mock( NumberOfParamsValidator.class );
        loadFileService = mock( LoadFileService.class );
        scriptExecutorService = mock( ScriptExecutorService.class );
        connectionService = mock( ConnectionService.class );
        resourcesModelManager = mock( ResourcesModelManager.class );
        useCase = new HeadlessExecutorInteractionUseCaseImpl( mock( RaceScriptLoader.class ),
                                                              mock( PreferencesService.class ),
                                                              scriptProjectBuilder,
                                                              loadFileService,
                                                              scriptExecutorService,
                                                              connectionService,
                                                              resourcesModelManager,
                                                              false,
                                                              numberOfParamsValidator,
                                                              mock( CountDownLatch.class ),
                                                              mock( CountDownLatch.class ) );
        params = Arrays.asList( "Script", "file" );
    }

    @Test
    public void errorMessageFetchedAndProjectNotCompiledIfNumberOfParamsWrong() throws Exception
    {
        when( numberOfParamsValidator.validationFailed() ).thenReturn( true );
        when( numberOfParamsValidator.errorMessage() ).thenReturn( "Dummy-Error-Message." );

        useCase.run( params );

        verify( numberOfParamsValidator ).validationFailed();
        verify( numberOfParamsValidator ).errorMessage();
        verify( scriptProjectBuilder, times( 0 ) ).buildProject();
    }

    @Test
    public void errorMessageNotFetchedButProjectCompiledIfNumberOfParamsOk() throws Exception
    {
        when( numberOfParamsValidator.validationFailed() ).thenReturn( false );
        when( numberOfParamsValidator.errorMessage() ).thenReturn( "Dummy-Error-Message." );

        useCase.run( params );

        verify( numberOfParamsValidator ).validationFailed();
        verify( numberOfParamsValidator, times( 0 ) ).errorMessage();
        verify( scriptProjectBuilder ).buildProject();
    }

}
