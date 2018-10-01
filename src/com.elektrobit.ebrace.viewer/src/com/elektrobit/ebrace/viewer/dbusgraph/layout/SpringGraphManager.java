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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import com.elektrobit.ebrace.viewer.dbusgraph.layout.LayoutNodeTools.ConnectionMap;
import com.elektrobit.ebrace.viewer.dbusgraph.layout.LayoutNodeTools.LayoutConnectionList;
import com.elektrobit.ebrace.viewer.dbusgraph.layout.LayoutNodeTools.LayoutNodeList;

/**
 * The SpringGraphManager has several tasks:
 * <ul>
 * <li>It manages all nodes which belongs to a Spring Layout.</li>
 * <li>It manages all node relations - the connections - for the Spring Layout.</li>
 * <li>It calculates the positions of the nodes.</li>
 * <li>Provides convenient functions as to query an InternalNode for its GraphNode.</li>
 * <li>Gives some informations about the actual graph (e.g. its bounds).</li>
 * </ul>
 */
public class SpringGraphManager
{
    /** The ZEST graph model */
    private Graph m_Graph;

    private final Vector m_GroupParentNodeInset = new Vector( -70, -30 );

    /** Stores the helper node. This is a virtual node is actually supposed to pull all graph nodes together. */
    public LayoutNode m_HelperNode;

    /**
     * This Map stores all Layout connections for a given Layout node and is used as a cache.
     */
    private final ConnectionMap m_ConnectionMap;

    /**
     * This container stores all nodes
     */
    private final LayoutNodeList m_Nodes;

    /**
     * This container stores all connections
     */
    private final LayoutConnectionList m_Connections;

    /**
     * Map to set a relation from a InternalNode to its LayoutNode instance. This is used as a cache.
     */
    private final HashMap<InternalNode, LayoutNode> m_ZestNodeMap;

    /**
     * Map to set a relation from a InternalRelationship to its LayoutConnection instance. This is used as a cache.
     */
    private final HashMap<InternalRelationship, LayoutConnection> m_ZestConnectionMap;

    /**
     * Creates a new empty manager which initialize all internal members.
     */
    public SpringGraphManager()
    {
        m_Graph = null;
        m_ConnectionMap = new ConnectionMap();
        m_Nodes = new LayoutNodeList();
        m_Connections = new LayoutConnectionList();

        m_ZestNodeMap = new HashMap<InternalNode, LayoutNode>();
        m_ZestConnectionMap = new HashMap<InternalRelationship, LayoutConnection>();
        m_HelperNode = null;
    }

    /**
     * Add a Internal Node to this manager. This method creates an internal Node object for rendering. Either Node as
     * Connection objects are not visible outside and are supposed for rendering only!
     * 
     * @param intNode
     *            The internal node to add to.
     * @see #addConnection(InternalRelationship)
     */
    public LayoutNode addNode(final InternalNode intNode)
    {
        GraphNode graphNode = LayoutNodeTools.getGraphNodeForInternalNode( intNode );
        if (null != graphNode)
        {
            if (m_Graph == null)
            {
                m_Graph = graphNode.getGraphModel();
            }

            final double nodeRepulseStrength = 28;

            final LayoutNode node = new LayoutNode( intNode, nodeRepulseStrength );
            m_Nodes.add( node );
            m_ZestNodeMap.put( intNode, node );
            return node;
        }
        return null; // no node added
    }

    public LayoutNode addParentNode(String nodeName)
    {
        final double nodeRepulseStrength = 28.0;

        final LayoutNode node = new LayoutNode( nodeName, nodeRepulseStrength );
        m_Nodes.add( node );
        return node;
    }

