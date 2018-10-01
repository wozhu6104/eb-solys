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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class StructureTreesContentProvider implements ITreeContentProvider
{
    private static final Logger LOG = Logger.getLogger( StructureTreesContentProvider.class );

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {

    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        if (inputElement instanceof List)
        {
            List<?> inputList = (List<?>)inputElement;
            return inputList.toArray();
        }
        else
            LOG.warn( "getElements: unexpected type of input " + inputElement.getClass() );

        return null;
    }

    @Override
    public Object[] getChildren(Object node)
    {
        if (node instanceof Tree)
            return getChildren( ((Tree)node).getRootNode() );
        if (node instanceof TreeNode)
        {
            TreeNode treeNode = (TreeNode)node;
            return treeNode.getChildren().toArray();
        }
        else
            LOG.warn( "getChildren: unexpected type of node " + node.getClass() );
        return null;
    }

    @Override
    public Object getParent(Object node)
    {
        if (node instanceof TreeNode)
        {
            return ((TreeNode)node).getParent();
        }
        else
            LOG.warn( "getParent: Unexpected type of node " + node.getClass() );

        return null;
    }

    /**
     * Returns whether the given Element has children
     */
    @Override
    public boolean hasChildren(Object element)
    {
        if (element instanceof Tree)
            return hasChildren( ((Tree)element).getRootNode() );
        if (element instanceof TreeNode)
        {
            if (((TreeNode)element).getChildren().size() > 0)
                return true;
            else
                return false;
        }
        LOG.warn( "hasChildren: Unexpected type of node " + element.getClass() );

        return false;
    }
}
