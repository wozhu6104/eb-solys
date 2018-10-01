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

import com.elektrobit.ebsolys.core.targetdata.api.Properties;

/**
 * 
 * This class is the counter part to {@link com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider} and
 * extends this class by the possibility to change the internal structure of the DataManager.
 * 
 * So while {@link com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider} can only read the internal
 * structure of the DataManager, this class can create new instances of a trees and modify existing trees.
 * 
 * 
 * @author rage2903
 * @version 11.05
 */
public interface StructureAcceptor extends StructureProvider
{
    /**
     * Creates a new instance of a tree in the internal structure of the data manager. <b>Important:</b> It's in the
     * responsibility of the developer to guarantee that the root node of a tree is only existing once in the system.
     * 
     * @param nameOfTree
     *            the name of the tree
     * @param descriptionOfTree
     *            the description of the tree
     * @param nameOfRootNode
     *            the name of the root node
     * @param iTreeLevelDefinition
     *            the tree level definition as <code>String[]</code>, whereby the first entry in the array refers to the
     *            highest level(= root node) and the last entry to the lowest level of the tree.
     * @return the root node of the created tree
     */
    Tree addNewTreeInstance(String nameOfTree, String descriptionOfTree, String nameOfRootNode,
            List<TreeLevelDef> treeLevelDefinition);

    /**
     * Removes the {@link Tree} from the structure.
     * 
     * @param tree
     *            The {@link Tree} which should be removed.
     */
    void removeTree(Tree tree);

    /**
     * Creates a new {@link TreeLevelDef} with the given parameters.
     * 
     * @param name
     *            the name of the {@link TreeLevelDef}
     * @param description
     *            the description of the {@link TreeLevelDef}
     * @param pathToIcon
     *            the path to the icon of the {@link TreeLevelDef}
     * 
     * @return a new instance of a {@link TreeLevelDef} with the given parameters.
     */
    TreeLevelDef createTreeLevel(String name, String description, String pathToIcon);

    /**
     * Adds a new {@link com.elektrobit.ebsolys.core.targetdata.api.structure.Tree} to a existing
     * {@link com.elektrobit.ebsolys.core.targetdata.api.structure.Tree} with the given name. <b>Important:</b> It's in
     * the responsibility of the developer to guarantee that a tree level definition exists for the new added node.
     * 
     * 
     * @param parent
     *            Parent {@link com.elektrobit.ebsolys.core.targetdata.api.structure.Tree} of the new added node.
     * @param nameOfChildNode
     *            Name of the new added node.
     * @return the added node
     */
    TreeNode addTreeNode(TreeNode parent, String nameOfChildNode);

    /**
     * Removes the given {@link TreeNode} from the {@link Tree}.
     * 
     * @param node
     *            The {@link TreeNode} which should be removed.
     */
    void removeTreeNode(TreeNode node);

    /**
     * Appends a tree level definition the given tree. It's not possible to add a tree level definition between two
     * existing tree level definitions. It's only possible to append a tree level definition at the end of the tree
     * level definition list.
     * 
     * @param tree
     *            The tree, which the new tree level is containing to.
     * @param newLevel
     *            The new TreeLevel
     * 
     * @return true, if appending was successful, else false.
     */
    boolean appendTreeLevelDef(Tree tree, TreeLevelDef newLevel);

    /**
     * Adds a {@link com.elektrobit.ebsolys.core.targetdata.api.Properties} object with the given key and the given
     * value to the given {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode} if the key wasn't
     * already there.
     * 
     * @param node
     *            The {@link com.elektrobit.ebsolys.core.targetdata.api.structure.Tree} which the given key-value-pair
     *            should be attached.
     * @param key
     *            The key of the {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}-Object.
     * @param value
     *            The value of the {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}-Object.
     * @param descritpion
     *            The description of this {@link Properties}-Object.
     * @return true, if {@link Properties}-Object was successfully added. If the key was already there the return value
     *         is false!
     * @see StructureAcceptor#changeStructureProperty(TreeNode, Object, Object)
     */
    boolean addStructureProperty(TreeNode node, Object key, Object value, String description);

    /**
     * Changes the {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}-Object with the given key and the given
     * value of the given {@link com.elektrobit.ebsolys.core.targetdata.api.structure.Tree}.
     * 
     * @param node
     *            The {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode} whose
     *            {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}-Object should be changed.
     * @param key
     *            The key of the {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}-Object.
     * @param value
     *            The value of the {@link Properties}-Object.
     * @param description
     *            The description of this {@link Properties}-Object.
     * @return true, if the {@link Properties}-Object was successfully changed. If the key was already there it returns
     *         false.
     */
    boolean changeStructureProperty(TreeNode node, Object key, Object value, String description);

    /**
     * Changes the name of the given TreeNode.
     * 
     * @param node
     *            The existing node whose name should be changed.
     * @param newTreeNodeName
     *            The new name of the TreeNode
     */
    void changeNameOfTreeNode(TreeNode node, String newTreeNodeName);

    /**
     * Removes the property with the given key
     * 
     * @param node
     *            The {@link TreeNode} which the property belongs to. Null value isn't allowed.
     * @param key
     *            The key of this property which should be removed. Null value isn't allowed.
     */
    void removeProperty(TreeNode node, Object key);
}
