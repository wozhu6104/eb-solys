/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.channels.NodeFilter;
import com.elektrobit.ebrace.viewer.channelsview.ChannelsView;
import com.elektrobit.ebrace.viewer.common.constants.ViewIDs;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

import lombok.extern.log4j.Log4j;

@Log4j
public class ChannelsViewHandlerUtil
{
    public static List<RuntimeEventChannel<?>> filterChannelsInNodes(List<Object> input)
    {
        List<ChannelTreeNode> selectedNodes = filterOtherObjects( input );
        List<RuntimeEventChannel<?>> allSubChannels = getAllSubChannels( selectedNodes );
        return filterChannels( allSubChannels );
    }

    private static List<ChannelTreeNode> filterOtherObjects(List<Object> input)
    {
        List<ChannelTreeNode> treeNodes = new ArrayList<>();
        for (Object inputObject : input)
        {
            if (inputObject instanceof ChannelTreeNode)
            {
                treeNodes.add( (ChannelTreeNode)inputObject );
            }
            else
            {
                log.warn( "Object is not instance of " + ChannelTreeNode.class.getSimpleName() + " :"
                        + inputObject.getClass().getName() );
            }
        }
        return treeNodes;
    }

    public static List<RuntimeEventChannel<?>> filterChannels(List<RuntimeEventChannel<?>> allSubChannels)
    {
        List<RuntimeEventChannel<?>> result;

        ChannelsView channelsView = getChannelsView();
        String searchTerm = channelsView.getSearchTerm();
        NodeFilter nodeFilter = new NodeFilter( searchTerm );
        if (nodeFilter.isFilteringRequired( searchTerm ))
        {
            result = new ArrayList<RuntimeEventChannel<?>>();
            for (RuntimeEventChannel<?> runtimeEventChannel : allSubChannels)
            {
                String name = runtimeEventChannel.getName();
                if (nodeFilter.match( name ))
                {
                    result.add( runtimeEventChannel );
                }
            }
        }
        else
        {
            result = allSubChannels;
        }
        return result;
    }

    public static List<RuntimeEventChannel<?>> getChannelListFromSelection(ISelection selectedChannels)
    {
        @SuppressWarnings("unchecked")
        List<ChannelTreeNode> listSelectedChannels = ((StructuredSelection)selectedChannels).toList();
        LinkedHashSet<RuntimeEventChannel<?>> channelsListSet = new LinkedHashSet<RuntimeEventChannel<?>>();

        for (ChannelTreeNode node : listSelectedChannels)
        {
            if (node.isLeaf())
            {
                channelsListSet.add( node.getRuntimeEventChannel() );
            }
            else
            {
                List<RuntimeEventChannel<?>> channelsInSubTree = getAllSubChannels( node );
                channelsListSet.addAll( channelsInSubTree );
            }
        }
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>( channelsListSet );
        return channels;
    }

    private static List<RuntimeEventChannel<?>> getAllSubChannels(List<ChannelTreeNode> selectedNodes)
    {
        List<RuntimeEventChannel<?>> listOfChannels = new ArrayList<>();
        for (ChannelTreeNode selectedNode : selectedNodes)
        {
            traverseAllSubNodes( listOfChannels, selectedNode );
        }

        return listOfChannels;
    }

    private static List<RuntimeEventChannel<?>> getAllSubChannels(ChannelTreeNode node)
    {
        List<ChannelTreeNode> nodeAsList = Arrays.asList( node );
        return getAllSubChannels( nodeAsList );
    }

    private static void traverseAllSubNodes(List<RuntimeEventChannel<?>> list, ChannelTreeNode node)
    {
        if (node.isLeaf())
        {
            list.add( node.getRuntimeEventChannel() );
            return;
        }

        for (ChannelTreeNode child : node.getChildren())
        {
            traverseAllSubNodes( list, child );
        }
    }

    private static ChannelsView getChannelsView()
    {
        IViewPart channelsView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .findView( ViewIDs.CHANNELS_VIEW_ID );
        return (ChannelsView)channelsView;
    }

}
