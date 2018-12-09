/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.script.importing;

import java.io.File;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.script.importing.ImportScriptInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.scriptimporter.api.ImportScriptStatusListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptImporterService;

public class ImportScriptInteractionUseCaseImpl implements ImportScriptInteractionUseCase, ImportScriptStatusListener
{
    private ImportScriptInteractionCallback callback;
    private final ScriptImporterService scriptImporterService;

    public ImportScriptInteractionUseCaseImpl(ImportScriptInteractionCallback callback,
            ScriptImporterService scriptImporterService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "importScriptInteractionCallback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptImporterService", scriptImporterService );

        this.callback = callback;
        this.scriptImporterService = scriptImporterService;
        scriptImporterService.addListener( this );
    }

    @Override
    public void importUserScript(final File xtendFile)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptFile", xtendFile );

        UseCaseExecutor.schedule( new UseCaseRunnable( "ImportScriptInteractionUseCase.importUserScript",
                                                       () -> scriptImporterService.importUserScript( xtendFile ) ) );

    }

    @Override
    public void importPreinstalledScript(final File xtendFile)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptFile", xtendFile );

        UseCaseExecutor
                .schedule( new UseCaseRunnable( "ImportScriptInteractionUseCase.importPreinstalledScript",
                                                () -> scriptImporterService.importPreinstalledScript( xtendFile ) ) );

    }

    @Override
    public void onScriptImportSuccessful()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onScriptImportSuccessful();
                }
            }
        } );
    }

    @Override
    public void onScriptAlreadyExists()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onScriptAlreadyExists();
                }
            }
        } );
    }

    @Override
    public void onScriptImportFailed()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onScriptImportFailed();
                }
            }
        } );

    }

    @Override
    public void unregister()
    {
        scriptImporterService.removeListener( this );
        callback = null;
    }

}
