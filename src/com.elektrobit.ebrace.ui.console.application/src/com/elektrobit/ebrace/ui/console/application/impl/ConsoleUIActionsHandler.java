/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.application.impl;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;

public class ConsoleUIActionsHandler implements ResourceTreeNotifyCallback
{
    public ConsoleUIActionsHandler()
    {
        UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( this );
    }

    @Override
    public void onNewResourceTreeData(List<ResourcesFolder> folders)
    {
    }

    @Override
    public void openResource(ResourceModel resourceModel)
    {
        System.out.println( "WARN: Working with UI Resources in automation mode is not supported. Resource was: "
                + resourceModel.getName() );
    }

    @Override
    public void revealResource(ResourceModel resourceModel)
    {
    }
}
