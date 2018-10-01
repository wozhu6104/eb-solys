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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class NodeBuilder
{
    private Map<String, RuntimeEventChannel<?>> channelMap;
    private final Map<String, ChannelTreeNode> nodeFullNameToNodeMap = new HashMap<>();
    private ChannelTreeNode rootNode;

    public ChannelTreeNode build(List<String> inputList)
    {
        rootNode = new ChannelTreeNode( "root", null, null );

        iterateListAndConvert( inputList, rootNode );

        return rootNode;
    }

    private void iterateListAndConvert(List<String> inputList, ChannelTreeNode rootNode)
    {
        for (String nodeFullName : inputList)
        {
            ChannelTreeNode parentNode = findParent( nodeFullName );
            String nodeName = createNodeName( parentNode, nodeFullName );
            createNode( nodeFullName, nodeName, parentNode );
        }
    }

    private ChannelTreeNode findParent(String nodeFullName)
    {
        String upperLevelPrefix = getUpperLevelPrefix( nodeFullName );
        ChannelTreeNode parent = nodeFullNameToNodeMap.get( upperLevelPrefix );
        return parent == null ? rootNode : parent;
    }

    private String getUpperLevelPrefix(String nodeFullName)
    {
        int lastIndexOfLevelSeparator = nodeFullName.lastIndexOf( StructureExpander.TREE_LEVEL_SEPARATOR );

        if (lastIndexOfLevelSeparator == -1)
        {
            return null;
        }
        else if (lastIndexOfLevelSeparator == nodeFullName.length() - 1)
        {
            return null;
        }
        else
        {
            return nodeFullName.substring( 0, lastIndexOfLevelSeparator );
        }
    }

    private String createNodeName(ChannelTreeNode parentNode, String fullName)
    {
        String result;

        String parentFullName = parentNode.getFullName();
        if (parentFullName == null)
        {
            result = fullName;
        }
        else
        {
            int index = parentFullName.length() + 1;
            result = fullName.substring( index );
        }

        return result;
    }

    private void createNode(String fullName, String nodeName, ChannelTreeNode parentNode)
    {
        ChannelTreeNode newNode = new ChannelTreeNode( nodeName, parentNode, fullName );
        parentNode.addChild( newNode );
        addChannelToNode( newNode, fullName );
        nodeFullNameToNodeMap.put( fullName, newNode );
    }

    private void addChannelToNode(ChannelTreeNode newNode, String fullName)
    {
        if (channelMap != null)
        {
            RuntimeEventChannel<?> channel = channelMap.get( fullName );
            newNode.setRuntimeEventChannel( channel );
        }
    }

    public void setChannelMap(Map<String, RuntimeEventChannel<?>> channelMap)
    {
        this.channelMap = channelMap;
    }
}
