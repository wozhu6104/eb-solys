/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.channels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class NodeFilter
{
    private String searchTerm = null;
    private final Set<ChannelTreeNode> nodesToDetach = new HashSet<>();

    public NodeFilter(String searchTerm)
    {
        if (searchTerm != null)
        {
            this.searchTerm = searchTerm.toLowerCase();
        }
    }

    public void filter(ChannelTreeNode rootNode)
    {
        if (searchTerm != null)
        {
            traverseAndFilter( rootNode );
            detachRememberedNodes( rootNode );
        }
    }

    public boolean isFilteringRequired(String searchTerm)
    {
        return searchTerm != null && !searchTerm.equals( "" );
    }

    public boolean match(String name)
    {
        return name.toLowerCase().contains( searchTerm );
    }

    private boolean traverseAndFilter(ChannelTreeNode node)
    {
        String nodeSearchName = getSearchName( node );
        boolean matches = match( nodeSearchName );

        if (node.isLeaf())
        {
            return matches;
        }

        if (matches)
        {
            return true;
        }

        boolean result = false;
        for (ChannelTreeNode child : node.getChildren())
        {
            boolean matchesSubNode = traverseAndFilter( child );
            if (!matchesSubNode)
            {
                detachLater( child );
            }
            else
            {
                result = true;
            }
        }

        return result;
    }

    private String getSearchName(ChannelTreeNode node)
    {
        String result;
        RuntimeEventChannel<?> channel = node.getRuntimeEventChannel();
        if (channel != null)
        {
            result = getSearchName( channel );
        }
        else
        {
            result = node.getNodeName();
        }

        return result;
    }

    private String getSearchName(RuntimeEventChannel<?> channel)
    {
        String searchName = "";
        String channelName = channel.getName();
        searchName = channelName + " " + getChannelTypeLabel( channel );

        return searchName;
    }

    private String getChannelTypeLabel(RuntimeEventChannel<?> runtimeEventChannel)
    {
        Unit<?> unit = runtimeEventChannel.getUnit();
        if (unit != null)
        {
            String channelType = unit.getName();
            return "[" + channelType + "]";
        }

        return "";
    }

    private void detachLater(ChannelTreeNode node)
    {
        nodesToDetach.add( node );
    }

    private void detachRememberedNodes(ChannelTreeNode rootNode)
    {
        traverseTreeAndRemoveNodes( rootNode );
    }

    private void traverseTreeAndRemoveNodes(ChannelTreeNode node)
    {
        List<ChannelTreeNode> childrenCopy = new ArrayList<ChannelTreeNode>( node.getChildren() );
        for (ChannelTreeNode child : childrenCopy)
        {
            if (nodesToDetach.contains( child ))
            {
                child.getParent().remove( child );
            }
            else
            {
                traverseTreeAndRemoveNodes( child );
            }
        }
    }
}
