/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.structure;

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;

public interface TreeNodesCheckState
{
    public CHECKED_STATE getNodeCheckState(TreeNode node);
}
