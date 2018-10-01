/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.model;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.dependencygraph.DependencyGraphModel;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

public class DependencyGraphModelImpl extends BaseResourceModel implements DependencyGraphModel
{
    public static final String DEFAULT_MODEL_NAME = "Communication";

    public DependencyGraphModelImpl(ResourcesFolder parentFolder, ResourceChangedNotifier resourceChangedNotifier)
    {
        super( DEFAULT_MODEL_NAME, parentFolder, EditRight.READ_ONLY, resourceChangedNotifier );
    }
}
