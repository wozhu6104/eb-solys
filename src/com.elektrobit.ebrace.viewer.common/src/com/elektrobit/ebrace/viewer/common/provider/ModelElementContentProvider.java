/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * Content Provider which returns the list with registered runtime event channels. If we have a treeview or a table
 * viewer which has to show the runtime event channels this is the content provider to use.
 */

public class ModelElementContentProvider implements ITreeContentProvider
{

    @Override
    public void dispose()
    {
        // nothing to dispose.

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // nothing to do.

    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return getChildren( inputElement );
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof StructureAcceptor)
        {
            List<TreeNode> listOfTreeNodesOfAllTrees = new ArrayList<TreeNode>();
            for (Tree t : ((StructureAcceptor)parentElement).getTrees())
            {
                listOfTreeNodesOfAllTrees.add( t.getRootNode() );
            }
            return listOfTreeNodesOfAllTrees.toArray();
        }
        else if (parentElement instanceof Tree)
        {
            return ((Tree)parentElement).getRootNode().getChildren().toArray();
        }
        else if (parentElement instanceof TreeNode)
        {
            return ((TreeNode)parentElement).getChildren().toArray();
        }
        else if (parentElement instanceof ComRelationAcceptor)
        {
            CopyOnWriteArrayList<ModelElement> comRels = new CopyOnWriteArrayList<ModelElement>();
            comRels.addAll( ((ComRelationAcceptor)parentElement).getComRelations() );
            return comRels.toArray();

        }
        return Collections.emptyList().toArray();
    }

    @Override
    public Object getParent(Object element)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        return (element instanceof Tree && !((Tree)element).getRootNode().getChildren().isEmpty())
                || (element instanceof TreeNode && !((TreeNode)element).getChildren().isEmpty());
    }

}
