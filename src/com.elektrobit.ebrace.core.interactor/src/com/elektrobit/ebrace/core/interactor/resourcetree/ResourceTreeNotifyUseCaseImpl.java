/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.resourcetree;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResouceTreeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.resources.api.manager.ResourceTreeChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class ResourceTreeNotifyUseCaseImpl implements ResouceTreeNotifyUseCase, ResourceTreeChangedListener
{
    private ResourceTreeNotifyCallback callback;
    private final ResourcesModelManager resourcesModelManager;

    public ResourceTreeNotifyUseCaseImpl(ResourceTreeNotifyCallback callback,
            ResourcesModelManager resourcesModelManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        this.callback = callback;
        this.resourcesModelManager = resourcesModelManager;
        resourcesModelManager.registerTreeListener( this );
        collectAndPostData();
    }

    private void collectAndPostData()
    {
        List<ResourcesFolder> folders = resourcesModelManager.getRootFolders();
        if (callback != null)
        {
            callback.onNewResourceTreeData( folders );
        }
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
        collectAndPostDataToUIThread();
    }

    @Override
    public void onResourceAdded(final ResourceModel resourceModel)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                collectAndPostData();
                if (callback != null)
                {
                    callback.revealResource( resourceModel );
                }
            }
        } );
    }

    @Override
    public void onResourceDeleted(ResourceModel resourceModel)
    {
        collectAndPostDataToUIThread();
    }

    @Override
    public void onResourceRenamed(ResourceModel resourceModel)
    {
        collectAndPostDataToUIThread();
    }

    private void collectAndPostDataToUIThread()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                collectAndPostData();
            }
        } );
    }

    @Override
    public void onOpenResourceModel(final ResourceModel resourceModel)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                collectAndPostData();
                if (callback != null)
                {
                    callback.revealResource( resourceModel );
                    callback.openResource( resourceModel );
                }
            }
        } );
    }
}
