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

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * This class is called from the ZEST GraphViewer to generate the ZEST Graph.
 */
public class GraphContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider
{
    private final ComRelationProvider comRelationProvider = new GenericOSGIServiceTracker<ComRelationProvider>( ComRelationProvider.class )
            .getService();

    @Override
    public Object[] getConnectedTo(Object entity)
    {
        if (entity instanceof TreeNode)
        {
            TreeNode node = (TreeNode)entity;
            List<TreeNode> connectedReceivers = comRelationProvider.getConnectedReceivers( node );
            return connectedReceivers.toArray();
        }
        throw new RuntimeException( "Type not supported " + entity.getClass() );
    }

}
