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
import java.util.Set;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.ContinuousLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionUseCase;
import com.elektrobit.ebrace.viewer.StructureSelectionUtil;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

import lombok.extern.log4j.Log4j;

/**
 * Improved Spring layout Algorithm which uses spring forces and repulsion forces to weight the graph nodes. The spring
 * forces are linear forces and are part of the connections. The repulsion forces are properties of the nodes and act
 * like negative gravitation forces in a r<sup>3</sup> semantic.
 * 
 * This means the force will increase by a factor of 8 if the distance is halved.
 * 
 * The actual calculation will be done by a GraphManager class. This layout algorithm employs a instance of the
 * GraphManager to calculate the forces.
 * 
 * @version 1.0
 * @author Hans-Peter Dusel &lt;<a href="mailto:pedu2501@elektrobit.com">pedu2501@elektrobit.com</a>&gt;
 */
@SuppressWarnings("restriction")
@Log4j
public class EBSpringLayoutAlgorithm extends ContinuousLayoutAlgorithm implements SelectStructureInteractionCallback
{
    private SpringGraphManager m_GraphManager;
    private GroupedNodeMap m_GroupedNodeMap = null;
    private String m_GroupLayoutFilterString;
    private final List<EntityConnectionData> comRelationModelSelection = new ArrayList<EntityConnectionData>();
    private final List<GraphItem> comRelationEdgesSelection = new ArrayList<GraphItem>();
    private final SelectStructureInteractionUseCase selectStructureInteractionUseCase;

    public EBSpringLayoutAlgorithm()
    {
        super( LayoutStyles.NO_LAYOUT_NODE_RESIZING );
        m_GraphManager = null;
        selectStructureInteractionUseCase = UseCaseFactoryInstance.get().makeSelectStructureInteractionUseCase( this );
    }

    public void setLayoutGroupFilterString(String currentLayoutFilterString)
    {
        m_GroupLayoutFilterString = currentLayoutFilterString;
    }

    @Override
    protected boolean performAnotherNonContinuousIteration()
    {
        return false;
    }

    @Override
    protected void computeOneIteration(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
            double x, double y, double width, double height)
    {
    }

    @Override
    protected boolean isValidConfiguration(boolean asynchronous, boolean continuous)
    {
        return true;
    }

    @SuppressWarnings("unused")
    private void doDump(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider)
    {
        int idx = 0;

        java.util.HashMap<GraphNode, String> nodeMap = new java.util.HashMap<GraphNode, String>();

        for (InternalNode intNode : entitiesToLayout)
        {
            final GraphNode graphNode = LayoutNodeTools.getGraphNodeForInternalNode( intNode );
            final String nodeName = graphNode.getText();
            org.eclipse.draw2d.geometry.Point graphLocation = graphNode.getLocation();
            org.eclipse.draw2d.geometry.Dimension graphSize = graphNode.getSize();

            final String varName = "node" + (idx++);

            nodeMap.put( graphNode, varName );
        }

        idx = 0;
        for (InternalRelationship intRelationship : relationshipsToConsider)
        {
            final String varName = "connection" + (idx++);

            final GraphConnection graphConnection = LayoutConnection
                    .getGraphConnectionForInternalRelationship( intRelationship );
            if (graphConnection != null)
            {
                GraphNode nodeA = graphConnection.getSource();
                GraphNode nodeB = graphConnection.getDestination();
                if (nodeA != null && nodeB != null)
                {
                    // Get the GraphNodes
                    String nodeNameA = nodeMap.get( nodeA );
                    String nodeNameB = nodeMap.get( nodeB );
                }
            }
        }
    }

    private long m_StartTime;

    class GroupedNodeMap
    {
        class GroupedNode extends ArrayList<InternalNode>
        {
            /**
             * 
             */
            private static final long serialVersionUID = 4206519154600504779L;

            GroupedNode()
            {
                super();
            }

            public void addNode(InternalNode intNode)
            {
                this.add( intNode );
            }
        }

        HashMap<TreeNode, GroupedNode> m_GroupMap;

        boolean hasGroups()
        {
            return !m_GroupMap.isEmpty();
        }

        public GroupedNodeMap(final InternalNode[] intNodes, final String type)
        {
            m_GroupMap = new HashMap<TreeNode, GroupedNode>();

            for (InternalNode intNode : intNodes)
            {
                // get the TreeNode for this internal node...
                final TreeNode thisTreeNode = LayoutNodeTools.getGraphTreeNodeForInternalNode( intNode );
                if (null != thisTreeNode)
                {
                    TreeNode thisChildTreeNode = thisTreeNode;
                    TreeNode parentNode = LayoutNodeTools.getParentNodeOfType( thisChildTreeNode, type );

                    if (null != parentNode)
                    {
                        // try to get the internal node list for this (filtered) parent node
                        GroupedNode nodeList = m_GroupMap.get( parentNode );

                        // does not exists yet? -> Then create a new GroupedNode in the map.
                        if (null == nodeList)
                        {
                            nodeList = new GroupedNode();
                            m_GroupMap.put( parentNode, nodeList );
                        }

                        // ..and add this child node in this list.
                        nodeList.add( intNode );
                    }
                }
            }
        }

        public void initGraphManagerWithNodes(SpringGraphManager graphManager)
        {
            Set<TreeNode> TreeNodes = m_GroupMap.keySet();

            for (TreeNode vn : TreeNodes)
            {
                String parentName = vn.getName();
                LayoutNode parentLayoutNode = graphManager.addParentNode( parentName );

                GroupedNode nodes = m_GroupMap.get( vn );
                for (InternalNode intNode : nodes)
                {
                    LayoutNode thisNode = graphManager.addNode( intNode );
                    thisNode.setParentNode( parentLayoutNode );
                }
            }
        }
    }

