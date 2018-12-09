/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.splitfile;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.tracefile.api.SplitFileListener;
import com.elektrobit.ebrace.core.tracefile.api.SplitFileService;

public class SplitFileInteractionUseCaseImpl implements SplitFileListener, SplitFileInteractionUseCase
{
    private final SplitFileService splitFileService;
    private SplitFileInteractionCallback callback;

    public SplitFileInteractionUseCaseImpl(SplitFileInteractionCallback callback, SplitFileService splitFileService)
    {
        this.callback = callback;
        this.splitFileService = splitFileService;
        registerForUpdates();
    }

    @Override
    public void startSplitting(final String path)
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "SplitFileInteractionUseCase.startSplitting",
                                                       () -> splitFileService.startSplittingFile( path ) ) );
    }

    private void registerForUpdates()
    {
        splitFileService.registerSplitDoneListener( this );
    }

    @Override
    public void unregister()
    {
        splitFileService.unregisterSplitDoneListener( this );
        callback = null;
    }

    @Override
    public void onSplittingStarted()
    {
        postSplittingStartedToCallBack();
    }

    @Override
    public void onSplittingDone()
    {
        postSplittingDoneToCallBack();
    }

    @Override
    public void onSplittingError(String message)
    {
        postErrorToCallBack( message );
    }

    private void postSplittingStartedToCallBack()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onFileSplittingStarted();
                }
            }
        } );
    }

    private void postSplittingDoneToCallBack()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onFileSplittingDone();
                }
            }
        } );
    }

    private void postErrorToCallBack(final String message)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onFileSplittingError( message );
                }
            }
        } );
    }
}
