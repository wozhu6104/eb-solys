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
import java.util.List;

import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionUseCase;
import com.elektrobit.ebrace.viewer.dbusgraph.GraphView;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

@SuppressWarnings("restriction")
class BoundFigure extends org.eclipse.draw2d.Figure
{
    private final String m_Label;

    public BoundFigure(final DisplayIndependentRectangle rect, final String label)
    {
        super();

        m_Label = label;

        setEnabled( true );
        setVisible( true );

        setBounds( new org.eclipse.draw2d.geometry.Rectangle( (int)rect.x,
                                                              (int)rect.y,
                                                              (int)rect.width,
                                                              (int)rect.height ) );
    }

    @Override
    public void paintClientArea(Graphics graphics)
    {
        graphics.setForegroundColor( new Color( null, 0x40, 0x40, 0x40 ) );
        graphics.setLineWidth( 2 );

        final org.eclipse.draw2d.geometry.Rectangle r = getBounds();

        final int textX = r.x + 7;
        final int textY = r.y + 3;

        graphics.drawString( m_Label, new Point( textX, textY ) );
        graphics.drawRoundRectangle( new org.eclipse.draw2d.geometry.Rectangle( r.x + 1,
                                                                                r.y + 1,
                                                                                r.width - 2,
                                                                                r.height - 2 ),
                                     15,
                                     15 );
    }

