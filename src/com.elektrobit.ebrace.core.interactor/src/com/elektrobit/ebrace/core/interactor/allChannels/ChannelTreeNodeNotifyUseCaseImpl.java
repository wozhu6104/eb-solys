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
import com.elektrobit.ebrace.core.interactor.api.allChannels.ChannelTreeNodeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.ChannelTreeNodeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channels.NodeFilter;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.common.NotificationAccumulator;
import com.elektrobit.ebrace.core.interactor.common.NotificationAccumulatorCallback;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannelsChangedListener;

public class ChannelTreeNodeNotifyUseCaseImpl
        implements
            RuntimeEventChannelsChangedListener,
            ChannelTreeNodeNotifyUseCase,
            NotificationAccumulatorCallback
{
    private static final int TREE_REFRESH_TIMEOUT_MS = 500;

    private final RuntimeEventChannelManager runtimeEventChannelManager;
    private final NotificationAccumulator notificationAccumulator;

    private ChannelTreeNodeNotifyCallback callback;
    private ServiceRegistration<?> serviceRegistration;
    private String searchTerm;

    public ChannelTreeNodeNotifyUseCaseImpl(ChannelTreeNodeNotifyCallback callback,
            RuntimeEventChannelManager runtimeEventChannelManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "RuntimeEventChannelManager", runtimeEventChannelManager );
        this.runtimeEventChannelManager = runtimeEventChannelManager;
        this.callback = callback;

        notificationAccumulator = new NotificationAccumulator( TREE_REFRESH_TIMEOUT_MS, this );

        registerForUpdates();
        onAccumulatedNotification();
    }

    private void registerForUpdates()
    {
        serviceRegistration = GenericOSGIServiceRegistration.registerService( RuntimeEventChannelsChangedListener.class,
                                                                              this );
    }

    @Override
    public void onAccumulatedNotification()
    {
        UseCaseExecutor.schedule( new UseCaseRunnable( "ChannelTreeNodeNotifyUseCase.onAccumulatedNotification",
                                                       () -> computeTreeAndPostToCallback() ) );
    }

    private void computeTreeAndPostToCallback()
    {
        List<RuntimeEventChannel<?>> allRuntimeEventChannels = runtimeEventChannelManager.getRuntimeEventChannels();
        ChannelToNodeConverter channelToNodeConverter = new ChannelToNodeConverter();
        ChannelTreeNode rootNode = channelToNodeConverter.convert( allRuntimeEventChannels );
        NodeFilter nodeFilter = new NodeFilter( searchTerm );
        nodeFilter.filter( rootNode );

        UIExecutor.post( () -> {
            if (callback != null)
            {
                callback.onChannelTreeChanged( rootNode );
            }
        } );
    }

    @Override
    public void setSearchTerm(String searchTerm)
    {
        this.searchTerm = searchTerm;
        notificationAccumulator.postNotification();
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
        callback = null;
    }

}
