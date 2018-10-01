/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;

class EdgesFilter extends ViewerFilter
{
    private final GraphView parent;
    private final ComRelationProvider comRelationProvider;

    EdgesFilter(GraphView parent, ComRelationProvider comRelationProvider)
    {
        this.parent = parent;
        this.comRelationProvider = comRelationProvider;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        boolean result = true;

        if (element instanceof TreeNode)
        {
            result = selectTreeNode( element );
        }

        return result;
    }

    private boolean selectTreeNode(Object element)
    {
        if (!parent.hideIsolatedNodesActive)
        {
            return true;
        }

        TreeNode node = (TreeNode)element;
        for (TreeNode connectedNode : comRelationProvider.getConnectedTreeNodes( node ))
        {
            CHECKED_STATE connectedNodeCheckState = parent.nodesCheckState.getNodeCheckState( connectedNode );
            boolean stateChecked = connectedNodeCheckState.equals( CHECKED_STATE.CHECKED );
            boolean statePartiallyChecked = connectedNodeCheckState.equals( CHECKED_STATE.PARTIALLY_CHECKED );
            if (stateChecked || statePartiallyChecked)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFilterProperty(Object element, String property)
    {
        return false;
    }
}
