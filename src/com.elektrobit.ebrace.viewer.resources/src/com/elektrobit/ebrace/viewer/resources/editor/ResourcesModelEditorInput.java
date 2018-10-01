/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;

public class ResourcesModelEditorInput implements IEditorInput
{
    ResourceModel rModel;

    public ResourcesModelEditorInput(ResourceModel model)
    {
        this.rModel = model;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter)
    {
        return null;
    }

    @Override
    public boolean exists()
    {
        return false;
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.rModel.getName();
    }

    @Override
    public IPersistableElement getPersistable()
    {
        return null;
    }

    @Override
    public String getToolTipText()
    {
        return this.rModel.getName();
    }

    public ResourceModel getModel()
    {
        return this.rModel;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rModel == null) ? 0 : rModel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ResourcesModelEditorInput other = (ResourcesModelEditorInput)obj;
        if (rModel == null)
        {
            if (other.rModel != null)
            {
                return false;
            }
        }
        else if (!rModel.equals( other.rModel ))
        {
            return false;
        }
        return true;
    }
}
