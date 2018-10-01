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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeModificationListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * This class is the implementation of the {@link Tree} interface.
 * 
 * @author rage2903
 * @version 11.07
 */
public class TreeImpl implements Tree, Serializable
{
    private static final long serialVersionUID = -1940077169901282332L;

    private final TreeNodeImpl m_rootNode;

    private final TreeDefImpl m_treeDef;

    private final String m_name;

    private final String m_description;

    transient private final Set<TreeModificationListener> m_treeModificationListener = new CopyOnWriteArraySet<TreeModificationListener>();

    /**
     * Standard c'tor.
     */
    public TreeImpl(String nameOfTree, String description, String nameOfRootNode, TreeDefImpl treeDef)
    {
        m_rootNode = new TreeNodeImpl( nameOfRootNode, this );
        m_name = nameOfTree;
        m_description = description;
        m_treeDef = treeDef;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public TreeNode getRootNode()
    {
        return m_rootNode;
    }

    @Override
    public String getDescription()
    {
        return m_description;
    }

    @Override
    public TreeDef getTreeDef()
    {
        return m_treeDef;
    }

    @Override
    public List<TreeNode> getTreeNodesForTreeLevelDef(TreeLevelDef level)
    {
        List<TreeNode> resultList = new ArrayList<TreeNode>();
        for (TreeNode node : toList())
        {
            if (node.getTreeLevel().equals( level ))
            {
                resultList.add( node );
            }
        }
        return resultList;
    }

    @Override
    public String toString()
    {
        return toList().toString() + getTreeDef().toString();
    }

    @Override
    public int hashCode()
    {
        int result = 17;
        result = 31 * result + toList().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof TreeImpl))
        {
            return false;
        }
        // TODO Check if TreeNodeImpl equals is implemented correct
        return this.toList().equals( ((TreeImpl)object).toList() );
    }

    /**
     * Returns the Tree as {@link List}. The elements are generated from a pre-order traversal of the Tree.
     * 
     * @return the Tree as {@link List}.
     */
    @Override
    public List<TreeNode> toList()
    {
        List<TreeNode> list = new ArrayList<TreeNode>();
        generatePreOrderedList( m_rootNode, list );
        return list;
    }

    /**
     * Divides a tree in a List by traversing the tree starting from the given node in pre-order style. The given list
     * should be empty. Else the traversed node will be append to the given List. In the end the given List will contain
     * the subtree as List in pre-order style.
     * 
     * @param startNode
     *            The root node of the tree.
     * @param list
     *            should be empty when the method is called and contains the tree as List in the end.
     */
    private void generatePreOrderedList(TreeNode startNode, List<TreeNode> list)
    {
        list.add( startNode );
        ArrayList<TreeNode> childrenCopy = new ArrayList<TreeNode>( startNode.getChildren() );
        for (TreeNode nextChild : childrenCopy)
        {
            generatePreOrderedList( nextChild, list );
        }
    }

    public boolean appendTreeLevelDef(TreeLevelDef newTreeLevelDef)
    {
        boolean successfullAdded = getTreeDef().getTreeLevelDefs().add( newTreeLevelDef );
        notifyAboutTreeLevelAppending( newTreeLevelDef );
        return successfullAdded;
    }

    @Override
    public boolean addTreeModificationListener(TreeModificationListener listener)
    {
        return m_treeModificationListener.add( listener );
    }

    @Override
    public void removeTreeModificationListener(TreeModificationListener listener)
    {
        m_treeModificationListener.remove( listener );
    }

    void notifyAboutTreeLevelAppending(TreeLevelDef appendedTreeLevelDef)
    {
        for (TreeModificationListener listener : m_treeModificationListener)
        {
            listener.treeLevelDefAppended( appendedTreeLevelDef );
        }
    }

    /**
     * Notifies all listeners if a node was added to the {@link Tree}
     * 
     * @param node
     */
    void notifyNodeAdded(TreeNode node)
    {
        for (TreeModificationListener listener : m_treeModificationListener)
        {
            listener.added( node );
        }
    }

    void notifyNodeChanged(TreeNode node)
    {
        for (TreeModificationListener listener : m_treeModificationListener)
        {
            listener.changed( node );
        }
    }

    /**
     * Notifies all listeners if a node was removed from the {@link Tree}
     * 
     * @param node
     */
    void notifyNodeRemoved(TreeNode node)
    {
        for (TreeModificationListener listener : m_treeModificationListener)
        {
            listener.removed( node );
        }
    }
}
