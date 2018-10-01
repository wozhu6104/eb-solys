/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.loaddatachunk;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkNotifyUseCase;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class LoadDataChunkNotifyUseCaseImpl implements LoadDataChunkNotifyUseCase, LoadFileProgressListener
{
    private LoadDataChunkNotifyCallback callback;
    private final LoadFileService loadFileService;

    public LoadDataChunkNotifyUseCaseImpl(LoadDataChunkNotifyCallback callback, LoadFileService loadFileService)
    {
        this.callback = callback;
        this.loadFileService = loadFileService;

        loadFileService.registerFileProgressListener( this );
        notifyCallbackAboutChangedChunk( loadFileService.getStartTimestamp(),
                                         loadFileService.getEndTimestamp(),
                                         loadFileService.getChunkStartTimestamp(),
                                         loadFileService.getChunkEndTimestamp() );
    }

    @Override
    public void unregister()
    {
        loadFileService.unregisterFileProgressListener( this );
        callback = null;
    }

    @Override
    public void onLoadFileStarted(String pathToFile)
    {
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
    }

    @Override
    public void onLoadFileDone(final long fileStartTime, final long fileEndTime, final long chunkStartTime,
            final long chunkEndTime)
    {
        notifyCallbackAboutChangedChunk( fileStartTime, fileEndTime, chunkStartTime, chunkEndTime );
    }

    private void notifyCallbackAboutChangedChunk(final long fileStartTime, final long fileEndTime,
            final long chunkStartTime, final long chunkEndTime)
    {
        final long selectionLength = chunkEndTime - chunkStartTime;

        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onDataChunkChanged( fileStartTime,
                                                 fileEndTime,
                                                 chunkStartTime,
                                                 chunkEndTime,
                                                 selectionLength );
            }
        } );
    }

    @Override
    public void onLoadFileCanceled()
    {
    }
}
