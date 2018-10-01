/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.splitfile.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loadfile.LoadFileProgressNotifyUseCase;
import com.elektrobit.ebrace.viewer.common.constants.DemoConstants;
import com.elektrobit.ebrace.viewer.preferences.util.PersistentLoadedOfflineFilesPreferences;

public class LoadingFileProgressRunningOperation implements IRunnableWithProgress, LoadFileProgressNotifyCallback
{
    private static final String JOB_DESCRIPTION = "Loading File ";
    private final String path;
    private static final int _100_PERCENT = 100;
    private int lastPercent = 0;
    private volatile IProgressMonitor progressMonitor;

    private volatile boolean finished = false;

    private final LoadFileProgressNotifyUseCase loadFileProgressNotifyUseCase;

    public LoadingFileProgressRunningOperation(String path)
    {
        this.path = path;
        loadFileProgressNotifyUseCase = UseCaseFactoryInstance.get().makeLoadFileProgressNotifyUseCase( this, path );
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        progressMonitor = monitor;
        progressMonitor.beginTask( JOB_DESCRIPTION + path, _100_PERCENT );

        if (finished)
        {
            done();
            return;
        }

        waitForLoadingDone();

        if (progressMonitor.isCanceled())
        {
            throw new InterruptedException( "Loading of file " + path + " was canceled. All data has been cleared." );
        }
    }

    private synchronized void waitForLoadingDone()
    {
        while (!finished)
        {
            try
            {
                wait();

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private synchronized void notifyWaitingThread()
    {
        notifyAll();
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
        if (progressMonitor != null)
        {
            progressMonitor.worked( percentDone - lastPercent );
        }
        lastPercent = percentDone;
    }

    @Override
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        finished = true;

        if (progressMonitor != null)
        {
            done();
        }
        notifyWaitingThread();

        if (!path.contains( DemoConstants.DEMO_RACE_FILE_PATH ))
        {
            PersistentLoadedOfflineFilesPreferences.appendFileToRecentFilesList( path );
        }
    }

    @Override
    public void onLoadFileCanceled()
    {
        done();
    }

    private void done()
    {
        waitBeforeRemovingProgressWindow();
        progressMonitor.worked( _100_PERCENT - lastPercent );
        progressMonitor.done();
        finished = true;
        notifyWaitingThread();
        loadFileProgressNotifyUseCase.unregister();
    }

    private void waitBeforeRemovingProgressWindow()
    {
        try
        {
            Thread.sleep( 1000 );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
