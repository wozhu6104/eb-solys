/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.zest.core.viewers.EntityConnectionData;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class StructureSelectionUtil
{
    private static final GenericOSGIServiceTracker<ComRelationProvider> comRelationProviderTracker = new GenericOSGIServiceTracker<ComRelationProvider>( ComRelationProvider.class );

    public static List<TreeNode> getTreeNodesFromSelection(ISelection selection)
    {
        if (selection instanceof ITreeSelection)
        {
            ITreeSelection treeSelection = (ITreeSelection)selection;
            List<TreeNode> selectedNodes = new ArrayList<TreeNode>();
            for (TreePath path : treeSelection.getPaths())
            {
                Object selectedSegment = path.getLastSegment();
                if (selectedSegment != null && selectedSegment instanceof TreeNode)
                {
                    TreeNode selectedNode = (TreeNode)selectedSegment;
                    selectedNodes.add( selectedNode );
                }

            }
            return selectedNodes;
        }
        return null;

    }

    public static ISelection getSelectionStructureFromNodes(List<TreeNode> selectedNodes)
    {
        List<TreePath> treePaths = new ArrayList<TreePath>();
        for (TreeNode treeNode : selectedNodes)
        {
            List<TreeNode> nodePath = getPathForNode( treeNode );
            TreePath treePath = new TreePath( nodePath.toArray() );
            treePaths.add( treePath );
        }

        TreeSelection treeSelection = new TreeSelection( treePaths.toArray( new TreePath[treePaths.size()] ) );
        return treeSelection;
    }

    private static List<TreeNode> getPathForNode(TreeNode treeNode)
    {
        List<TreeNode> pathToNode = new ArrayList<TreeNode>();
        pathToNode.add( treeNode );

        TreeNode currentNode = treeNode;
        while (currentNode.getParent() != null)
        {
            pathToNode.add( 0, currentNode.getParent() );
            currentNode = currentNode.getParent();
        }
        return pathToNode;
    }

    public static List<ComRelation> getComrelationsFromGraphSelection(List<EntityConnectionData> entityConnections)
    {
        List<ComRelation> selectedComrelations = new ArrayList<ComRelation>();
        ComRelationProvider comRelationProvider = comRelationProviderTracker.getService();

        for (EntityConnectionData selectedConnection : entityConnections)
        {
            if (selectedConnection.source instanceof TreeNode && selectedConnection.dest instanceof TreeNode)
            {
                TreeNode srcNode = (TreeNode)selectedConnection.source;
                TreeNode destNode = (TreeNode)selectedConnection.dest;
                for (ComRelation nextComRelation : comRelationProvider.getComRelations( srcNode ))
                {
                    if (nextComRelation.getReceiver().equals( destNode ))
                    {

                        selectedComrelations.add( nextComRelation );
                        for (ComRelation nextChildComRelation : comRelationProvider
                                .getChildrenComRelationsRecusivly( nextComRelation ))// TODO no recursion needed, only
                                                                                     // the top level comrelation
                        {
                            selectedComrelations.add( nextChildComRelation );
                        }
                        break;
                    }
                }
            }
        }
        return selectedComrelations;
    }
}