    private boolean isCtrlStateMask(MouseEvent event)
    {
        return (event.getState() & SWT.CTRL) > 0;
    }

    private void initGraphManagerFromInternalRepresentation(InternalNode[] entitiesToLayout,
            final InternalRelationship[] relationshipsToConsider)
    {
        m_StartTime = java.lang.System.currentTimeMillis();
        comRelationModelSelection.clear();
        comRelationEdgesSelection.clear();
        m_GraphManager = new SpringGraphManager();
        entitiesToLayout = LayoutNodeTools.cleanupDecorationNodes( entitiesToLayout );

        m_GroupedNodeMap = new GroupedNodeMap( entitiesToLayout, m_GroupLayoutFilterString );

        if (m_GroupedNodeMap.hasGroups())
        {
            m_GroupedNodeMap.initGraphManagerWithNodes( m_GraphManager );
        }
        else
        {
            // Apply all nodes to the graph manager
            for (final InternalNode intNode : entitiesToLayout)
            {
                m_GraphManager.addNode( intNode );
            }
        }

        for (InternalRelationship intRelationship : relationshipsToConsider)
        {
            final GraphConnection graphConnection = LayoutConnection
                    .getGraphConnectionForInternalRelationship( intRelationship );

            graphConnection.getConnectionFigure().addMouseListener( new MouseListener()
            {

                @SuppressWarnings("unchecked")
                @Override
                public void mouseReleased(MouseEvent arg0)
                {
                    if (!isCtrlStateMask( arg0 ))
                    {
                        comRelationModelSelection.clear();
                        comRelationEdgesSelection.clear();
                    }
                    List<Object> c = graphConnection.getGraphModel().getConnections();

                    for (Object o : c)
                    {
                        Object mouseEventSource = arg0.getSource();
                        if (((GraphConnection)o).getConnectionFigure().equals( mouseEventSource ))
                        {
                            EntityConnectionData entityConnectionData = (EntityConnectionData)((GraphConnection)o)
                                    .getData();
                            GraphItem item = (GraphItem)o;
                            // notify about selection
                            if (!comRelationModelSelection.contains( entityConnectionData )
                                    && !comRelationEdgesSelection.contains( item ))
                            {
                                comRelationModelSelection.add( entityConnectionData );
                                comRelationEdgesSelection.add( item );
                            }
                            else
                            {
                                if (isCtrlStateMask( arg0 ))
                                {
                                    comRelationModelSelection.remove( entityConnectionData );
                                    comRelationEdgesSelection.remove( item );
                                }
                            }

                        }

                    }

                    List<ComRelation> comrelationsFromGraphSelection = StructureSelectionUtil
                            .getComrelationsFromGraphSelection( comRelationModelSelection );
                    selectStructureInteractionUseCase.setComRelationsSelected( comrelationsFromGraphSelection );

                    GraphItem[] selectedConnections = new GraphItem[comRelationEdgesSelection.size()];
                    int count = 0;
                    for (GraphItem item : comRelationEdgesSelection)
                    {
                        selectedConnections[count] = item;
                        count++;
                    }

                    graphConnection.getGraphModel().setSelection( selectedConnections ); // used to highlight selected
                                                                                         // edges
                }

                @Override
                public void mousePressed(MouseEvent arg0)
                {
                }

                @Override
                public void mouseDoubleClicked(MouseEvent arg0)
                {
                }
            } );

            m_GraphManager.addConnection( intRelationship );

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
            double x, double y, double width, double height)
    {
        initGraphManagerFromInternalRepresentation( entitiesToLayout, relationshipsToConsider );

        GraphNode o = (GraphNode)entitiesToLayout[0].getLayoutEntity().getGraphData();

        ZestRootLayer z = (ZestRootLayer)o.getNodeFigure().getParent();

        if (z != null)
        {
            int numberOfChildren = z.getChildren().size();
            Object b = z.getChildren().get( numberOfChildren - 1 );
            if (b instanceof BoundFigure)
            {
                List<Object> newChildren = new ArrayList<Object>( numberOfChildren );
                newChildren.add( b );
                newChildren.addAll( z.getChildren() );
                z.getChildren().clear();

                for (int i = 0; i < numberOfChildren; i++)
                {
                    z.getChildren().add( newChildren.get( i ) );
                }
            }
        }

        m_GraphManager.recalc();
    }

    class X implements Comparable<X>
    {
        public Object m_object;

        public X(Object obj)
        {
            m_object = obj;
        }

        @Override
        public int compareTo(X o)
        {
            final int thisHash = hashCode();
            final int otherHash = o.hashCode();
            return (thisHash > otherHash) ? +1 : (thisHash < otherHash) ? -1 : 0;
        }

        @Override
        public int hashCode()
        {
            final int maskedHashMap = m_object.hashCode() & 0x0fffffff;

            if (m_object instanceof BoundFigure)
            {
                return 0x40000000 | maskedHashMap;
            }
            else if (m_object instanceof PolylineConnection)
            {
                return 0x50000000 | maskedHashMap;
            }
            else if (m_object instanceof GraphLabel)
            {
                return 0x60000000 | maskedHashMap;
            }
            else
            {
                return 0x70000000 | maskedHashMap;
            }
        }
    }

    @Override
    protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider)
    {
        m_GraphManager = null;
        long now = java.lang.System.currentTimeMillis();
        long layoutTime = now - m_StartTime;
        if (layoutTime > 400)
        {
            log.warn( String.format( "Time needed to layout: %d ms.", layoutTime ) );
        }
    }

    @Override
    protected int getTotalNumberOfLayoutSteps()
    {
        return 0;
    }

    @Override
    protected int getCurrentLayoutStep()
    {
        return 0;
    }
}
