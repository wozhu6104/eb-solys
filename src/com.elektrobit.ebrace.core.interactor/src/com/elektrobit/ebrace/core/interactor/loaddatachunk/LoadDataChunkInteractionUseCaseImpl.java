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
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.LoadDataChunkInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataNotifier;

import lombok.extern.log4j.Log4j;

@Log4j
public class LoadDataChunkInteractionUseCaseImpl implements LoadDataChunkInteractionUseCase
{
    private LoadDataChunkInteractionCallback callback;
    private final LoadFileService loadFileService;
    private final ClearChunkDataNotifier clearChunkDataNotifier;
    private volatile boolean loading = false;

    public LoadDataChunkInteractionUseCaseImpl(LoadDataChunkInteractionCallback callback,
            LoadFileService loadFileService, ClearChunkDataNotifier clearChunkDataNotifier)
    {
        this.callback = callback;
        this.loadFileService = loadFileService;
        this.clearChunkDataNotifier = clearChunkDataNotifier;
    }

    @Override
    public void loadDataChunk(final long startTimestamp, final long desiredChunkLengthTime)
    {
        if (loading)
        {
            notifyCallbackAboutLoadAlreadyActive( callback );
        }
        else if (desiredChunkLengthTime <= 0)
        {
            notifyCallbackAboutFailure( callback, "No file is open. Cannot load chunk." );
        }
        else
        {
            loading = true;
            UseCaseExecutor.schedule( new UseCaseRunnable( "LoadDataChunkInteractionUseCase.loadDataChunk", () -> {
                clearChunkDataNotifier.notifyClearChunkData();

                boolean loadFileSuccessful = loadFileService.loadFileFrom( startTimestamp, desiredChunkLengthTime );

                notifyCallback( loadFileService.getStartTimestamp(),
                                loadFileService.getEndTimestamp(),
                                loadFileService.getChunkStartTimestamp(),
                                loadFileService.getChunkEndTimestamp(),
                                loadFileSuccessful,
                                callback );

                loading = false;
            } ) );
        }
    }

    private void notifyCallbackAboutLoadAlreadyActive(final LoadDataChunkInteractionCallback callback)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                if (callback != null)
                {
                    log.info( "Loading chunk already active. Ignoring load data chunk call." );
                    callback.onLoadDataAlreadyActive();
                }
            }
        } );

    }

    private void notifyCallbackAboutFailure(final LoadDataChunkInteractionCallback callback, final String errorMessage)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                if (callback != null)
                {
                    log.info( "No file is open. Cannot load chunk." );
                    callback.onLoadDataFromFailed();
                }
            }
        } );

    }

    private void notifyCallback(final long fileStartTime, final long fileEndTime, final long chunkStartTime,
            final long chunkEndTime, final boolean successfull, final LoadDataChunkInteractionCallback callback)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                if (callback != null)
                {
                    if (successfull)
                    {
                        notifyCallbackAboutSuccess( fileStartTime,
                                                    fileEndTime,
                                                    chunkStartTime,
                                                    chunkEndTime,
                                                    callback );
                    }
                    else
                    {
                        notifyCallbackAboutFailure( callback, "LoadFileService couldn't load chunk." );
                    }
                }

            }
        } );

    }

    private void notifyCallbackAboutSuccess(final long fileStartTime, final long fileEndTime, final long chunkStartTime,
            final long chunkEndTime, final LoadDataChunkInteractionCallback callback)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onLoadDataFromSuccessful( fileStartTime, fileEndTime, chunkStartTime, chunkEndTime );
                }
            }
        } );

    }

    @Override
    public void unregister()
    {
        this.callback = null;
    }

}
