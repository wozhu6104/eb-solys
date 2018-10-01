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

/**
 * This interface represents tree definition of a {@link Tree}. A tree definition is a list of {@link TreeLevelDef}s.
 * Every layer in the {@link Tree} has exactly one {@link TreeLevelDef}. A {@link TreeLevelDef} is a kind of type of the
 * {@link TreeNode}s in a certain layer of the {@link Tree}.
 */
public interface TreeDef
{
    /**
     * Returns a list of {@link TreeLevelDef}. The first {@link TreeLevelDef} is the {@link TreeLevelDef} of the root
     * node. The second {@link TreeLevelDef} is the {@link TreeLevelDef} of the children of the root node. And so on...
     * Every layer of the {@link Tree} must exactly have one {@link TreeLevelDef}.
     * 
     * @return Returns a list of {@link TreeLevelDef}
     */
    List<TreeLevelDef> getTreeLevelDefs();
}
