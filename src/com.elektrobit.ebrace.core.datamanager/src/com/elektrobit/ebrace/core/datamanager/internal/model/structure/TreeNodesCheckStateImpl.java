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

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;

public class TreeNodesCheckStateImpl implements TreeNodesCheckState
{
    private final TreeNodeCheckStateServiceImpl service;

    public TreeNodesCheckStateImpl(TreeNodeCheckStateServiceImpl service)
    {
        this.service = service;
    }

    @Override
    public CHECKED_STATE getNodeCheckState(TreeNode node)
    {
        return service.getNodeCheckState( node );
    }

}
