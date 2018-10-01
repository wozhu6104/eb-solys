/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.allChannels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelToNodeConverter
{
    private final Map<String, RuntimeEventChannel<?>> channelMap = new HashMap<>();

    public ChannelTreeNode convert(List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        List<String> channelList = extractChannelNames( runtimeEventChannels );

        StructureExpander structureExpander = new StructureExpander();
        List<String> allNodes = structureExpander.createStructureWithAllSubgroups( channelList );

        NodeBuilder nodeBuilder = new NodeBuilder();
        nodeBuilder.setChannelMap( channelMap );
        ChannelTreeNode rootNode = nodeBuilder.build( allNodes );

        TreeCompactor treeCompactor = new TreeCompactor( rootNode );
        rootNode = treeCompactor.compact();

        return rootNode;
    }

    private List<String> extractChannelNames(List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        List<String> result = new ArrayList<>();
        for (RuntimeEventChannel<?> channel : runtimeEventChannels)
        {
            String channelName = channel.getName();
            result.add( channelName );
            channelMap.put( channelName, channel );
        }

        return result;
    }

}
