/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.api.model;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;

public abstract class BaseResourceTreeNode implements ResourceTreeNode
{
    private String name;
    private final ResourcesFolder parent;
    private EditRight editRight;

    public BaseResourceTreeNode(String name, ResourcesFolder parent)
    {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public ResourcesFolder getParent()
    {
        return parent;
    }

    @Override
    public EditRight getEditRight()
    {
        return editRight;
    }

    @Override
    public void setEditRight(EditRight editRight)
    {
        this.editRight = editRight;
    }

}
