/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;

public class ResourcesModelContentProvider implements ITreeContentProvider
{
    private List<ResourcesFolder> folders = Collections.emptyList();

    public void setInput(List<ResourcesFolder> folders)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "folders", folders );
        this.folders = folders;
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return folders.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        Object[] children = null;
        if (parentElement instanceof ResourcesFolder)
        {
            children = ((ResourcesFolder)parentElement).getChildren().toArray();
        }
        else
        {
            children = Collections.emptyList().toArray();
        }
        return children;
    }

    @Override
    public Object getParent(Object element)
    {
        Object parent = null;
        if (element instanceof ResourceTreeNode)
        {
            parent = ((ResourceTreeNode)element).getParent();
        }
        return parent;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        boolean hasChildren = false;
        if (element instanceof ResourcesFolder)
        {
            hasChildren = ((ResourcesFolder)element).hasChildren();
        }
        return hasChildren;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

}
