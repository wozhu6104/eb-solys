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

import java.util.List;

import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.loaddatachunk.SystemCPUValuesNotifyUseCase;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RTargetHeaderCPUValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService;

public class SystemCPUValuesNotifyUseCaseImpl
        implements
            SystemCPUValuesNotifyUseCase,
            LoadFileProgressListener,
            ResetListener
{
    private final TargetHeaderMetaDataService targetHeaderMetaDataService;
    private SystemCPUValuesNotifyCallback callback;
    private final LoadFileService loadFileService;
    private ServiceRegistration<?> resetListenerServiceRegistration;

    public SystemCPUValuesNotifyUseCaseImpl(SystemCPUValuesNotifyCallback callback,
            TargetHeaderMetaDataService targetHeaderMetaDataService, LoadFileService loadFileService)
    {
        this.callback = callback;
        this.targetHeaderMetaDataService = targetHeaderMetaDataService;
        this.loadFileService = loadFileService;

        loadFileService.registerFileProgressListener( this );

        notifyCallbackAboutChangedSystemCPUValues( targetHeaderMetaDataService.getTargetHeaderCPUValues() );
    }

    public void setServiceRegistration(ServiceRegistration<?> serviceRegistration)
    {
        this.resetListenerServiceRegistration = serviceRegistration;
    }

    private void notifyCallbackAboutChangedSystemCPUValues(final List<RTargetHeaderCPUValue> systemCPUValues)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onSystemCPUValuesUpdated( systemCPUValues );
            }
        } );
    }

    @Override
    public void unregister()
    {
        resetListenerServiceRegistration.unregister();
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
    public void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        notifyCallbackAboutChangedSystemCPUValues( targetHeaderMetaDataService.getTargetHeaderCPUValues() );
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

    @Override
    public void onReset()
    {
        notifyCallbackAboutChangedSystemCPUValues( null );
    }

}
