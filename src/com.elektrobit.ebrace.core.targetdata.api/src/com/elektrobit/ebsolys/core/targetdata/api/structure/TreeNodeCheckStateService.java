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

import java.util.List;

public interface TreeNodeCheckStateService
{
    public enum CHECKED_STATE {
        CHECKED, UNCHECKED, PARTIALLY_CHECKED
    };

    public void toggleCheckState(TreeNode node);

    public void checkTreeNodes(List<TreeNode> nodes);

    public TreeNodesCheckState getCheckStates();

    public void registerListener(TreeNodesCheckStateListener listener);

    public void unregisterListener(TreeNodesCheckStateListener listener);
}
