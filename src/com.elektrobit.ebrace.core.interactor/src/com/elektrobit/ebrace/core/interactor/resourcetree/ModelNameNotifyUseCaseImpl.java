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
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.resources.api.manager.ResourceChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourceTreeChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class ModelNameNotifyUseCaseImpl
        implements
            ModelNameNotifyUseCase,
            ResourceChangedListener,
            ResourceTreeChangedListener
{
    private final ResourcesModelManager resourcesModelManager;
    private ModelNameNotifyCallback callback;
    private ResourceModel resourceModel;

    public ModelNameNotifyUseCaseImpl(ModelNameNotifyCallback callback, ResourcesModelManager resourcesModelManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        this.callback = callback;
        this.resourcesModelManager = resourcesModelManager;

        registerListener();
    }

    private void registerListener()
    {
        resourcesModelManager.registerResourceListener( this );
        resourcesModelManager.registerTreeListener( this );
    }

    @Override
    public void register(ResourceModel resourceModel)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "resource model", resourceModel );
        this.resourceModel = resourceModel;
        postNewName();
        checkIfModelNotAlreadyDeleted( resourceModel );
    }

    private void postNewName()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onNewResourceName( resourceModel.getName() );
                }
            }
        } );
    }

    private void checkIfModelNotAlreadyDeleted(ResourceModel resourceModel)
    {
        List<ResourceModel> allResources = resourcesModelManager.getResources();
        if (!allResources.contains( resourceModel ))
        {
            postResourceDeleted();
        }
    }

    @Override
    public void onResourceModelChannelsChanged(ResourceModel changedResourceModel)
    {
    }

    @Override
    public void unregister()
    {
        resourcesModelManager.unregisterResourceListener( this );
        resourcesModelManager.unregisterTreeListener( this );
        callback = null;
    }

    @Override
    public void onResourceDeleted(ResourceModel deletedResourceModel)
    {
        if (resourceModel.equals( deletedResourceModel ))
        {
            postResourceDeleted();
        }
    }

    private void postResourceDeleted()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onResourceDeleted();
                }
            }
        } );
    }

    @Override
    public void onResourceRenamed(ResourceModel resourceModel)
    {
        postNewName();
    }

    @Override
    public void onResourceAdded(ResourceModel resourceModel)
    {
    }

    @Override
    public void onResourceTreeChanged()
    {
    }

    @Override
    public void onOpenResourceModel(ResourceModel resourceModel)
    {
    }

    @Override
    public void onResourceModelSelectedChannelsChanged(ResourceModel resourceModel)
    {
    }
}
