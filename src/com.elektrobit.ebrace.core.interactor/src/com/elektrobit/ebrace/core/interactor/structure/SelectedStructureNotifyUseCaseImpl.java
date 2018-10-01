/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.structure;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class SelectedStructureNotifyUseCaseImpl implements SelectedStructureNotifyUseCase, StructureSelectionListener
{
    private SelectedStructureCallback callback;
    private final StructureSelectionService structureSelectionService;
    private Object currentSelection = null;

    public SelectedStructureNotifyUseCaseImpl(SelectedStructureCallback callback,
            StructureSelectionService structureSelectionService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "structureSelectionService", structureSelectionService );
        this.callback = callback;
        this.structureSelectionService = structureSelectionService;

        structureSelectionService.registerListener( this );
        structureSelectionService.notifyListenerWithCurrentState( this );
    }

    @Override
    public void onNodesSelected(List<TreeNode> nodes)
    {
        if (notAlreadySelected( nodes ))
        {
            currentSelection = nodes;
            callback.onNodesSelected( nodes );
        }
    }

    @Override
    public void onComRelationsSelected(List<ComRelation> comRelations)
    {
        if (notAlreadySelected( comRelations ))
        {
            currentSelection = comRelations;
            callback.onComRelationsSelected( comRelations );
        }
    }

    private boolean notAlreadySelected(Object newSelection)
    {
        return currentSelection == null || !currentSelection.equals( newSelection );
    }

    @Override
    public void onSelectionCleared()
    {
        if (currentSelection != null)
        {
            currentSelection = null;
            callback.onSelectionCleared();
        }
    }

    @Override
    public void unregister()
    {
        structureSelectionService.unregisterListener( this );
        callback = null;
    }
}
