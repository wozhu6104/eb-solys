/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.createResource;

import java.util.List;
import java.util.Set;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.createresource.DefaultResourceNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class DefaultResourceNameNotifyUseCaseImpl implements DefaultResourceNameNotifyUseCase
{
    private static final String CONNECTION_DEFAULT_PREFIX_NAME = "Target ";
    private final ResourcesModelManager resourcesModelManager;

    public DefaultResourceNameNotifyUseCaseImpl(ResourcesModelManager resourcesModelManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "resourcesModelManager", resourcesModelManager );
        this.resourcesModelManager = resourcesModelManager;
    }

    @Override
    public String getNextPossibleConnectionName()
    {
        List<ResourceModel> connections = resourcesModelManager.getConnections();

        int nameIndex = 1;
        String possibleConnectionName;
        do
        {
            possibleConnectionName = CONNECTION_DEFAULT_PREFIX_NAME + nameIndex;
            nameIndex++;
        }
        while (modelsContainName( connections, possibleConnectionName ));

        return possibleConnectionName;
    }

    @Override
    public Set<String> getUsedConnectionNames()
    {
        Set<String> result = resourcesModelManager.getUsedConnectionNames();
        return result;
    }

    private boolean modelsContainName(List<ResourceModel> models, String name)
    {
        for (ResourceModel model : models)
        {
            if (model.getName().equals( name ))
            {
                return true;
            }
        }
        return false;
    }
}
