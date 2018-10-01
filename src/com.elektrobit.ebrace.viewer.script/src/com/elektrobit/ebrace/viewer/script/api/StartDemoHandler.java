/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.api;

import java.net.URI;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.viewer.common.constants.DemoConstants;
import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;
import com.elektrobit.ebrace.viewer.script.handler.DemoProgressWindow;
import com.elektrobit.ebrace.viewer.script.handler.DemoProgressWindow.STATE;

import lombok.extern.log4j.Log4j;

@Log4j
public class StartDemoHandler
        implements
            OpenFileInteractionCallback,
            LoadFileProgressNotifyCallback,
            ScriptChangedNotifyCallback
{
    private static final int SECONDS_TO_MS = 1000;

    private static final String DEMO_SCRIPT_CLASS_NAME = "DemoUseCase";
    private static final String SCRIPT_EXECUTE_METHOD = "execute";
    private static final String RUNTIME_PERSPECTIVE_ID = "com.elektrobit.ebrace.resourceconsumptionanalysis";
    private final OpenFileInteractionUseCase loadFileInteractionUseCase;

    private final LoadFileProgressNotifyUseCase loadFileProgressNotifyUseCase;

    private DemoProgressWindow progressWindow;

    private final ResourcesModelManager resourcesManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
            .getService();

    private final ScriptChangedNotifyUseCase scriptChangedNotifyUseCase;

    public StartDemoHandler()
    {
        loadFileInteractionUseCase = UseCaseFactoryInstance.get().makeLoadFileInteractionUseCase( this );
        loadFileProgressNotifyUseCase = UseCaseFactoryInstance.get()
                .makeLoadFileProgressNotifyUseCase( this, getPathToDemoRaceFile() );
        scriptChangedNotifyUseCase = UseCaseFactoryInstance.get().makeScriptChangedNotifyUseCase( this );
    }

    @UseStatLog(UseStatLogTypes.DEMO_MODE_STARTED)
    public void start()
    {
        openProgresWindow();
        startDemo();
    }

    private void openProgresWindow()
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        progressWindow = new DemoProgressWindow( shell );
        progressWindow.create();
        progressWindow.setBlockOnOpen( false );
        progressWindow.open();
    }

    private void startDemo()
    {
        if (!isRuntimePerspectiveActive())
        {
            switchToRuntimePerspective();
            waitSeconds( 2 );
        }
        progressWindow.setState( STATE.LOADING_FILE );
        startFileLoading();
    }

    private boolean isRuntimePerspectiveActive()
    {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
        String activePerspectiveId = activePage.getPerspective().getId();
        return activePerspectiveId.equals( RUNTIME_PERSPECTIVE_ID );
    }

    private synchronized void waitSeconds(int seconds)
    {
        try
        {
            log.info( "StarDemoHandler - waitSeconds() " + seconds );
            Thread.sleep( seconds * SECONDS_TO_MS );
            log.info( "StarDemoHandler - waitSeconds-END() " );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void switchToRuntimePerspective()
    {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        try
        {
            PlatformUI.getWorkbench().showPerspective( RUNTIME_PERSPECTIVE_ID, activeWorkbenchWindow );
        }
        catch (WorkbenchException e)
        {
            e.printStackTrace();
        }
    }

    private void startFileLoading()
    {
        loadFileInteractionUseCase.openFile( getPathToDemoRaceFile() );
    }

    private String getPathToDemoRaceFile()
    {
        URI pathURI = FileHelper.locateFileInBundle( ViewerScriptPlugin.PLUGIN_ID, DemoConstants.DEMO_RACE_FILE_PATH );
        String path = pathURI.getPath();
        return path;
    }

    @Override
    public void onFileLoadingStarted(String pathToFile)
    {
        loadFileInteractionUseCase.unregister();
    }

    @Override
    public void onFileTooBig(String pathToFile)
    {
        log.error( "Demo file too big to load" );
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
    }

    @Override
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        log.info( "StartDemoHandler onLoadFileDone()" );
        loadFileProgressNotifyUseCase.unregister();

        new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                startScript();
            }
        }, "Start Demo Script Thread" ).start();

    }

    private void startScript()
    {
        log.info( "StartDemoHandler startScript()" );
        RaceScriptResourceModel scriptModel = waitUntilScriptMethodIsAvailable();
        log.info( "StartDemoHandler startScript() - script loaded" );
        waitSeconds( 2 );
        log.info( "StartDemoHandler startScript() - setting window state" );
        Display.getDefault().syncExec( new Runnable()
        {
            @Override
            public void run()
            {
                progressWindow.setState( STATE.EXECUTING_SCRIPT );
            }
        } );
        log.info( "StartDemoHandler startScript() - starting script method" );
        RunScriptInteractionUseCase runScriptUseCase = UseCaseFactoryInstance.get().makeRunScriptInteractionUseCase();
        runScriptUseCase.runScriptWithGlobalMethod( scriptModel.getScriptInfo(), SCRIPT_EXECUTE_METHOD );
        log.info( "StartDemoHandler startScript() - script method started" );
    }

    private RaceScriptResourceModel getLoadedScriptObject()
    {
        List<RaceScriptResourceModel> scripts = resourcesManager.getAllScripts();
        for (RaceScriptResourceModel script : scripts)
        {
            if (script.getName().equals( DEMO_SCRIPT_CLASS_NAME ))
            {
                return script;
            }
        }
        return null;
    }

    private RaceScriptResourceModel waitUntilScriptMethodIsAvailable()
    {
        while (true)
        {
            RaceScriptResourceModel scriptModel = getLoadedScriptObject();
            if (scriptModel != null)
            {
                List<RaceScriptMethod> globalMethods = scriptModel.getScriptInfo().getGlobalMethods();
                for (RaceScriptMethod method : globalMethods)
                {
                    if (method.getMethodName().equals( SCRIPT_EXECUTE_METHOD ))
                    {
                        return scriptModel;
                    }
                }
            }

            log.warn( "waiting for script methods loading" );
            waitSeconds( 1 );
        }
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

    @Override
    public void scriptInfoChanged(RaceScriptInfo script)
    {
        if (script.getName().equals( DEMO_SCRIPT_CLASS_NAME ) && script.isRunning() == false)
        {
            onScriptFinished();
        }
    }

    private void onScriptFinished()
    {
        log.info( "StartDemoHandler onScriptFinished()" );
        Display.getDefault().syncExec( new Runnable()
        {
            @UseStatLog(UseStatLogTypes.DEMO_MODE_FINISHED)
            @Override
            public void run()
            {
                progressWindow.setState( STATE.DONE );
            }
        } );

        stop();
    }

    private void stop()
    {
        scriptChangedNotifyUseCase.unregister();
    }

    @Override
    public void onFileLoadedSucessfully()
    {
    }

    @Override
    public void onFileLoadingFailed()
    {
    }

    @Override
    public void onFileAlreadyLoaded(String pathToFile)
    {
    }

    @Override
    public void onFileEmpty(String pathToFile)
    {
    }

    @Override
    public void onFileNotFound(String pathToFile)
    {
    }
}
