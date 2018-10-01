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
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class SelectStructureInteractionUseCaseImpl implements SelectStructureInteractionUseCase
{
    private final StructureSelectionService structureSelectionService;

    public SelectStructureInteractionUseCaseImpl(SelectStructureInteractionCallback callback,
            StructureSelectionService structureSelectionService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "structureSelectionService", structureSelectionService );
        this.structureSelectionService = structureSelectionService;
    }

    @Override
    public void setNodesSelected(List<TreeNode> nodes)
    {
        structureSelectionService.setNodesSelected( nodes );
    }

    @Override
    public void setComRelationsSelected(List<ComRelation> comRelations)
    {
        structureSelectionService.setComRelationsSelected( comRelations );
    }

    @Override
    public void clearSelection()
    {
        structureSelectionService.clearSelection();
    }

    @Override
    public void unregister()
    {
    }
}
