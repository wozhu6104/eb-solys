/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.loadfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;

public class LoadFileInteractionUseCaseImpl implements OpenFileInteractionUseCase, LoadFileProgressListener
{
    public static final String FILE_ALREADY_LOADED_MESSAGE = "File already loaded. Cannot load the same file again.";
    public static final String EMPTY_FILE_MESSAGE = "File is empty. Cannot load empty file.";
    public static final String FILE_NOT_FOUND_MESSAGE = "File not found: ";

    private volatile OpenFileInteractionCallback callback;
    private final LoadFileService loadFileService;
    private final UserMessageLogger userMessageLogger;
    private final ImporterRegistry importerRegistry;
    private boolean resultOk;

    public LoadFileInteractionUseCaseImpl(OpenFileInteractionCallback callback, LoadFileService loadFileService,
            ImporterRegistry importerRegistry, UserMessageLogger userMessageLogger)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "loadFileService", loadFileService );
        RangeCheckUtils.assertReferenceParameterNotNull( "importerRegistry", importerRegistry );
        RangeCheckUtils.assertReferenceParameterNotNull( "userMessageLogger", userMessageLogger );

        this.callback = callback;
        this.loadFileService = loadFileService;
        this.importerRegistry = importerRegistry;
        this.userMessageLogger = userMessageLogger;

        loadFileService.registerFileProgressListener( this );
    }

    @Override
    public void openFile(String pathToFile)
    {
        if (!loadFileService.isFileNotTooBig( pathToFile ))
        {
            callback.onFileTooBig( pathToFile );
            return;
        }
        if (!loadFileService.isFileNotLoaded( pathToFile ))
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR, FILE_ALREADY_LOADED_MESSAGE );
            callback.onFileAlreadyLoaded( pathToFile );
            return;
        }
        if (!fileExists( pathToFile ))
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR, FILE_NOT_FOUND_MESSAGE + pathToFile );
            callback.onFileNotFound( pathToFile );
            return;
        }
        if (loadFileService.isFileEmpty( pathToFile ))
        {
            userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR, EMPTY_FILE_MESSAGE );
            callback.onFileEmpty( pathToFile );
            return;
        }
        startLoadingFile( pathToFile );
    }

    private boolean fileExists(String pathToFile)
    {
        return new File( pathToFile ).exists();
    }

    private void startLoadingFile(final String pathToFile)
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "LoadFileInteractionUseCase.startLoadingFile", () -> {
            resultOk = loadFileService.loadFile( pathToFile );
            postResult();
        } ) );
    }

    private void postResult()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    if (resultOk)
                    {
                        callback.onFileLoadedSucessfully();
                    }
                    else
                    {
                        callback.onFileLoadingFailed();
                    }
                }

            }
        } );
    }

    @Override
    public void unregister()
    {
        loadFileService.unregisterFileProgressListener( this );
        callback = null;
    }

    @Override
    public void cancelLoadingFile()
    {
        loadFileService.cancelFileLoading();
    }

    @Override
    public List<List<String>> getAnotherFilesTypesAndExtensions()
    {
        List<List<String>> namesAndExtensions = importerRegistry.getSupportedFileTypesAndExtensions();

        List<String> names = namesAndExtensions.get( 0 );
        List<String> extensions = namesAndExtensions.get( 1 );
        String allExtensions = "";

        for (int i = 0; i < extensions.size(); i++)
        {
            String extension = extensions.get( i );
            extension = "*." + extension;
            extensions.set( i, extension );

            String name = names.get( i );
            name = name + " (" + extension + ")";
            names.set( i, name );

            allExtensions += extension;
            if (i != extensions.size() - 1)
            {
                allExtensions += ";";
            }
        }

        List<String> namesWithAll = new ArrayList<String>();
        List<String> extensionsWithAll = new ArrayList<String>();

        namesWithAll.add( "All Importable Files" );
        namesWithAll.addAll( names );

        extensionsWithAll.add( allExtensions );
        extensionsWithAll.addAll( extensions );

        namesAndExtensions.set( 0, namesWithAll );
        namesAndExtensions.set( 1, extensionsWithAll );

        return namesAndExtensions;
    }

    @Override
    public void onLoadFileStarted(final String pathToFile)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                callback.onFileLoadingStarted( pathToFile );
            }
        } );
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
    }

    @Override
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

}
