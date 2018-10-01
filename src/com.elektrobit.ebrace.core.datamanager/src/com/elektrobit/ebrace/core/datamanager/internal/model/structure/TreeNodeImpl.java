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

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.collections.GenericNode;
import com.elektrobit.ebrace.common.utils.IdNumberGenerator;
import com.elektrobit.ebrace.core.datamanager.internal.PropertiesImpl;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class TreeNodeImpl implements TreeNode, Serializable
{
    private static final long serialVersionUID = -3330350376407004815L;

    transient private static final Logger S_LOGGER = Logger.getLogger( TreeNodeImpl.class );

    private String name;

    private final Tree tree;

    private TreeNode parent;

    private final List<TreeNode> children = new ArrayList<TreeNode>();

    private PropertiesImpl m_properties = new PropertiesImpl( this );

    private final long uniqueModelElementID;

    private SystemdiffStatus status = null;

    /**
     * Constructs the TreeNode with the given content as data.
     * 
     * @param content
     *            The data of the node.
     */
    public TreeNodeImpl(String name, Tree tree)
    {
        this.name = name;
        this.tree = tree;
        this.uniqueModelElementID = IdNumberGenerator.getNextId( "ModelElement" );

    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public TreeLevelDef getTreeLevel()
    {
        int depth = getDepth();
        List<TreeLevelDef> treeLevels = tree.getTreeDef().getTreeLevelDefs();
        int sizeOfTreeDef = treeLevels.size();

        if ((sizeOfTreeDef - 1) < depth)
        {
            S_LOGGER.warn( "No tree level for TreeNode found. " );
            return null;
        }

        return treeLevels.get( depth );
    }

    /**
     * Returns the depth of the {@link TreeNodeImpl} in the {@link Tree}.
     * 
     * @return the depth of the {@link TreeNodeImpl} in the {@link Tree}.
     */
    private int getDepth()
    {
        int count = 0;
        TreeNode parent = this;
        while (!parent.isRoot())
        {
            count++;
            parent = parent.getParent();
        }
        return count;
    }

    /**
     * Returns the {@link Tree} this {@link TreeNodeImpl} contains to.
     * 
     * @return the {@link Tree} this {@link TreeNodeImpl} contains to.
     */
    @Override
    public Tree getTree()
    {
        return tree;
    }

    @Override
    public Properties getProperties()
    {
        return m_properties;
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    public void setParent(TreeNodeImpl parent)
    {
        this.parent = parent;
    }

    @Override
    public List<TreeNode> getChildren()
    {
        return children;
    }

    /**
     * Adds a new {@link GenericNode} to the internal node.
     * 
     * @param content
     *            The content of the new node as {@link TreeNodeContent}.
     */
    public TreeNode addTreeNode(TreeNodeImpl child)
    {
        addTreeNodeWithoutNotification( child );
        ((TreeImpl)tree).notifyNodeAdded( child );
        return child;
    }

    private void addTreeNodeWithoutNotification(TreeNodeImpl child)
    {
        children.add( child );
        child.setParent( this );
    }

    /**
     * Remove this {@link TreeNode} from the {@link Tree}
     */
    public void removeTreeNode()
    {

        // TODO Check if remove childList elements necessary
        if (parent != null)
        {
            parent.getChildren().remove( this );
            parent = null;
        }

        m_properties = null;
        children.clear();
        ((TreeImpl)tree).notifyNodeRemoved( this );
    }

    /**
     * Sets the property of the node.
     */
    public void setProperty(Object key, Object value, String description)
    {
        m_properties.put( key, value, description );
    }

    public void changeName(String newTreeNodeName)
    {
        setName( newTreeNodeName );
        ((TreeImpl)tree).notifyNodeChanged( this );
    }

    @Override
    public long getUniqueModelElementID()
    {
        return uniqueModelElementID;
    }

    public SystemdiffStatus getStatus()
    {
        return status;
    }

    public void setStatus(SystemdiffStatus status)
    {
        this.status = status;
    }

    @Override
    public boolean isRoot()
    {
        if (parent == null)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLeaf()
    {
        if (children.isEmpty())
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return getParentNameRecursive( this );
    }

    public String getParentNameRecursive(TreeNode parent)
    {
        if (parent == null)
        {
            return "";
        }
        else
        {
            return getParentNameRecursive( parent.getParent() ) + "#" + parent.getName();
        }
    }
}