    /**
     * Add a Internal Relationship which actually describes the connection of two Internal Nodes to this manager. This
     * method creates an internal Connection object for rendering. This Connection Object deals with Node objects.
     * Either Node as Connection objects are not visible outside and are supposed for rendering only!
     * 
     * @param intRelationship
     *            The internal node to add to.
     * @see #addNode(InternalNode)
     */
    public void addConnection(final InternalRelationship intRelationship)
    {
        GraphConnection graphConnection = LayoutConnection.getGraphConnectionForInternalRelationship( intRelationship );
        if (null != graphConnection)
        {
            final InternalNode sourceIntNode = intRelationship.getSource();
            final InternalNode destIntNode = intRelationship.getDestination();

            final LayoutNode sourceNode = m_ZestNodeMap.get( sourceIntNode );
            final LayoutNode destNode = m_ZestNodeMap.get( destIntNode );

            LayoutConnection connection = new LayoutConnection( sourceNode, destNode, 0.5 );
            m_Connections.add( connection );
            m_ZestConnectionMap.put( intRelationship, connection );
        }
    }

    public void addGroupGraphNodes()
    {
        final Vector insetRect = new Vector( -m_GroupParentNodeInset.getX() / 2, 0 );
        final LayoutNodeList parentNodes = getParentNodes();

        for (LayoutNode parentNode : parentNodes)
        {
            parentNode.setRectangle( parentNode.getRectangle().getInsetRect( insetRect ) );
            Rectangle r = parentNode.getRectangle();

            EBGroupGraphNode gn = new EBGroupGraphNode( m_Graph,
                                                        new DisplayIndependentRectangle( r.x, r.y, r.width, r.height ),
                                                        parentNode.getCustomLabel() );

            final LayoutNodeList childNodes = parentNode.getChildNodes();
            for (LayoutNode childNode : childNodes)
            {
                GraphNode childGraphNode = childNode.getGraphNode();
                if (null != childGraphNode)
                {
                    gn.addChildNode( childGraphNode );
                }
            }
        }
    }

    /**
     * Gets a list of all Layout Nodes.
     * 
     * @return The list of all Layout Node objects.
     * @see #addNode(InternalNode)
     */
    public final LayoutNodeList getNodes()
    {
        return m_Nodes;
    }

    /**
     * Gets a list of all Layout Connections.
     * 
     * @return The list of all Layout Connection objects.
     * @see #addConnection(InternalRelationship)
     */
    public final LayoutConnectionList getConnections()
    {
        return m_Connections;
    }

    /**
     * Returns the current count of Layout Node objects.
     * 
     * @return The number of nodes managed by this object.
     */
    public int getNodeCount()
    {
        return m_Nodes.size();
    }

    public Rectangle getBoundsForParentNode(final LayoutNode inParentNode)
    {
        final LayoutNodeList childNodes = inParentNode.getChildNodes();

        if (null == childNodes)
        {
            return new Rectangle( 0, 0, 0, 0 );
        }

        double xMin = 1e10f;
        double xMax = -1e10f;

        double yMin = 1e10f;
        double yMax = -1e10f;

        for (final LayoutNode thisNode : childNodes)
        {
            final DisplayIndependentPoint nodePos = thisNode.getPosition();
            final DisplayIndependentDimension nodeSize = thisNode.getSize();

            final double nodeRight = nodePos.x + nodeSize.width;
            final double nodeTop = nodePos.y + nodeSize.height;

            xMin = Math.min( xMin, nodePos.x );
            xMax = Math.max( xMax, nodeRight );

            yMin = Math.min( yMin, nodePos.y );
            yMax = Math.max( yMax, nodeTop );
        }
        return new Rectangle( xMin, yMin, xMax - xMin, yMax - yMin );
    }

    /**
     * Get the bounding box for all nodes
     * 
     * @return The bounding box for all nodes.
     */
    public Rectangle getBounds()
    {
        double xMin = 1e10f;
        double xMax = -1e10f;

        double yMin = 1e10f;
        double yMax = -1e10f;

        for (final LayoutNode thisNode : m_Nodes)
        {
            final DisplayIndependentPoint nodePos = thisNode.getPosition();
            final DisplayIndependentDimension nodeSize = thisNode.getSize();

            final double nodeRight = nodePos.x + nodeSize.width;
            final double nodeTop = nodePos.y + nodeSize.height;

            xMin = Math.min( xMin, nodePos.x );
            xMax = Math.max( xMax, nodeRight );

            yMin = Math.min( yMin, nodePos.y );
            yMax = Math.max( yMax, nodeTop );
        }
        return new Rectangle( xMin, yMin, xMax - xMin, yMax - yMin );
    }

