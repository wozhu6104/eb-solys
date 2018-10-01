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

import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * The Layout connection represents the relation of two nodes. Node in this terms does not necessarily mean Zest-Nodes
 * because this nodes are supposed to control the layout. So this object is a compound of Zest Nodes among other layout
 * nodes which may be used to control the layout further.
 */
class LayoutConnection
{
    private final LayoutNode m_NodeA;
    private final LayoutNode m_NodeB;
    private double m_AttractStrength;

    /**
     * Gets the GraphConnection object which belongs to this InternalRelationship. If there is no GraphConnection for
     * this internal connection then this call returns null.
     * 
     * @param inRelationship
     * @return the Graph connection for this internal relationship or null if there is no GraphNode.
     * @see #getGraphNodeForInternalNode(InternalNode)
     */
    public static GraphConnection getGraphConnectionForInternalRelationship(final InternalRelationship inRelationship)
    {
        LayoutRelationship layoutRelationship = inRelationship.getLayoutRelationship();
        if (null != layoutRelationship && layoutRelationship instanceof InternalRelationship)
        {
            if (null != layoutRelationship && layoutRelationship instanceof InternalRelationship)
            {
                InternalRelationship intRelationship = (InternalRelationship)layoutRelationship;
                LayoutRelationship graphLayoutRelationship = intRelationship.getLayoutRelationship();
                if (null != graphLayoutRelationship)
                {
                    Object layoutItem = graphLayoutRelationship.getGraphData();
                    if (null != layoutItem && layoutItem instanceof GraphConnection)
                    {
                        return (GraphConnection)layoutItem;
                    }
                }
            }
        }
        else
        {
            LayoutRelationship graphLayoutRelationship = inRelationship.getLayoutRelationship();
            if (null != graphLayoutRelationship)
            {
                Object layoutItem = graphLayoutRelationship.getGraphData();
                if (null != layoutItem && layoutItem instanceof GraphConnection)
                {
                    return (GraphConnection)layoutItem;
                }
            }
        }
        return null;
    }

    public LayoutConnection(LayoutNode nodeA, LayoutNode nodeB, double attractStrength)
    {
        m_NodeA = nodeA;
        m_NodeB = nodeB;
        m_AttractStrength = attractStrength;
    }

    public LayoutConnection(LayoutNode nodeA, LayoutNode nodeB)
    {
        this( nodeA, nodeB, 1.f );
    }

    public LayoutNode nodeA()
    {
        return m_NodeA;
    }

    public LayoutNode nodeB()
    {
        return m_NodeB;
    }

    public void setAttractionStrength(float attractStrength)
    {
        m_AttractStrength = attractStrength;
    }

    public double getAttractStrength()
    {
        return m_AttractStrength;
    }

    @Override
    public String toString()
    {
        return "Connection(" + hashCode() + ")={" + "nodeA=" + m_NodeA + ", nodeB=" + m_NodeB + ", attractStrength="
                + "" + m_AttractStrength + "}";
    }

}