    @Override
    public boolean isShowing()
    {
        return true;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean isOpaque()
    {
        return false;
    }

    @Override
    public boolean isRequestFocusEnabled()
    {
        return true;
    }
}

/**
 * This calls is a Custom ZEST GraphNode which implements a Group Box.
 * 
 * @author pedu2501
 */
@SuppressWarnings("restriction")
public class EBGroupGraphNode extends CGraphNode
        implements
            MouseListener,
            MouseMotionListener,
            SelectStructureInteractionCallback
{
    private static GraphView s_GraphView = null;
    private static EBGroupGraphNode s_capturedGraphNode = null;
    private static Point s_capturedOffset = null;
    private final ArrayList<ChildGraphNode> m_ChildNodeList;
    private final SelectStructureInteractionUseCase selectStructureUseCase;

    /**
     * Get the GraphView object.
     * 
     * Since the GraphView is a single instance this method will search the current Eclipse Workbench session for a view
     * named "GraphView" and store it into a static variable in order to accelerate subsequent accesses.
     * 
     * @return The GraphView instance.
     */
    public static GraphView getGraphView()
    {
        if (s_GraphView == null) // not cached (yet?)
        {
            // The search it by...
            IViewReference ivr[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getViewReferences();
            // ...iterating through all active Workbench windows...
            for (IViewReference ref : ivr)
            {
                // ..and search this whose id contains the name "GraphView".
                if (ref.getId().lastIndexOf( "GraphView" ) != -1)
                {
                    IViewReference graphViewRef = ref;

                    IWorkbenchPart iwpart = graphViewRef.getPart( false );
                    // Check if this Workbench part is of the kind "GraphView".
                    if (iwpart instanceof GraphView)
                    {
                        // if it is then we got it. -> Remember it.
                        return s_GraphView = (GraphView)iwpart;
                    }
                }
            }
        }
        return s_GraphView;
    }

    public EBGroupGraphNode(IContainer graphModel, int style, IFigure figure)
    {
        super( graphModel, style, figure );
        selectStructureUseCase = UseCaseFactoryInstance.get().makeSelectStructureInteractionUseCase( this );

        m_ChildNodeList = new ArrayList<ChildGraphNode>();

        figure.addMouseListener( this );
        figure.addMouseMotionListener( this );

        if (s_masterView == null)
        {
            IFigure parentView = figure.getParent();

            for (; null != parentView; parentView = parentView.getParent())
            {
                if (parentView instanceof FreeformViewport)
                {
                    s_masterView = parentView;
                    s_masterView.addMouseMotionListener( this );
                    break;
                }
            }
        }
    }

    @Override
    public void dispose()
    {
        selectStructureUseCase.unregister();
        super.dispose();
    }

    @Override
    /**
     * Override the setLocation method in order to perform a relocation of all children either.
     * 
     * @param x
     *            The new X-Position of this GroupGraphNode.
     * @param y
     *            The new Y-Position of this GroupGraphNode.
     */
    public void setLocation(double x, double y)
    {
        super.setLocation( x, y ); // set the position of this node...

        // ...and all of its children.
        for (ChildGraphNode childNode : m_ChildNodeList)
        {
            childNode.moveRelToParentNode();
        }
    }

    @Override
    public boolean isSizeFixed()
    {
        return true;
    }

    public EBGroupGraphNode(IContainer graphModel, final DisplayIndependentRectangle rect, String label)
    {
        this( graphModel, SWT.NONE, createEBGroupNodeInstance( rect, label ) );
        setLocation( rect.x, rect.y );
        setSize( rect.width, rect.height );
    }

    static IFigure s_masterView = null;

    @SuppressWarnings("unchecked")
    public void addChildNode(GraphNode graphNode)
    {
        m_ChildNodeList.add( new ChildGraphNode( this, graphNode ) );
        graphNode.getNodeFigure().addMouseListener( this );
        graphNode.getNodeFigure().addMouseMotionListener( this );

        ZestRootLayer z = (ZestRootLayer)graphNode.getNodeFigure().getParent();

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

    private static BoundFigure createEBGroupNodeInstance(final DisplayIndependentRectangle rect, String label)
    {
        BoundFigure figure = new BoundFigure( rect, label );
        return figure;
    }

    @Override
    // from MouseListener
    public void mouseDoubleClicked(MouseEvent arg0)
    {
    }

    private void selectAllChildren()
    {
        // -----------------------------------------------------
        // Get the GraphView instance...
        final GraphView gv = getGraphView();

        if (gv != null)
        {
            /*
             * This list carries all children of this EBGraphNode object.
             * 
             * Since these children are of the kind GraphNode they has to converted as their corresponding
             * ViewerGraph.ViewerNode objects. Utilize the LayoutNodeTools to do so.
             */
            ArrayList<TreeNode> selectedChildNodes = new ArrayList<TreeNode>();

            // System.out.println("selecting "+ m_ChildNodeList.size() +
            // " children.");

            for (ChildGraphNode childNode : m_ChildNodeList)
            {
                /*
                 * Transform all children which are objects of the kind GraphNodes to ViewerGraph.ViewerNode objects and
                 * append it to the list.
                 */
                selectedChildNodes.add( LayoutNodeTools.getGraphTreeNodeForGraphNode( childNode.getGraphNode() ) );
            }

            // Send the GraphView this list of ViewerGraph.ViewerNode
            // objects.
            selectStructureUseCase.setNodesSelected( selectedChildNodes );
        }
    }

    private void selectChild(IFigure childFigure)
    {
        for (ChildGraphNode childGraphNode : m_ChildNodeList)
        {
            IFigure nodeFigure = childGraphNode.getGraphNode().getNodeFigure();
            if (nodeFigure == childFigure)
            {
                ArrayList<TreeNode> selectedChildNodes = new ArrayList<TreeNode>();
                selectedChildNodes.add( LayoutNodeTools.getGraphTreeNodeForGraphNode( childGraphNode.getGraphNode() ) );

                // Send the GraphView this list of
                // ViewerGraph.ViewerNode
                // objects.
                selectStructureUseCase.setNodesSelected( selectedChildNodes );
                break;
            }
        }
    }

    @Override
    /**
     * Either on a GraphNode or a BoundFigure has been clicked.
     * 
     */
    public void mousePressed(MouseEvent arg0)
    {
        // Check if the click really occurred on a figure.
        Object o = arg0.getSource();

        if (o instanceof IFigure)
        {
            // Remember the clicked GraphModelNode node for drag operation. This will be canceled by mouseReleased().
            s_capturedGraphNode = this;

            // Remember the offset of the mouse click relatively to this Nodes location.
            final Point p0 = s_capturedGraphNode.getLocation();
            s_capturedOffset = new Point( arg0.x - p0.x, arg0.y - p0.y );

            // Get the clicked Figure object.
            IFigure selectedFigure = (IFigure)o;

            // If the click occurred on a BoundFigure then select all children
            if (o instanceof BoundFigure)
            {
                selectAllChildren();
            }
            else
            // Otherwise just select the child.
            {
                selectChild( selectedFigure );
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
        s_capturedGraphNode = null; // Cancel an (probably) pending Drag operation.
    }

    @Override
    // From MouseMotionListener
    public void mouseDragged(MouseEvent me)
    {
        // Just drag if there has been a GraphNode selected by a Mouse click in advance.
        if (null != s_capturedGraphNode)
        {
            // Reposition this node at the new mouse position by honoring the Scapture Offset position.
            s_capturedGraphNode.setLocation( me.x - s_capturedOffset.x, me.y - s_capturedOffset.y );
        }
    }

    @Override
    public void mouseEntered(MouseEvent me)
    {
    }

    @Override
    public void mouseExited(MouseEvent me)
    {
    }

    @Override
    public void mouseHover(MouseEvent me)
    {
    }

    @Override
    public void mouseMoved(MouseEvent me)
    {
    }

    class ChildGraphNode
    {
        private final EBGroupGraphNode m_ParentNode;
        private final GraphNode m_GraphNode;
        private final Point m_OffsetToParent;

        public ChildGraphNode(EBGroupGraphNode parentNode, GraphNode gn)
        {
            m_ParentNode = parentNode;
            m_GraphNode = gn;

            final Point thisPos = m_GraphNode.getLocation();
            final Point parentPos = m_ParentNode.getLocation();

            m_OffsetToParent = new Point( thisPos.x - parentPos.x, thisPos.y - parentPos.y );
        }

        public void moveRelToParentNode()
        {
            final Point parentPos = m_ParentNode.getLocation();
            m_GraphNode.setLocation( parentPos.x + m_OffsetToParent.x, parentPos.y + m_OffsetToParent.y );
        }

        public GraphNode getGraphNode()
        {
            return m_GraphNode;
        }

        public GraphNode getParentGraphNode()
        {
            return m_ParentNode;
        }
    } // class ChildGraphNode
}
