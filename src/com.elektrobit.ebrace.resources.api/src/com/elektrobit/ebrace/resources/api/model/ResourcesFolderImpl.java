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

import java.util.ArrayList;
import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;

public class ResourcesFolderImpl extends BaseResourceTreeNode implements ResourcesFolder
{
    private final List<ResourceTreeNode> children;

    public ResourcesFolderImpl(String name)
    {
        this( name, null );
    }

    public ResourcesFolderImpl(String name, ResourcesFolder parent)
    {
        super( name, parent );
        children = new ArrayList<ResourceTreeNode>();
        setEditRight( EditRight.READ_ONLY );
    }

    @Override
    public List<ResourceTreeNode> getChildren()
    {
        return children;
    }

    @Override
    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    @Override
    public void addChild(ResourceTreeNode child)
    {
        children.add( 0, child );
    }

}
