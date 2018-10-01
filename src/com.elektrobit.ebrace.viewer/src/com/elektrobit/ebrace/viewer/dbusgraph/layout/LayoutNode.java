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

import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

import com.elektrobit.ebrace.viewer.dbusgraph.layout.LayoutNodeTools.LayoutNodeList;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * A Layout Node represents a node in the layout graph. Node in this terms does not necessarily mean Zest-Nodes because
 * this nodes are supposed to control the layout. So this object is a compound of Zest Nodes among other layout nodes
 * which may be used to control the layout further.
 */
class LayoutNode
{
    private LayoutNode m_ParentNode;
    private final InternalNode m_InternalNode;
    private final String m_CustomLabel;
    private final DisplayIndependentRectangle m_Rectangle;

    private final double m_RepulseStrength;
    private final LayoutNodeList m_ChildNodes;

    public LayoutNode(final InternalNode internalNode, double inRepusionStrength)
    {
        m_ParentNode = null;
        m_InternalNode = internalNode;
        m_CustomLabel = null;
        m_Rectangle = null;
        m_RepulseStrength = inRepusionStrength;
        m_ChildNodes = new LayoutNodeList();
    }

    public LayoutNode(final DisplayIndependentRectangle inRect, double inRepusionStrength)
    {
        m_ParentNode = null;
        m_InternalNode = null;
        m_CustomLabel = null;
        m_Rectangle = inRect;
        m_RepulseStrength = inRepusionStrength;
        m_ChildNodes = new LayoutNodeList();
    }

    public LayoutNode(final DisplayIndependentRectangle inRect, double inRepusionStrength, final String customLabel)
    {
        m_ParentNode = null;

        m_InternalNode = null;
        m_CustomLabel = customLabel;

        m_Rectangle = inRect;
        m_RepulseStrength = inRepusionStrength;

        m_ChildNodes = new LayoutNodeList();
    }

    public LayoutNode(String nodeName, double inRepusionStrength)
    {
        m_ParentNode = null;
        m_InternalNode = null;
        m_CustomLabel = nodeName;
        m_Rectangle = new DisplayIndependentRectangle( 0, 0, 10, 10 );
        m_RepulseStrength = inRepusionStrength;
        m_ChildNodes = new LayoutNodeList();
    }

    public double getRepulseStrength()
    {
        return m_RepulseStrength;
    }

    public InternalNode getInternalNode()
    {
        return m_InternalNode;
    }

    public GraphNode getGraphNode()
    {
        if (null != m_InternalNode)
        {
            return LayoutNodeTools.getGraphNodeForInternalNode( m_InternalNode );
        }
        return null;
    }

    public String getCustomLabel()
    {
        return m_CustomLabel;
    }

    LayoutNodeList getChildNodes()
    {
        return m_ChildNodes;
    }

    /**
     * Check if this node is a child node. This node is a child note if it has a parent node. All nodes which does not
     * have an parent node assigned are not treat as child nodes!
     * 
     * @return true if this node has a parent node.
     */
    boolean isChildNode()
    {
        return (m_ParentNode != null);
    }

    LayoutNodeList getSiblingNodes()
    {
        LayoutNodeList siblingList;

        // If this node is not a child - which is the case if it has no parent node then return an empty list
        if (m_ParentNode == null)
        {
            return new LayoutNodeList();
        }

        siblingList = (LayoutNodeList)m_ParentNode.m_ChildNodes.clone();
        siblingList.remove( this );
        return siblingList;
    }

    /**
     * Sets a new parent node to this child node.
     * 
     * @param inParentNode
     *            The parent node to be add to.
     * @see #setChildNode(LayoutNode)
     */
    public void setParentNode(final LayoutNode inParentNode)
    {
        m_ParentNode = inParentNode;
        if (inParentNode != null)
        {
            inParentNode.m_ChildNodes.add( this );
        }
    }

    /**
     * gets the parent node. If this node does not contain a parent node, which is the case if this node is not a child
     * node, then null will be returned.
     * 
     * @return The parent node of this node.
     */
    public LayoutNode getParentNode()
    {
        return m_ParentNode;
    }

    /**
     * Add a child node to this parent node. This node is assumed to be a parent node.
     * 
     * @param inChildNode
     *            The child node to add to this parent node.
     * @see #setParentNode(LayoutNode)
     */
    public void setChildNode(final LayoutNode inChildNode)
    {
        if (inChildNode != null)
        {
            inChildNode.setParentNode( this );
        }
    }

    /**
     * Get the ViewerGraph.ViewerNode for this LayoutNode.
     * 
     * @return the ViewerGraph.ViewerNode which belongs to this Node or NULL if this does not have a
     *         ViewerGraph.ViewerNode assigned to this node.
     */
    public TreeNode getGrapViewerNode()
    {
        return LayoutNodeTools.getGraphTreeNodeForInternalNode( m_InternalNode );
    }

    /**
     * Get the GraphNode for this LayoutNode.
     * 
     * @return the GraphNode which belongs to this Node or NULL if this does not have a GraphNode assigned to this node.
     */
    public GraphNode getGrapNode()
    {
        return LayoutNodeTools.getGraphNodeForInternalNode( m_InternalNode );
    }

