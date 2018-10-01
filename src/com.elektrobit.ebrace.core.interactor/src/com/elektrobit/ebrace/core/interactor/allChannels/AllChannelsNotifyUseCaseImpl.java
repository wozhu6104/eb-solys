/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.allChannels;

import java.util.List;

import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.common.NotificationAccumulator;
import com.elektrobit.ebrace.core.interactor.common.NotificationAccumulatorCallback;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelRemovedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannelsChangedListener;

public class AllChannelsNotifyUseCaseImpl
        implements
            RuntimeEventChannelsChangedListener,
            AllChannelsNotifyUseCase,
            NotificationAccumulatorCallback,
            ChannelRemovedListener
{
    private static final int TREE_REFRESH_TIMEOUT_MS = 500;

    private final NotificationAccumulator notificationAccumulator;
    private final RuntimeEventChannelManager runtimeEventChannelManager;
    private final RuntimeEventAcceptor runtimeEventAcceptor;

    private AllChannelsNotifyCallback callback;
    private ServiceRegistration<?> serviceRegistration;

    public AllChannelsNotifyUseCaseImpl(AllChannelsNotifyCallback callback,
            RuntimeEventChannelManager runtimeEventChannelManager, RuntimeEventAcceptor acceptor)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "RuntimeEventChannelManager", runtimeEventChannelManager );

        this.runtimeEventChannelManager = runtimeEventChannelManager;
        this.runtimeEventAcceptor = acceptor;
        this.callback = callback;

        notificationAccumulator = new NotificationAccumulator( TREE_REFRESH_TIMEOUT_MS, this );

        registerForUpdates();
        onAccumulatedNotification();
    }

    private void registerForUpdates()
    {
        serviceRegistration = GenericOSGIServiceRegistration.registerService( RuntimeEventChannelsChangedListener.class,
                                                                              this );
        runtimeEventAcceptor.addChannelRemovedListener( this );
    }

    @Override
    public void onRuntimeEventChannelsChanged()
    {
        notificationAccumulator.postNotification();
    }

    @Override
    public void unregister()
    {
        serviceRegistration.unregister();
        runtimeEventAcceptor.removeChannelRemovedListener( this );
        callback = null;
    }

    @Override
    public void onAccumulatedNotification()
    {
        List<RuntimeEventChannel<?>> runtimeEventChannels = runtimeEventChannelManager.getRuntimeEventChannels();
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onAllChannelsChanged( runtimeEventChannels );
            }
        } );
    }

    @Override
    public void onRuntimeEventChannelRemoved(RuntimeEventChannel<?> channel)
    {
        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onChannelRemoved( channel );
            }
        } );
    }

}
