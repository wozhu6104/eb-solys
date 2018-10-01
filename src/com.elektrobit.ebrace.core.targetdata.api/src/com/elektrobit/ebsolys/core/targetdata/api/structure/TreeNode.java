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

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;

/**
 * The core element of the data view interface is the <code>TreeNode</code>. It represents a node in a hierarchical
 * structure of the system. An <code>TreeNode</code> could represent a whole system, an application or just a component
 * for example. To traverse the whole tree or just a subtree of the structure, you can use the
 * <code>DataViewStructureProvider</code> interface. The identity of an <code>TreeNode</code> is defined by its path
 * which consists of the names of all its parents and its own name separated by a "/" slash character.
 * 
 * @author rage2903
 * @version 12.06
 */
public interface TreeNode extends ModelElement
{
    /**
     * Check's if the TreeNode is the root node of the whole tree. Root node means that this node does not have a parent
     * node.
     * 
     * @return true, if it is a root node of the hierarchical structure of the system. If not it returns false.
     */
    boolean isRoot();

    /**
     * Check's if the TreeNode is the a leaf node of the whole tree. Leaf node means that this node does not have any
     * children.
     * 
     * @return true, if it is a leaf node of the hierarchical structure of the system. If not it returns false.
     */
    boolean isLeaf();

    /**
     * Returns the TreeLevel of the TreeNode. "System", "bundle", "application", "component" could be examples for
     * types.
     * 
     * @return Returns the name of the ITreeNode.
     */
    TreeLevelDef getTreeLevel();

    /**
     * Returns the parent {@link TreeNode} of this node. Returns null if this {@link TreeNode} is the root node of the
     * {@link Tree}.
     * 
     * @return the parent {@link TreeNode} of this node.
     */
    TreeNode getParent();

    /**
     * Returns the children of this {@link TreeNode} as a {@link List}. If this children {@link List} is empty, this
     * {@link TreeNode} is a leaf node.
     * 
     * @return the children of this {@link TreeNode} as a {@link List}.
     */
    List<TreeNode> getChildren();

    /**
     * Returns the {@link Properties} of this {@link TreeNode}.
     * 
     * @return the {@link Properties} of this {@link TreeNode}.
     */
    Properties getProperties();

    /** Returns the tree to which this tree node belongs to. */
    public Tree getTree();
}
