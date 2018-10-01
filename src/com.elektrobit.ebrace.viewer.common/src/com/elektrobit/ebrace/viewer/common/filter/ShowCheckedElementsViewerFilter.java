/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.filter;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ShowCheckedElementsViewerFilter extends ViewerFilter
{
    boolean isActive;
    private CheckboxTreeViewer cbTreeViewer;

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (viewer instanceof CheckboxTreeViewer)
        {
            if (isActive)
            {
                cbTreeViewer = (CheckboxTreeViewer)viewer;
                return isChecked( cbTreeViewer.getCheckedElements(), element );
            }
            return true;
        }
        return false;
    }

    private boolean isChecked(Object[] elements, Object element)
    {
        if ((element instanceof Tree))
        {
            Tree tree = (Tree)element;
            isChecked( elements, tree.getRootNode() );
            return cbTreeViewer.getChecked( element ) || isChecked( elements, tree.getRootNode() );
        }
        if (element instanceof TreeNode)
        {
            TreeNode node = (TreeNode)element;
            boolean result = false;
            for (TreeNode child : node.getChildren())
            {
                result = isChecked( elements, child );
                if (result)
                    break;
            }
            return result || containesElelemtn( elements, node );
        }
        else
            return cbTreeViewer.getChecked( element );
    }

    boolean containesElelemtn(Object[] elements, Object element)
    {
        for (Object o : elements)
        {
            if (o.equals( element ))
                return true;
        }
        return false;
    }

    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }
}
