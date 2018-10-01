/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview.treemodel;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;

public class ChannelLazyTreeContentProvider implements ILazyTreeContentProvider
{
    private final TreeViewer viewer;

    public ChannelLazyTreeContentProvider(TreeViewer viewer)
    {
        this.viewer = viewer;
    }

    @Override
    public Object getParent(Object element)
    {
        Object result = null;
        if (element != null)
        {
            ChannelTreeNode node = (ChannelTreeNode)element;
            result = node.getParent();
        }

        return result;
    }

    @Override
    public void updateChildCount(Object element, int currentChildCount)
    {
        ChannelTreeNode node = (ChannelTreeNode)element;
        viewer.setChildCount( node, node.getChildCount() );
    }

    @Override
    public void updateElement(Object parent, int index)
    {
        ChannelTreeNode parentNode = (ChannelTreeNode)parent;
        ChannelTreeNode childNode = parentNode.getChildAt( index );

        viewer.replace( parentNode, index, childNode );
        viewer.setChildCount( childNode, childNode.getChildCount() );
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    @Override
    public void dispose()
    {

    }
}
