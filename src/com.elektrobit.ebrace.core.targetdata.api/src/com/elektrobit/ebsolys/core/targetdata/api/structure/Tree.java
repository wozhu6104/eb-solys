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
 * A tree is part of the whole system structure. The system structure is {@link List} of {@link Tree}s. The entry point
 * of a {@link Tree} is the root node. Every {@link Tree} has a {@link TreeDef} annotated and it manages it's own
 * {@link TreeModificationListener}. So every change on the tree could easily get monitored with a
 * {@link TreeModificationListener}.
 * 
 * @author rage2903
 * @version 11.06
 */
public interface Tree
{
    /**
     * Returns the name of the {@link Tree}. Every {@link Tree} must have a unique name.
     * 
     * @return the unique name of the {@link Tree}.
     */
    String getName();

    /**
     * Returns the description of the {@link Tree}.
     * 
     * @return the description of the {@link Tree}.
     */
    String getDescription();

    /**
     * Returns the root node of the {@link Tree}.
     * 
     * @return Returns the root node of the {@link Tree}.
     */
    TreeNode getRootNode();

    /**
     * Returns the {@link Tree} as {@link List}. The ordering of the list is implementation dependent. Preferred
     * ordering is pre-ordering.
     * 
     * @return Returns the {@link Tree} as {@link List}
     */
    List<TreeNode> toList();

    /**
     * Returns the {@link TreeDef} of this {@link Tree}.
     * 
     * @return Returns the {@link TreeDef} of this {@link Tree}.
     */
    TreeDef getTreeDef();

    /**
     * Returns a {@link List} of {@link TreeNode}s with the given {@link TreeLevelDef}.
     * 
     * @param level
     *            The {@link TreeLevelDef} that should the returned {@link TreeNode}s have.
     * @return Returns a {@link List} of {@link TreeNode}s with the given {@link TreeLevelDef}.
     */
    List<TreeNode> getTreeNodesForTreeLevelDef(TreeLevelDef level);

    /**
     * Adds a {@link TreeModificationListener} to the {@link Tree}, which gets informed about every structural change on
     * this {@link Tree}.
     * 
     * @param listener
     *            The {@link TreeModificationListener} that wants get informed.
     * @return true, if the {@link TreeModificationListener} could be added, else false.
     */
    boolean addTreeModificationListener(TreeModificationListener listener);

    /**
     * Removes the given {@link TreeModificationListener} from the notification list.
     * 
     * @param listener
     *            The {@link TreeModificationListener} that doesn't want get informed anymore.
     */
    void removeTreeModificationListener(TreeModificationListener listener);
}
