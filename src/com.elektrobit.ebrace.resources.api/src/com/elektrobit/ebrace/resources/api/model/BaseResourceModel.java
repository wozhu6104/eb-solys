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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public abstract class BaseResourceModel extends BaseResourceTreeNode implements ResourceModel
{
    private LinkedHashSet<RuntimeEventChannel<?>> allChannels = new LinkedHashSet<RuntimeEventChannel<?>>();
    private LinkedHashSet<RuntimeEventChannel<?>> disabledChannels = new LinkedHashSet<RuntimeEventChannel<?>>();
    private LinkedHashSet<RuntimeEventChannel<?>> selectedChannels = new LinkedHashSet<RuntimeEventChannel<?>>();
    private final ResourceChangedNotifier resourceChangedNotifier;

    public BaseResourceModel(String initialName, ResourcesFolder parent, EditRight editRight,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( initialName, parent );
        setEditRight( editRight );
        this.resourceChangedNotifier = resourceChangedNotifier;
    }

    @Override
    public void setName(String name)
    {
        super.setName( name );
        resourceChangedNotifier.notifyResourceRenamed( this );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        String name = getName();
        ResourceTreeNode parent = getParent();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
        BaseResourceModel other = (BaseResourceModel)obj;
        String name = getName();
        ResourceTreeNode parent = getParent();
        if (name == null)
        {
            if (other.getName() != null)
            {
                return false;
            }
        }
        else if (!name.equals( other.getName() ))
        {
            return false;
        }
        if (parent == null)
        {
            if (other.getParent() != null)
            {
                return false;
            }
        }
        else if (!parent.equals( other.getParent() ))
        {
            return false;
        }
        return true;
    }

    @Override
    public void setChannels(List<RuntimeEventChannel<?>> channels)
    {
        this.allChannels = new LinkedHashSet<RuntimeEventChannel<?>>( channels );
        removeDeletedChannelsFromSet( disabledChannels );
        removeDeletedChannelsFromSet( selectedChannels );
        resourceChangedNotifier.notifyResourceChannelsChanged( this );
    }

    private void removeDeletedChannelsFromSet(Set<RuntimeEventChannel<?>> channelsToUpdate)
    {
        channelsToUpdate.retainAll( allChannels );
    }

    @Override
    public List<RuntimeEventChannel<?>> getChannels()
    {
        return new ArrayList<RuntimeEventChannel<?>>( allChannels );
    }

    @Override
    public void setDisabledChannels(List<RuntimeEventChannel<?>> disabledChannels)
    {
        this.disabledChannels = new LinkedHashSet<RuntimeEventChannel<?>>( disabledChannels );
        resourceChangedNotifier.notifyResourceChannelsChanged( this );
    }

    @Override
    public List<RuntimeEventChannel<?>> getDisabledChannels()
    {
        return new ArrayList<RuntimeEventChannel<?>>( disabledChannels );
    }

    @Override
    public List<RuntimeEventChannel<?>> getEnabledChannels()
    {
        List<RuntimeEventChannel<?>> allChannelsCopy = new ArrayList<RuntimeEventChannel<?>>( allChannels );
        allChannelsCopy.removeAll( disabledChannels );
        return new ArrayList<RuntimeEventChannel<?>>( allChannelsCopy );
    }

    @Override
    public void setSelectedChannels(List<RuntimeEventChannel<?>> selectedChannels)
    {
        LinkedHashSet<RuntimeEventChannel<?>> newChannelSet = new LinkedHashSet<RuntimeEventChannel<?>>( selectedChannels );
        boolean notify = !newChannelSet.equals( this.selectedChannels );
        this.selectedChannels = newChannelSet;
        if (notify)
        {
            resourceChangedNotifier.notifySelectedChannelsChanged( this );
        }
    }

    @Override
    public List<RuntimeEventChannel<?>> getSelectedChannels()
    {
        return new ArrayList<RuntimeEventChannel<?>>( selectedChannels );
    }

    protected void notifyResourceStateChanged()
    {
        resourceChangedNotifier.notifyResourceStateChanged( this );
    }
}