    public void moveBy(final Vector moveVect)
    {
        for (LayoutNode thisNode : m_Nodes)
        {
            thisNode.moveBy( moveVect );
            thisNode.adjustLayoutPos();
        }
    }

    public void normalizeNodesPosition()
    {
        final DisplayIndependentRectangle currentBounds = getBounds();
        moveBy( new Vector( -currentBounds.x, -currentBounds.y ) );
    }

    class LayoutNodeConnection implements Comparable<LayoutNodeConnection>, Iterable<LayoutNode>
    {
        private final LayoutNode m_Node;

        public LayoutNodeConnection(LayoutNode inNode)
        {
            m_Node = inNode;
        }

        public final LayoutNode getNode()
        {
            return m_Node;
        }

        public final LayoutConnectionList getConnections()
        {
            return getConnectionsFromNode( m_Node );
        }

        public int getNumberOfConnections()
        {
            return getConnections().size();
        }

        @Override
        public int compareTo(LayoutNodeConnection inNodeConnection)
        {
            final int thisConnectionCount = getNumberOfConnections();
            final int otherConnectionCount = inNodeConnection.getNumberOfConnections();

            return (thisConnectionCount >= otherConnectionCount) ? +1 : -1;
        }

        @Override
        public String toString()
        {
            return "LayoutNodeConnection (" + hashCode() + ") = {#conn=" + getNumberOfConnections() + "}";
        }

        /**
         * Get all peer nodes which are connected with this node.
         * 
         * @return A list of all peer nodes.
         */
        public final LayoutNodeList getPeerNodesConnected()
        {
            return getConnections().getPeerNodesConnectedWithNode( m_Node );
        }

        @Override
        public Iterator<LayoutNode> iterator()
        {
            final LayoutNodeList peerNodes = getPeerNodesConnected();
            return peerNodes.iterator();
        }
    } // class LayoutNodeConnection

    private boolean hasNode(final TreeSet<LayoutNodeConnection> sortedNodeSet, final LayoutNode node)
    {
        for (LayoutNodeConnection connection : sortedNodeSet)
        {
            for (LayoutNode childNode : connection)
            {
                if (childNode == node)
                {
                    return true;
                }
            }
        }
        return false; // not found
    }

    private LayoutNodeList getCenterNodes()
    {
        LayoutNodeList list = new LayoutNodeList();

        TreeSet<LayoutNodeConnection> sortedNodeSet = new TreeSet<LayoutNodeConnection>();
        for (LayoutNode node : m_Nodes)
        {
            // Only insert nodes which are not inserted already.
            if (!hasNode( sortedNodeSet, node ))
            {
                sortedNodeSet.add( new LayoutNodeConnection( node ) );
                list.add( node );
            }
        }
        return list;
    }

    private void prelayout()
    {
        if (!getParentNodes().isEmpty())
        {
            prelayoutAccordingGroups();
        }
        else
        {
            prelayoutSoleNodes();
        }
    }

    private void prelayoutAccordingGroups()
    {
        final LayoutNodeList parentNodes = getParentNodes();

        final float radiusX = 200.0f;
        final float radiusY = 150.0f;

        final int incX = (int)(radiusX * 2.5);
        final int incY = (int)(radiusY * 2.5);

        DisplayIndependentPoint centerPoint = new DisplayIndependentPoint( 0, 0 );

        int idx = 0;
        final int kGraphsPerRow = 6;

        for (LayoutNode parentNode : parentNodes)
        {
            final LayoutNodeList childNodes = parentNode.getChildNodes();

            int nodeIdx = 0;

            for (LayoutNode childNode : childNodes)
            {
                final double phi = (nodeIdx * 2.0 * Math.PI) / (childNodes.size());

                final double rscale = 1;

                final double x = centerPoint.x + radiusX * rscale * Math.sin( phi );
                final double y = centerPoint.y + radiusY * rscale * Math.cos( phi );

                // System.out.println("nodeIdx = "+nodeIdx+" / "+peerNodeCnt+" : phi = " + phi + ", x="+x+", y="+y);

                childNode.setPosition( x, y );
                ++nodeIdx;
            }

            if (idx % kGraphsPerRow == (kGraphsPerRow - 1))
            {
                centerPoint.x = 0.0;
                centerPoint.y += incY;
            }
            else
            {
                centerPoint.x += incX;
            }
            ++idx;
        }
    }

