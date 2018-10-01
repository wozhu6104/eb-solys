/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * The layout node tools are a collection of utility functions which deals with LayoutNodes.
 * 
 * @author pedu2501
 * 
 */
public abstract class LayoutNodeTools
{
    @SuppressWarnings("unchecked")
    public static void cleanupGroupNodes(Graph graphControl)
    {
        // Iterate through all internal nodes and remove all decoration notes.
        // A Decoration note is for instance a GraphNode of the kind EBGroupGraphNode
        ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
        graphNodes.addAll( graphControl.getNodes() );

        for (final GraphNode graphNodeNode : graphNodes)
        {
            if (graphNodeNode instanceof EBGroupGraphNode)
            {
                graphNodeNode.dispose();
            }
        }
    }

    /**
     * Dispose all decoration nodes from the Graph model. Decoration nodes are for example all nodes of the kind
     * EBGroupGraphNode This method finally returns an array of all remaining nodes.
     * 
     * @param entitiesToLayout
     *            the list of all nodes to layout to.
     * @return A list of all internal nodes which are no decoration nodes.
     */
    public static final InternalNode[] cleanupDecorationNodes(final InternalNode[] entitiesToLayout)
    {
        ArrayList<InternalNode> remainingIntNodes = new ArrayList<InternalNode>();

        // Iterate through all internal nodes and remove all decoration notes.
        // A Decoration note is for instance a GraphNode of the kind EBGroupGraphNode
        for (final InternalNode intNode : entitiesToLayout)
        {
            GraphNode thisGN = LayoutNodeTools.getGraphNodeForInternalNode( intNode );
            if (thisGN instanceof EBGroupGraphNode)
            {
                thisGN.dispose();
                continue;
            }
            remainingIntNodes.add( intNode );
        }

        return remainingIntNodes.toArray( new InternalNode[remainingIntNodes.size()] );
    }

    final static class LayoutConnectionList extends ArrayList<LayoutConnection>
    {
        private static final long serialVersionUID = 1L;

        public LayoutConnectionList()
        {
            super();
        }

        public LayoutNodeList getPeerNodesConnectedWithNode(final LayoutNode thisNode)
        {
            LayoutNodeList nodeList = new LayoutNodeList();

            for (LayoutConnection conn : this)
            {
                final LayoutNode peerNode = (conn.nodeA() == thisNode) ? conn.nodeB() : conn.nodeA();
                nodeList.add( peerNode );
            }
            return nodeList;
        }
    }

    final static class LayoutNodeList extends ArrayList<LayoutNode>
    {
        private static final long serialVersionUID = 1L;

        public LayoutNodeList()
        {
            super();
        }
    }

    final static class ConnectionMap extends HashMap<LayoutNode, LayoutConnectionList>
    {
        private static final long serialVersionUID = 1L;

        public ConnectionMap()
        {
            super();
        }
    }

    public static Integer getGroupId(final InternalNode intNode)
    {
        final GraphNode graphNode = LayoutNodeTools.getGraphNodeForInternalNode( intNode );
        int style = graphNode.getNodeStyle();

        if ((style & 0x40000000) == 0)
        {
            return null;
        }
        return new Integer( style & ~0x40000000 );
    }

    /**
     * Gets the GraphNode from a InternalNode. This method returns null if this internal node represents not a
     * GraphNode.
     * 
     * @param intNode
     *            The internal node to gather the Graph node from.
     * @return the GraphNode object which belongs to this internal node.
     * @see #getGraphTreeNodeForInternalNode(InternalNode)
     */
    public static GraphNode getGraphNodeForInternalNode(final InternalNode intNode)
    {
        final LayoutEntity intLayoutEntity = intNode.getLayoutEntity();
        if (intLayoutEntity instanceof InternalNode)
        {
            final InternalNode intEntity = (InternalNode)intLayoutEntity;
            if (null != intEntity)
            {
                final LayoutEntity layoutEntity = intEntity.getLayoutEntity();
                if (null != layoutEntity)
                {
                    Object graphNode = layoutEntity.getGraphData();
                    if (graphNode instanceof GraphNode)
                    {
                        return (GraphNode)graphNode;
                    }
                }
            }
        }
        else
        {
            final LayoutEntity layoutEntity = intNode.getLayoutEntity();
            if (null != layoutEntity)
            {
                Object graphNode = layoutEntity.getGraphData();
                if (graphNode instanceof GraphNode)
                {
                    return (GraphNode)graphNode;
                }
            }
        }
        return null;
    }

