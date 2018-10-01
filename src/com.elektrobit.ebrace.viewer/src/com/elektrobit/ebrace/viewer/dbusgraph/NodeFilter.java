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

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;

class NodeFilter extends ViewerFilter
{
    private final GraphView parent;

    NodeFilter(GraphView parent)
    {
        this.parent = parent;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        boolean result = true;

        if (element instanceof TreeNode)
        {
            return selectTreeNode( element );
        }

        return result;
    }

    private boolean selectTreeNode(Object element)
    {
        TreeNode node = (TreeNode)element;
        String nodeLevelName = node.getTreeLevel().getName();
        if (!nodeLevelName.equals( parent.visibleTreeLayerCombo.getText() ))
        {
            return false;
        }

        CHECKED_STATE checkedState = parent.nodesCheckState.getNodeCheckState( node );

        return !checkedState.equals( CHECKED_STATE.UNCHECKED );
    }

    @Override
    public boolean isFilterProperty(Object element, String property)
    {
        return false;
    }

}