    private void prelayoutSoleNodes()
    {
        TreeSet<LayoutNodeConnection> sortedNodeSet = new TreeSet<LayoutNodeConnection>();

        for (LayoutNode node : m_Nodes)
        {
            // Only insert nodes which are not inserted already.
            if (!hasNode( sortedNodeSet, node ))
            {
                sortedNodeSet.add( new LayoutNodeConnection( node ) );
            }
        }

        Iterator<LayoutNodeConnection> it = sortedNodeSet.descendingIterator();

        final float radiusX = 200.0f;
        final float radiusY = 150.0f;

        final int incX = (int)(radiusX * 2.5);
        final int incY = (int)(radiusY * 2.5);

        DisplayIndependentPoint centerPoint = new DisplayIndependentPoint( 0, 0 );

        int idx = 0;
        final int kGraphsPerRow = 6;

        for (; it.hasNext();)
        {
            final LayoutNodeConnection layoutConn = it.next();

            layoutConn.getNode().setPosition( centerPoint );

            final LayoutNodeList peerNodes = layoutConn.getPeerNodesConnected();

            int nodeIdx = 0;
            final int peerNodeCnt = peerNodes.size();

            for (final LayoutNode node : peerNodes)
            {
                final double phi = (nodeIdx * 2.0 * Math.PI) / (peerNodeCnt);

                final double rscale = 1;
                final double x = centerPoint.x + radiusX * rscale * Math.sin( phi );
                final double y = centerPoint.y + radiusY * rscale * Math.cos( phi );

                // System.out.println("nodeIdx = "+nodeIdx+" / "+peerNodeCnt+" : phi = " + phi + ", x="+x+", y="+y);

                node.setPosition( x, y );
                ++nodeIdx;
            }

            if (idx % kGraphsPerRow == (kGraphsPerRow - 1))
            {
                centerPoint.x = 0.0;
                centerPoint.y += incY;
            }
            else
            {
                centerPoint.x += incX;
            }
            ++idx;
            // System.out.println("layoutConn: " + layoutConn);
        }
    }

    private void addHelpNode()
    {
        m_HelperNode = new LayoutNode( new DisplayIndependentRectangle( 100, 100, 10, 10 ), 0 );

        // get all center nodes
        final LayoutNodeList centerNodes = getCenterNodes();

        for (LayoutNode node : m_Nodes)
        {
            // check if this is a center node...
            boolean isCenterNode = centerNodes.contains( node );
            if (isCenterNode)
            {
                final LayoutConnection connection = new LayoutConnection( node, m_HelperNode, 0.2 );
                m_Connections.add( connection );
            }
        }
        m_Nodes.add( m_HelperNode );
    }

    private void removeNode(LayoutNode inNode)
    {
        if (inNode != null)
        {
            // First, remove all connections which are used by the given node
            LayoutConnectionList connections = getConnectionsForNode( inNode );

            for (LayoutConnection connection : connections)
            {
                m_Connections.remove( connection );
            }

            // Remove the array list from the map (the connection cache)
            m_Connections.remove( connections );

            // Remove the node finally.
            m_Nodes.remove( inNode );
        }
    }

    private void removeHelpNode()
    {
        // search the center node
        removeNode( m_HelperNode );
        // m_CenterNode = null;
    }

