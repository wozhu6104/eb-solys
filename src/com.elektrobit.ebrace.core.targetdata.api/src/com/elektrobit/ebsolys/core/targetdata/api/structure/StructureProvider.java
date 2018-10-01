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
 * This interface provides all information of the hierarchical structure of the underlying component model. The
 * structure is represented by a forest. A forest (graph theory) is a disjoint collection of trees.
 * 
 * The basic element of the tree is a {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode}.
 * 
 * With {@link #getRootNodes()} and {@link #getChildren(TreeNode)} you can traverse the whole forest. Every
 * {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode} has its own
 * {@link com.elektrobit.ebsolys.core.targetdata.api.Properties} which you can get with the method
 * {@link #getStructureProperties(TreeNode)}.
 */
public interface StructureProvider
{

    /**
     * Provides all available structure trees.
     * 
     * @return Returns all structure trees.
     */
    List<Tree> getTrees();

    /**
     * Adds an {@link StructureModificationListener} to the {@link StructureProvider} to get notified, if a
     * {@link TreeNode} is changed in the structure tree.
     * 
     * @param listener
     *            Implementation of the {@link StructureModificationListener}
     */
    void addStructureModificationListener(StructureModificationListener listener);

    /**
     * Removes a {@link StructureModificationListener} from the {@link StructureProvider} to not get notified anymore,
     * if a {@link TreeNode} is changed in the structure tree.
     * 
     * @param listener
     *            Implementation of the {@link StructureModificationListener}
     */
    void removeStructureModificationListener(StructureModificationListener listener);
}
