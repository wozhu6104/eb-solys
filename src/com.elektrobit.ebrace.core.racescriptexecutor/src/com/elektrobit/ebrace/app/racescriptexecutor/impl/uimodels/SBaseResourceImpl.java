/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.script.external.SBaseResource;

abstract class SBaseResourceImpl<M extends ResourceModel, S extends SBaseResource<S>> implements SBaseResource<S>
{
    private final M viewModel;
    private final ResourcesModelManager resourcesModelManager;
    private final S sModel;

    public SBaseResourceImpl(M viewModel, ResourcesModelManager resourcesModelManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "resource model", viewModel );
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        this.viewModel = viewModel;
        sModel = getThis();
        RangeCheckUtils.assertReferenceParameterNotNull( "sModel", sModel );
        this.resourcesModelManager = resourcesModelManager;
    }

    protected abstract S getThis();

    protected abstract boolean canChannelsBeAddedToView(List<RuntimeEventChannel<?>> channels);

    @Override
    public S setName(String newName)
    {
        viewModel.setName( newName );
        return sModel;
    }

    @Override
    public S add(RuntimeEventChannel<?> channel)
    {
        add( Arrays.asList( channel ) );
        return sModel;
    }

    @Override
    public S add(List<RuntimeEventChannel<?>> channels)
    {
        if (canChannelsBeAddedToView( channels ))
        {
            List<RuntimeEventChannel<?>> channelsOfChart = viewModel.getChannels();
            channelsOfChart.addAll( channels );
            viewModel.setChannels( channelsOfChart );
        }

        return sModel;
    }

    @Override
    public S remove(RuntimeEventChannel<?> channel)
    {
        remove( Arrays.asList( new RuntimeEventChannel<?>[]{channel} ) );
        return sModel;
    }

    @Override
    public S remove(List<RuntimeEventChannel<?>> channels)
    {
        List<RuntimeEventChannel<?>> channelsOfChart = viewModel.getChannels();
        channelsOfChart.removeAll( channels );
        viewModel.setChannels( channelsOfChart );
        return sModel;
    }

    @Override
    public S clear()
    {
        viewModel.setChannels( Collections.<RuntimeEventChannel<?>> emptyList() );
        return sModel;
    }

    @Override
    public void delete()
    {
        List<ResourceModel> chartInList = new ArrayList<ResourceModel>();
        chartInList.add( viewModel );
        resourcesModelManager.deleteResourcesModels( chartInList );
    }
}