    public void recalc()
    {
        if (!m_Nodes.isEmpty())
        {
            prelayout();

            addHelpNode();

            // final int kIterations = 1;
            final int kIterations = 1000;

            for (int i = 0; i != kIterations; ++i)
            {
                final double sMoveScale = (i > 800) ? 20 : 10;

                double maxMoveLength = 0;

                for (LayoutNode thisNode : m_Nodes)
                {
                    Vector resultForceVector = calculateForceVectorForNode( thisNode );
                    Vector resultMoveVector = resultForceVector;
                    resultMoveVector.scaleBy( sMoveScale );

                    thisNode.moveBy( resultMoveVector );

                    if (thisNode.isChildNode())
                    {
                        LayoutNode parentNode = thisNode.getParentNode();

                        final Rectangle parentRect = getBoundsForParentNode( parentNode )
                                .getInsetRect( m_GroupParentNodeInset );
                        parentNode.setSize( parentRect.getSize() );
                    }

                    final double thisMoveLength = resultMoveVector.lengthSqu();
                    maxMoveLength = Math.max( thisMoveLength, maxMoveLength );
                }

                if (maxMoveLength < 3.0)
                {
                    break; // Premature end of rendering
                }
            } // for(;;)

            centerChildNodesInParents();

            removeHelpNode();

            normalizeNodesPosition();
            addGroupGraphNodes();
        }
    }

    private LayoutNodeList getParentNodes()
    {
        LayoutNodeList parentNodes = new LayoutNodeList();

        HashSet<LayoutNode> parentNodesMap = new HashSet<LayoutNode>();

        // Search all parent nodes out of all nodes
        for (LayoutNode thisNode : m_Nodes)
        {
            final LayoutNode parentNode = thisNode.getParentNode();

            if (null != parentNode)
            {
                if (!parentNodesMap.contains( parentNode ))
                {
                    parentNodes.add( parentNode );
                }
                parentNodesMap.add( parentNode );

            }
        }
        parentNodesMap = null;
        return parentNodes;
    }

    private void centerChildNodesInParents()
    {
        LayoutNodeList parentNodes = getParentNodes();

        // Iterate through all parents and get their children...
        for (LayoutNode parentNode : parentNodes)
        {
            final Rectangle parentBounds = getBoundsForParentNode( parentNode );
            final LayoutNodeList childNodes = parentNode.getChildNodes();

            Vector mv = new Vector( parentBounds.getCenterPointOfRectangle(), parentNode.getCenterPosition() );

            for (LayoutNode childNode : childNodes)
            {
                childNode.moveBy( mv );
            }
        }
    }

    public boolean nodesConnected(LayoutNode nodeA, LayoutNode nodeB)
    {
        return (null != getConnectionForNodes( nodeA, nodeB ));
    }

    public LayoutConnection getConnectionForNodes(LayoutNode nodeA, LayoutNode nodeB)
    {
        final LayoutConnectionList connArray = getConnectionsForNode( nodeA );

        for (LayoutConnection thisConnection : connArray)
        {
            if (thisConnection.nodeB() == nodeB || thisConnection.nodeA() == nodeB)
            {
                return thisConnection; // found
            }
        }
        return null; // not found!
    }

    /**
     * Get all connections which are referred from <b>or</b> to a certain node.
     * 
     * @param inNode
     *            The node to get all connections.
     * @see #getConnectionsFromNode(LayoutNode)
     */
    private final LayoutConnectionList getConnectionsForNode(final LayoutNode inNode)
    {
        // Check if this Connection list is still cached?
        LayoutConnectionList connList = m_ConnectionMap.get( inNode );

        // Not Found in cache? Then collect all connections.
        if (null == connList)
        {
            connList = new LayoutConnectionList();

            for (LayoutConnection conn : m_Connections)
            {
                // It doesn't matter if this *or* the other node refers to this
                // connection */
                if ((conn.nodeA() == inNode) || (conn.nodeB() == inNode))
                {
                    connList.add( conn );
                }
            }
            m_ConnectionMap.put( inNode, connList ); // ...and place it into the
                                                     // cache
            return connList;
        }
        return connList;
    }

    /**
     * Get all connections which are coming from a certain node. Note that connections which refer <b>to</b> this node
     * are not recognized by this call! To get this connections too you have to call
     * {@link #getConnectionsForNode(LayoutNode)}
     * 
     * @param inNode
     *            The node to get all connections which are leading to another node.
     * @see #getConnectionsForNode(LayoutNode)
     */
    private final LayoutConnectionList getConnectionsFromNode(final LayoutNode inNode)
    {
        LayoutConnectionList connList = new LayoutConnectionList();

        for (LayoutConnection conn : m_Connections)
        {
            // It doesn't matter if this *or* the other node refers to this
            // connection */
            if (conn.nodeA() == inNode)
            {
                connList.add( conn );
            }
        }
        return connList;
    }