    public static TreeNode getParentNodeOfType(TreeNode childNode, String type)
    {
        // Dive hierarchical up and try to Found the parent node for the given type.
        for (;;)
        {
            if (childNode == null || childNode.getTreeLevel().getName().equals( type ))
            {
                return childNode;
            }
            childNode = childNode.getParent(); // get the parent
        }
    }

    /**
     * Gets the TreeNode from a GraphNode object. This method returns null if this internal node represents not a
     * TreeNode.
     * 
     * @param graphNode
     *            The graphNode node to gather the TreeNode from.
     * @return the TreeNode object which belongs to this internal node or null if this graphNode has no internalNode.
     * @see #getGraphNodeForInternalNode(InternalNode)
     * @see #getGraphTreeNodeForInternalNode(InternalNode)
     */
    public static TreeNode getGraphTreeNodeForGraphNode(final GraphNode graphNode)
    {
        if (null != graphNode)
        {
            Object o = graphNode.getData();
            return (o instanceof TreeNode) ? (TreeNode)o : null;
        }
        return null;
    }

    /**
     * Gets the TreeNode from a InternalNode. This method returns null if this internal node represents not a TreeNode.
     * 
     * @param intNode
     *            The internal node to gather the TreeNode from.
     * @return the TreeNode object which belongs to this internal node.
     * @see #getGraphTreeNodeForGraphNode(GraphNode)
     * @see #getGraphTreeNodeForInternalNode(InternalNode)
     */
    public static TreeNode getGraphTreeNodeForInternalNode(final InternalNode intNode)
    {
        if (null != intNode)
        {
            GraphNode graphNode = getGraphNodeForInternalNode( intNode );
            return getGraphTreeNodeForGraphNode( graphNode );
        }
        return null;
    }

    /**
     * Get the Graph for a InternalNode object.
     * 
     * @param intNode
     *            The internal node to search for its Graph for
     * @return The graph which owns the given InternalNode object or null if there is no Graph for this.
     * @see #getGraphNodeForInternalNode(InternalNode)
     * @see #getGraphTreeNodeForGraphNode(GraphNode)
     */
    public static Graph getGraphForInternalNode(final InternalNode intNode)
    {
        if (null != intNode)
        {
            GraphNode gn = getGraphNodeForInternalNode( intNode );
            if (gn != null)
            {
                Object o = gn.getGraphModel();
                if (o.getClass() == Graph.class)
                {
                    return (Graph)o;
                }
            }
        }
        return null;
    }

    /**
     * Get the internal node which belongs to a TreeNode object.
     * 
     * @param graph
     *            the graph to search the internal node for.
     * @param gvn
     *            the TreeNode object to gets its internal node object.
     * @return the internal node for the graph viewer node or null if there is no.
     */
    public static InternalNode searchInternalNodesGraphForTreeNode(final Graph graph, final TreeNode gvn)
    {
        if (graph != null && gvn != null)
        {
            // Get all Nodes
            @SuppressWarnings("unchecked")
            final List<GraphNode> graphNodes = graph.getNodes();
            for (GraphNode gn : graphNodes)
            {
                final Object data = gn.getData();
                if (data instanceof TreeNode)
                {
                    final TreeNode thisGVN = (TreeNode)data;

                    if (thisGVN.equals( gvn ))
                    {
                        final LayoutEntity le = gn.getLayoutEntity();
                        final Object layoutInfo = le.getLayoutInformation();
                        if (layoutInfo instanceof InternalNode)
                        {
                            return (InternalNode)layoutInfo;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get all parent internal nodes for a given internal nodes.
     * 
     * @param intNode
     *            The internal node to query its parent node.
     * @return The parent internal node or null if there is no parent internal node for this.
     */
    public static InternalNode getParentInternalNodeForInternalNode(final InternalNode intNode)
    {
        TreeNode thisGVN = getGraphTreeNodeForInternalNode( intNode );
        if (thisGVN != null)
        {
            TreeNode parentGVN = thisGVN.getParent();

            if (parentGVN != null)
            {
                // get the Internal node for this parentGVN...
                final Graph graph = getGraphForInternalNode( intNode );
                return searchInternalNodesGraphForTreeNode( graph, parentGVN );
            }
        }
        return null;
    }

}
