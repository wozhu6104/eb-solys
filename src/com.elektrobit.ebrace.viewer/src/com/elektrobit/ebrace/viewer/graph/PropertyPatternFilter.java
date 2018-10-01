/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class PropertyPatternFilter extends PatternFilter
{
    @Override
    protected boolean isLeafMatch(Viewer viewer, Object element)
    {
        if (element instanceof TreeNode)
        {
            boolean result = false;
            TreeNode node = (TreeNode)element;
            result = checkProperties( viewer, node ) || isParentMatch( viewer, node );
            return result;
        }
        return false;
    }

    private boolean checkProperties(Viewer viewer, TreeNode node)
    {
        boolean result = super.isLeafMatch( viewer, node.getName() );
        Properties prop = node.getProperties();
        for (Object o : prop.getKeys())
        {
            result = result || super.isLeafMatch( viewer, o ) || super.isLeafMatch( viewer, prop.getValue( o ) );
        }
        return result;
    }

    private boolean isParentMatch(Viewer viewer, TreeNode node)
    {
        boolean result = false;
        while (node != null)
        {
            result = result || checkProperties( viewer, node );
            node = node.getParent();
        }
        return result;
    }
}
