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
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class TreeCompactor
{

    private final ChannelTreeNode rootNode;

    public TreeCompactor(ChannelTreeNode rootNode)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "rootNode", rootNode );
        this.rootNode = rootNode;
    }

    public ChannelTreeNode compact()
    {
        replaceGroupThatHasOnlyOneGroupAsChild( rootNode );
        return rootNode;
    }

    private void replaceGroupThatHasOnlyOneGroupAsChild(ChannelTreeNode startNode)
    {
        List<ChannelTreeNode> childrenCopy = new ArrayList<>( startNode.getChildren() );
        for (ChannelTreeNode node : childrenCopy)
        {
            node = groupThisNode( node );

            if (!node.isLeaf())
            {
                replaceGroupThatHasOnlyOneGroupAsChild( node );
            }
        }
    }

    private ChannelTreeNode groupThisNode(ChannelTreeNode node)
    {
        if (node.getChildCount() == 1)
        {
            ChannelTreeNode onlyChild = node.getChildAt( 0 );
            node = joinParentGroupAndChildGroup( node, onlyChild );
            node = groupThisNode( node );
        }
        return node;
    }

    private ChannelTreeNode joinParentGroupAndChildGroup(ChannelTreeNode node, ChannelTreeNode onlyChild)
    {
        String fullJoinedName = node.getFullName() + StructureExpander.TREE_LEVEL_SEPARATOR + onlyChild.getNodeName();
        String shortJoinedName = node.getNodeName() + StructureExpander.TREE_LEVEL_SEPARATOR + onlyChild.getNodeName();
        ChannelTreeNode joinedNode = new ChannelTreeNode( shortJoinedName, node.getParent(), fullJoinedName );
        joinedNode.addChildren( onlyChild.getChildren() );
        onlyChild.getChildren().forEach( (ChannelTreeNode child) -> child.setParent( joinedNode ) );
        joinedNode.setRuntimeEventChannel( onlyChild.getRuntimeEventChannel() );

        node.getParent().replaceChild( joinedNode, node );
        return joinedNode;
    }
}
