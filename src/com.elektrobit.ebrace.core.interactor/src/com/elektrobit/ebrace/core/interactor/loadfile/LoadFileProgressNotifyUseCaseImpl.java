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

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyUseCase;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class LoadFileProgressNotifyUseCaseImpl implements LoadFileProgressListener, LoadFileProgressNotifyUseCase
{
    private LoadFileProgressNotifyCallback callback;
    private final LoadFileService loadFileService;
    private final String pathToFile;

    public LoadFileProgressNotifyUseCaseImpl(LoadFileProgressNotifyCallback callback, String pathToFile,
            LoadFileService loadFileService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "pathToFile", pathToFile );
        RangeCheckUtils.assertReferenceParameterNotNull( "loadFileService", loadFileService );

        this.callback = callback;
        this.pathToFile = pathToFile;
        this.loadFileService = loadFileService;

        loadFileService.registerFileProgressListener( this );
        postDoneIfFileAlreadyLoaded();
        postCanceledIfLoadingAlreadyFailed();
    }

    private void postDoneIfFileAlreadyLoaded()
    {
        if (!loadFileService.isFileNotLoaded( pathToFile ))
        {
            notifyLoadFileDone();
        }
    }

    private void postCanceledIfLoadingAlreadyFailed()
    {
        if (loadFileService.isFileLoadingFailed( pathToFile ))
        {
            notifyLoadingCanceled();
        }
    }

    @Override
    public void unregister()
    {
        loadFileService.unregisterFileProgressListener( this );
        callback = null;
    }

    @Override
    public void onLoadFileProgressChanged(final int percentDone)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onLoadFileProgressChanged( percentDone );
                }
            }
        } );

    }

    @Override
    public void onLoadFileDone(final long fileStartTime, final long fileEndTime, final long chunkStartTime,
            final long chunkEndTime)
    {
        notifyLoadFileDone();
    }

    private void notifyLoadFileDone()
    {
        final long fileStartTimestamp = loadFileService.getStartTimestamp();
        final long fileEndTimestamp = loadFileService.getEndTimestamp();
        final long chunkStartTimestamp = loadFileService.getChunkStartTimestamp();
        final long chunkEndTimestamp = loadFileService.getChunkEndTimestamp();

        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onLoadFileDone( fileStartTimestamp,
                                             fileEndTimestamp,
                                             chunkStartTimestamp,
                                             chunkEndTimestamp );
                }
            }
        } );
    }

    @Override
    public void onLoadFileCanceled()
    {
        notifyLoadingCanceled();
    }

    private void notifyLoadingCanceled()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onLoadFileCanceled();
                }
            }
        } );
    }

    @Override
    public void onLoadFileStarted(String pathToFile)
    {
    }
}