    private Vector calculateForceVectorForNode(final LayoutNode node)
    {
        // If this node has a parent node then perform a special handling
        LayoutNode parentNode = node.getParentNode();
        if (parentNode != null)
        {
            return calculateSiblingForceForNode( node );
        }
        else
        {
            final Vector repulseForceVect = calculateRepulsionForceVectorForNode( node );
            final Vector attractForceVect = calculateSpringForceVectorForNode( node );

            Vector v = new Vector( 0, 0 );
            v.addSelf( repulseForceVect );
            v.addSelf( attractForceVect );
            return v;
        }
    }

    Vector calculateSiblingForceForNode(final LayoutNode node)
    {
        Vector force = new Vector( 0, 0 );
        final LayoutNode parentNode = node.getParentNode();

        AttractForce attractForce = new AttractForce( 0.5 );
        final Vector attractForceVector = attractForce.calcForceVectorBetweenNodes( node, parentNode );
        force.addSelf( attractForceVector );

        // Just treat all children which belongs to this parent - the siblings
        final LayoutNodeList siblings = node.getSiblingNodes();
        for (LayoutNode peerNode : siblings)
        {
            AttractForce attractForce1 = new AttractForce( 0.2 );
            final Vector attractForceVector1 = attractForce1.calcForceVectorBetweenNodes( node, peerNode );
            force.addSelf( attractForceVector1 );

            RepulseForce3 repulseForce = new RepulseForce3( node.getRepulseStrength() * peerNode.getRepulseStrength() );
            Vector repulseForceVector = repulseForce.calcForceVectorBetweenNodes( node, peerNode );
            force.addSelf( repulseForceVector );
        }
        return force;
    }

    private Vector calculateSpringForceVectorForNode(final LayoutNode inNode)
    {
        Vector forceVect = new Vector( 0, 0 );
        // ===================================================
        // Calculate the spring force
        // ===================================================
        // Get all connection for this node
        LayoutConnectionList cl = getConnectionsForNode( inNode );

        // now, iterate through all connections and get the peer node
        for (LayoutConnection conn : cl)
        {
            AttractForce attractForce = new AttractForce( conn.getAttractStrength() );
            LayoutNode peerNode = (conn.nodeA() == inNode) ? conn.nodeB() : conn.nodeA();

            // If this node is a peer node then treat its parent node as peer node.
            if (peerNode.isChildNode())
            {
                LayoutNode parentNode = peerNode.getParentNode();
                Vector attractForceVector = attractForce.calcForceVectorBetweenNodes( inNode, parentNode );

                double scale = 1.0;
                int siblingNodeCount = parentNode.getChildNodes().size() - 1;
                if (siblingNodeCount > 0)
                {
                    scale = 1 / siblingNodeCount;
                }
                attractForceVector.scaleBy( scale );
                forceVect.addSelf( attractForceVector );
                continue;
            }
            forceVect.addSelf( attractForce.calcForceVectorBetweenNodes( inNode, peerNode ) );
        }

        // System.out.println("springForceVect=" + forceVect);
        return forceVect;
    }

    private Vector calculateRepulsionForceVectorForNode(final LayoutNode inNode)
    {
        Vector forceVect = new Vector( 0, 0 );
        // ===================================================
        // Calculate the repulse force
        // ===================================================
        for (final LayoutNode peerNode : m_Nodes)
        {
            if (peerNode != inNode)
            {
                if (!peerNode.isChildNode())
                {
                    // System.out.println("repulseVect=" + repulseVect);
                    RepulseForce3 repulseForce = new RepulseForce3( inNode.getRepulseStrength()
                            * peerNode.getRepulseStrength() );

                    forceVect.addSelf( repulseForce.calcForceVectorBetweenNodes( inNode, peerNode ) );
                }
            }
        }

        // System.out.println("repulseForceVect=" + forceVect);
        return forceVect;
    }

    public Graph getGraph()
    {
        return m_Graph;
    }
}
