/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.connect;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.resources.api.manager.ResourceTreeChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class ConnectionsNotifyUseCaseImpl implements ConnectionsNotifyUseCase, ResourceTreeChangedListener
{
    private ConnectionsNotifyCallback callback;
    private final ResourcesModelManager resourcesModelManager;

    public ConnectionsNotifyUseCaseImpl(ConnectionsNotifyCallback callback, ResourcesModelManager resourcesModelManager)
    {
        this.callback = callback;
        this.resourcesModelManager = resourcesModelManager;

        resourcesModelManager.registerTreeListener( this );
        notifyCallback();
    }

    private void notifyCallback()
    {
        List<ResourceModel> connections = resourcesModelManager.getConnections();
        Runnable task = () -> {
            if (callback != null)
            {
                callback.onConnectionsChanged( connections );
            }
        };
        UIExecutor.post( task );
    }

    @Override
    public void unregister()
    {
        resourcesModelManager.unregisterTreeListener( this );
        callback = null;
    }

    @Override
    public void onResourceTreeChanged()
    {
        notifyCallback();
    }

    @Override
    public void onResourceDeleted(ResourceModel resourceModel)
    {
        notifyCallback();
    }

    @Override
    public void onResourceRenamed(ResourceModel resourceModel)
    {
        notifyCallback();
    }

    @Override
    public void onResourceAdded(ResourceModel resourceModel)
    {
        notifyCallback();
    }

    @Override
    public void onOpenResourceModel(ResourceModel resourceModel)
    {
    }

}
