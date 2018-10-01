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
import com.elektrobit.ebrace.core.interactor.api.structure.CheckConnectedNodesCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.CheckConnectedNodesInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService;

public class CheckConnectedNodesInteractionUseCaseImpl implements CheckConnectedNodesInteractionUseCase
{
    private final TreeNodeCheckStateService treeNodeCheckStateService;
    private final ComRelationProvider comRelationProvider;

    public CheckConnectedNodesInteractionUseCaseImpl(CheckConnectedNodesCallback callback,
            ComRelationProvider comRelationProvider, TreeNodeCheckStateService treeNodeCheckStateService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "comRelationProvider", comRelationProvider );
        RangeCheckUtils.assertReferenceParameterNotNull( "treeNodeCheckStateService", treeNodeCheckStateService );

        this.comRelationProvider = comRelationProvider;
        this.treeNodeCheckStateService = treeNodeCheckStateService;
    }

    @Override
    public void unregister()
    {
    }

    @Override
    public void checkConnectedNodes(TreeNode treeNode)
    {
        List<TreeNode> connectedTreeNodes = comRelationProvider.getConnectedTreeNodes( treeNode );
        treeNodeCheckStateService.checkTreeNodes( connectedTreeNodes );
    }
}
