/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.model.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

@Component
public class StructureSelectionServiceImpl implements StructureSelectionService, ClearChunkDataListener
{
    private final Set<StructureSelectionListener> listeners = new HashSet<StructureSelectionListener>();
    private List<TreeNode> selectedNodes = new ArrayList<TreeNode>();
    private List<ComRelation> selectedComRelations = new ArrayList<ComRelation>();

    @Override
    public void setNodesSelected(List<TreeNode> nodes)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "selected nodes", nodes );
        selectedNodes = new ArrayList<TreeNode>( nodes );
        selectedComRelations.clear();
        notifyAllListeners();
    }

    @Override
    public void setComRelationsSelected(List<ComRelation> comRelations)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "selected comrelations", comRelations );
        selectedComRelations = new ArrayList<ComRelation>( comRelations );
        selectedNodes.clear();
        notifyAllListeners();
    }

    private void notifyAllListeners()
    {
        for (StructureSelectionListener listener : listeners)
        {
            notifyListener( listener );
        }
    }

    private void notifyListener(StructureSelectionListener listener)
    {
        if (!selectedNodes.isEmpty())
        {
            listener.onNodesSelected( getSelectedNodes() );
        }
        else if (!selectedComRelations.isEmpty())
        {
            listener.onComRelationsSelected( getSelectedComRelations() );
        }
        else
        {
            listener.onSelectionCleared();
        }
    }

    @Override
    public List<TreeNode> getSelectedNodes()
    {
        return Collections.unmodifiableList( selectedNodes );
    }

    @Override
    public List<ComRelation> getSelectedComRelations()
    {
        return Collections.unmodifiableList( selectedComRelations );
    }

    @Override
    public void clearSelection()
    {
        selectedComRelations.clear();;
        selectedNodes.clear();;
        notifyAllListeners();
    }

    @Override
    public void registerListener(StructureSelectionListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public void unregisterListener(StructureSelectionListener listener)
    {
        listeners.remove( listener );
    }

    @Override
    public void notifyListenerWithCurrentState(StructureSelectionListener listener)
    {
        notifyListener( listener );
    }

    @Override
    public void onClearChunkData()
    {
        clearSelection();
    }
}
