/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.application.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.elektrobit.ebrace.core.activation.api.Version;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutorIF;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessExecutorInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageNotifyUseCase;
import com.elektrobit.ebrace.dev.debug.annotations.api.EnterExitPrinter;
import com.elektrobit.ebrace.dev.debug.annotations.api.InterceptMethod;

public class Application implements IApplication, UserMessageLoggerNotifyCallback
{
    private final Version version = Activator.SERVICE_CONTEXT.get( Version.class );
    private final UserMessageNotifyUseCase userMessageNotifyUsecase = UseCaseFactoryInstance.get()
            .makeUserLoggerMessageNotifyUseCase( this );

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    @Override
    public Object start(IApplicationContext context) throws Exception
    {
        printWelcomeLineAndVersion();
        setUIExecutor();

        printCommandLineParams( context );
        registerUIResourcesActionsHandler();
        HeadlessExecutorInteractionUseCase headlessExecutor = UseCaseFactoryInstance.get()
                .makeHeadlessExecutorInteractionUseCase();
        List<String> commandLineParams = getCommandLineParams( context );
        if (headlessExecutor.run( commandLineParams ))
        {
            System.out.println( "\n=== EB solys auto stopped without an error ===" );
            return IApplication.EXIT_OK;
        }
        else
        {
            System.out.println( "\n=== EB solys auto stopped with an error ===" );
            return new Integer( 1 );
        }

    }

    private void printWelcomeLineAndVersion()
    {
        System.out.println( "\n\n=== EB solys auto starting ===\n" );
        System.out.println( "Version " + version.getName() );
    }

    private void printCommandLineParams(IApplicationContext context)
    {
        final List<String> appArgs = getCommandLineParams( context );
        System.out.println( "command line arguments" );
        System.out.println( "[" );
        for (final String arg : appArgs)
        {
            System.out.println( arg );
        }
        System.out.println( "] \n" );
    }

    private List<String> getCommandLineParams(IApplicationContext context)
    {
        final Map<?, ?> args = context.getArguments();
        final String[] appArgs = (String[])args.get( "application.args" );
        return Arrays.asList( appArgs );
    }

    private void setUIExecutor()
    {
        UIExecutor.set( new UIExecutorIF()
        {
            @Override
            public void execute(Runnable r)
            {
                r.run();
            }
        } );
    }

    private void registerUIResourcesActionsHandler()
    {
        new ConsoleUIActionsHandler();
    }

    @Override
    public void onLogUserMessage(UserMessageLoggerTypes type, String message)
    {
        System.out.println( "" + type + ": " + message );
    }

    @Override
    public void stop()
    {
        userMessageNotifyUsecase.unregister();
    }

}
