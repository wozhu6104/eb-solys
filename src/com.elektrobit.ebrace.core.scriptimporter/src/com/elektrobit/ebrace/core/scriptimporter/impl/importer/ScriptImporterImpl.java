/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptimporter.impl.importer;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller.Notifier;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.scriptimporter.api.ImportScriptStatusListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptImporterService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuildListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

import lombok.extern.log4j.Log4j;

@Log4j
public class ScriptImporterImpl implements ScriptImporterService
{
    private final ResourcesModelManager resourcesModelManager;
    private final GenericListenerCaller<ImportScriptStatusListener> statusListeners = new GenericListenerCaller<ImportScriptStatusListener>();
    private final ScriptProjectBuilderService scriptProjectBuilder;

    public ScriptImporterImpl(ResourcesModelManager resourcesModelManager, RaceScriptLoader raceScriptLoader,
            ScriptProjectBuilderService scriptProjectBuilder)
    {
        this.resourcesModelManager = resourcesModelManager;
        this.scriptProjectBuilder = scriptProjectBuilder;
    }

    @Override
    public void importUserScript(final File sourceXtendScript)
    {
        log.info( "Script imported " + sourceXtendScript.getName() + "." );
        importScript( sourceXtendScript, true );
    }

    private synchronized void importScript(File sourceXtendScript, boolean userScript)
    {

        if (!sourceXtendScript.exists())
        {
            notifyScriptImportFailed();
            return;
        }

        final String scriptName = FileHelper.removeExtension( sourceXtendScript.getName() );
        if (isScriptAlreadyExists( scriptName ))
        {
            notifyScriptAlreadyExists();
            return;
        }

        waitForScriptCompilationAndNotifyListener( sourceXtendScript, scriptName, userScript );
    }

    private boolean isScriptAlreadyExists(String scriptName)
    {
        return resourcesModelManager.scriptAlreadyExists( scriptName );
    }

    private void notifyScriptImportSuccessful(String scriptName)
    {
        statusListeners.notifyListeners( new Notifier<ImportScriptStatusListener>()
        {
            @Override
            public void notify(ImportScriptStatusListener listener)
            {
                listener.onScriptImportSuccessful();
            }
        } );
    }

    private void notifyScriptImportFailed()
    {
        statusListeners.notifyListeners( new Notifier<ImportScriptStatusListener>()
        {
            @Override
            public void notify(ImportScriptStatusListener listener)
            {
                listener.onScriptImportFailed();
            }
        } );
    }

    private void notifyScriptAlreadyExists()
    {
        statusListeners.notifyListeners( new Notifier<ImportScriptStatusListener>()
        {
            @Override
            public void notify(ImportScriptStatusListener listener)
            {
                listener.onScriptAlreadyExists();
            }
        } );
    }

    // TODO rage#17.29: Clean up
    private void waitForScriptCompilationAndNotifyListener(File sourceXtendScript, final String scriptName,
            boolean userScript)
    {
        CountDownLatch waitForProjectBuildDoneLatch = new CountDownLatch( 1 );

        ScriptProjectBuildListener listenerNotifier = (scripts) -> {
            boolean resultOk = false;
            for (ScriptData nextScript : scripts)
            {
                if (nextScript.getName().equals( scriptName ))
                {
                    resultOk = true;
                    break;
                }
            }
            waitForProjectBuildDoneLatch.countDown();
            if (resultOk)
            {
                notifyScriptImportSuccessful( scriptName );
            }
            else
            {
                notifyScriptImportFailed();
            }

        };
        scriptProjectBuilder.addScriptBuildListener( listenerNotifier );
        if (userScript)
        {
            scriptProjectBuilder.addUserScripts( Arrays.asList( sourceXtendScript ) );
        }
        else
        {
            scriptProjectBuilder.addPreinstalledScripts( Arrays.asList( sourceXtendScript ) );
        }
        try
        {
            boolean result = waitForProjectBuildDoneLatch.await( 10, TimeUnit.SECONDS );
            if (!result)
            {
                System.out.println( "Did not compile correctly." );
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        scriptProjectBuilder.removeScriptBuildListener( listenerNotifier );
    }

    @Override
    public void importPreinstalledScript(final File sourceXtendScript)
    {
        importScript( sourceXtendScript, false );
    }

    @Override
    public void addListener(ImportScriptStatusListener scriptImportStatusListener)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptImportStatusListener", scriptImportStatusListener );

        statusListeners.add( scriptImportStatusListener );
    }

    @Override
    public void removeListener(ImportScriptStatusListener scriptImportStatusListener)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptImportStatusListener", scriptImportStatusListener );

        statusListeners.remove( scriptImportStatusListener );
    }

}