    public InternalNode getParentInternalNode()
    {
        return LayoutNodeTools.getParentInternalNodeForInternalNode( m_InternalNode );
    }

    public boolean isHelperNode()
    {
        return (m_InternalNode == null);
    }

    public void setSize(double width, double height)
    {
        if (!isHelperNode())
        {
            m_InternalNode.setSizeInLayout( width, height );
        }
        else
        {
            m_Rectangle.width = width;
            m_Rectangle.height = height;
        }
    }

    public void setSize(final DisplayIndependentDimension size)
    {
        setSize( size.width, size.height );
    }

    public final DisplayIndependentPoint getPosition()
    {
        if (!isHelperNode())
        {
            return new DisplayIndependentPoint( m_InternalNode.getXInLayout(), m_InternalNode.getYInLayout() );
        }
        else
        {
            return new DisplayIndependentPoint( m_Rectangle.x, m_Rectangle.y );
        }
    }

    public final DisplayIndependentDimension getSize()
    {
        if (!isHelperNode())
        {
            return new DisplayIndependentDimension( m_InternalNode.getWidthInLayout(),
                                                    m_InternalNode.getHeightInLayout() );
        }
        else
        {
            return new DisplayIndependentDimension( m_Rectangle.width, m_Rectangle.height );
        }
    }

    public Rectangle getRectangle()
    {
        if (!isHelperNode())
        {
            return new Rectangle( m_InternalNode.getXInLayout(),
                                  m_InternalNode.getYInLayout(),
                                  m_InternalNode.getWidthInLayout(),
                                  m_InternalNode.getHeightInLayout() );
        }
        else
        {
            return new Rectangle( m_Rectangle.x, m_Rectangle.y, m_Rectangle.width, m_Rectangle.height );
        }
    }

    public void setRectangle(final Rectangle rect)
    {
        setPosition( rect.x, rect.y );
        setSize( rect.width, rect.height );
    }

    public void setPosition(double x, double y)
    {
        if (!isHelperNode())
        {
            m_InternalNode.setLocationInLayout( x, y );
        }
        else
        {
            m_Rectangle.x = x;
            m_Rectangle.y = y;
        }
    }

    public void setPosition(final DisplayIndependentPoint position)
    {
        setPosition( position.x, position.y );
    }

    public void adjustLayoutPos()
    {
        if (!isHelperNode())
        {
            final DisplayIndependentPoint currentPos = getPosition();
            m_InternalNode.setLocation( currentPos.x, currentPos.y );
        }
    }

    public DisplayIndependentPoint getCenterPosition()
    {
        final DisplayIndependentPoint currentPos = getPosition();
        final DisplayIndependentDimension currentDim = getSize();

        return new DisplayIndependentPoint( currentPos.x + 0.5 * currentDim.width,
                                            currentPos.y + 0.5 * currentDim.height );
    }

    /**
     * Check if this node geometrically collides with another node. Collision means if this nodes rectangle shape does
     * collide with that of another node.
     * 
     * @param inPeerNode
     *            The "other node" to check if it collides with this node.
     * @return true on collision.
     */
    public boolean collidesWith(LayoutNode inPeerNode)
    {
        return this.getRectangle().collidesWith( inPeerNode.getRectangle() );
    }

    public Vector getVectorToNode(final LayoutNode inNode)
    {
        DisplayIndependentPoint thisPos = getCenterPosition();
        DisplayIndependentPoint otherPos = inNode.getCenterPosition();

        return new Vector( otherPos.x - thisPos.x, otherPos.y - thisPos.y );
    }

    public double getDistanceFromNode(final LayoutNode inNode)
    {
        return getVectorToNode( inNode ).length();
    }

    public void moveBy(final Vector inVect)
    {
        final DisplayIndependentPoint currentPos = getPosition();
        setPosition( new DisplayIndependentPoint( currentPos.x + inVect.getX(), currentPos.y + inVect.getY() ) );
    }

    /**
     * Return a textual representation of this object.
     * 
     * @return The textual representation of this object.
     */
    @Override
    public final String toString()
    {
        final DisplayIndependentPoint currentPos = getPosition();
        final DisplayIndependentDimension currentSize = getSize();

        String label = m_CustomLabel;

        if (null != m_InternalNode)
        {
            final GraphNode graphNode = LayoutNodeTools.getGraphNodeForInternalNode( m_InternalNode );
            label = graphNode.getText();
        }

        String s = "Node (" + hashCode() + ") { label=" + label + ", x=" + currentPos.x + ", y=" + currentPos.y + ", w="
                + currentSize.width + ", h=" + currentSize.height + ", repulseStrength=" + m_RepulseStrength
                + ", childs[" + m_ChildNodes.size() + "]={";

        boolean first = true;
        for (LayoutNode childNode : m_ChildNodes)
        {
            if (!first)
            {
                s += ",";
            }
            s += childNode;
            first = false;
        }

        s += "}";
        return s + "}";

    }
}
