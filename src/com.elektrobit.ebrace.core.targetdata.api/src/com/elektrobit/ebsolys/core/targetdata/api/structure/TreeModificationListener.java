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

/**
 * A standard listener that want get informed if any structural modification is done on a {@link Tree}. Add the
 * implementation of this interface to the {@link Tree} with the method
 * {@link Tree#addTreeModificationListener(TreeModificationListener)}, if you want to get informed about structural
 * modifications. If you don't want to get informed about structural modifications, call
 * {@link Tree#removeTreeModificationListener(TreeModificationListener)}.
 */
public interface TreeModificationListener
{
    /**
     * This method is called if a {@link TreeNode} was added to the {@link Tree} at which this listener is attached to.
     * 
     * @param node
     *            The {@link TreeNode} which was added to the {@link Tree}.
     */
    void added(TreeNode node);

    /**
     * This method is called if a {@link TreeNode} was removed from the {@link Tree} at which this listener is attached
     * to.
     * 
     * @param node
     *            The {@link TreeNode} which was removed from the {@link Tree}.
     */
    void removed(TreeNode node);

    /**
     * This method is called if a {@link TreeNode} was changed.
     * 
     * @param node
     *            The {@link TreeNode} which was changed.
     */
    void changed(TreeNode node);

    /**
     * This method is called if a {@link TreeLevelDef} was appended to the tree.
     * 
     * @param appendedTreeLevelDef
     *            The {@link TreeLevelDef} which was appended.
     */
    void treeLevelDefAppended(TreeLevelDef appendedTreeLevelDef);
}
