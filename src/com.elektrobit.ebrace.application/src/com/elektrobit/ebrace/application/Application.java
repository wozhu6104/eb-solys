/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.application.statusline.StatusLineMessage;
import com.elektrobit.ebrace.application.threadprofiling.ThreadCPUProfiler;
import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutorIF;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;

import lombok.extern.log4j.Log4j;

/**
 * This class controls all aspects of the application's execution
 */
@Log4j
public class Application implements IApplication
{
    // Enable this if you want track swt resource handling.
    // You have also to enable 'debug' and 'trace/graphics'
    // in the tracing tab of the run configuration of the bundle
    // 'org.eclipse.ui'
    public final static boolean ENABLE_SLEAK_APP = false;

    private static final Logger profLog = Logger.getLogger( "rprof" );

    private final ThreadCPUProfiler profiler = new ThreadCPUProfiler();

    @UseStatLog(UseStatLogTypes.APP_STARTUP_STARTED)
    @Override
    public Object start(IApplicationContext context) throws Exception
    {
        StatusLineMessage statusLine = new StatusLineMessage();
        UseCaseFactoryInstance.get().CreateStatusLineTextNotifyUseCase( statusLine );

        if (profLog.isDebugEnabled())
        {
            profiler.start();
        }

        return runUI( context );
    }

    private Object runUI(IApplicationContext context)
    {
        Display display = PlatformUI.createDisplay();

        try
        {
            ProVersion.getInstance().setActive( true );

            setUIExecutorForGui();
            int returnCode = PlatformUI.createAndRunWorkbench( display, new ApplicationWorkbenchAdvisor() );

            saveWorkspaceBeforeShutdown();

            if (returnCode == PlatformUI.RETURN_RESTART)
            {
                return IApplication.EXIT_RESTART;
            }
            else
            {
                return IApplication.EXIT_OK;
            }
        }
        catch (Exception e)
        {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        }
        finally
        {
            display.dispose();
        }
        return null;
    }

    private void saveWorkspaceBeforeShutdown()
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        try
        {
            IStatus iStatus = ws.save( true, null );
            log.info( "Save workspace on shutdown status " + iStatus );
        }
        catch (CoreException e)
        {
            log.error( "Saving workspace before shutdown failed", e );
        }
    }

    private void setUIExecutorForGui()
    {
        UIExecutor.set( new UIExecutorIF()
        {
            @Override
            public void execute(Runnable r)
            {
                Display.getDefault().asyncExec( r );
            }
        } );
    }

    @Override
    public void stop()
    {
        if (profLog.isDebugEnabled())
        {
            profiler.stop();
        }

        if (!PlatformUI.isWorkbenchRunning())
        {
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Display display = workbench.getDisplay();
        display.syncExec( new Runnable()
        {
            @Override
            public void run()
            {
                if (!display.isDisposed())
                {
                    workbench.close();
                }
            }
        } );
    }

}
