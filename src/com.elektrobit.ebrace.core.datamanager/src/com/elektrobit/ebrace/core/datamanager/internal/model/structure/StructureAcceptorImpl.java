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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.datamanager.internal.PropertiesImpl;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElementPool;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureModificationListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * Implements the {@link StructureAcceptor}. This class is running as OSGi service and should only exists once in the
 * whole system.
 * 
 * @author rage2903
 * @version 11.07
 * @see {@link StructureProvider}
 */
@Component
public class StructureAcceptorImpl implements StructureAcceptor, StructureProvider, ClearChunkDataListener
{
    private static final Logger S_LOGGER = Logger.getLogger( StructureAcceptorImpl.class );

    private final List<Tree> m_treeList = Collections.synchronizedList( new ArrayList<Tree>() );
    private final List<StructureModificationListener> m_structureModificationListener = new CopyOnWriteArrayList<StructureModificationListener>();
    private ModelElementPool modelElementPool;

    public StructureAcceptorImpl()
    {
    }

    @Reference
    public void setModelElementPool(ModelElementPool modelElementPool)
    {
        this.modelElementPool = modelElementPool;
    }

    public void unsetModelElementPool(ModelElementPool modelElementPool)
    {
        this.modelElementPool = null;
    }

    @Override
    public List<Tree> getTrees()
    {
        return m_treeList;
    }

    @Override
    public TreeLevelDef createTreeLevel(String name, String description, String pathToIcon)
    {
        return new TreeLevelDefImpl( name, description, pathToIcon );
    }

    @Override
    public boolean appendTreeLevelDef(Tree tree, TreeLevelDef newLevel)
    {
        if (tree == null)
        {
            S_LOGGER.warn( "Couldn't append a TreeLevel on a Tree which is null." );
            return false;
        }

        if (newLevel == null)
        {
            S_LOGGER.warn( "Couldn't append a TreeLevel which is null." );
            return false;
        }

        return ((TreeImpl)tree).appendTreeLevelDef( newLevel );
    }

    @Override
    public Tree addNewTreeInstance(String nameOfTree, String descriptionOfTree, String nameOfRootNode,
            List<TreeLevelDef> treeLevelDefinition)
    {
        TreeDefImpl treeDef = new TreeDefImpl( treeLevelDefinition );
        TreeImpl newTree = new TreeImpl( nameOfTree, descriptionOfTree, nameOfRootNode, treeDef );
        m_treeList.add( newTree );
        notifyTreeAdding( newTree );
        return newTree;
    }

    @Override
    public void removeTree(Tree tree)
    {
        m_treeList.remove( tree );
        notifyTreeRemoving( tree );
    }

    @Override
    public TreeNode addTreeNode(TreeNode parent, String nameOfChildNode)
    {
        if (!(parent instanceof TreeNodeImpl))
        {
            throw new RuntimeException( "Type of TreeNode is null or not TreeNodeImpl. " );
        }

        TreeNodeImpl parentInstance = (TreeNodeImpl)parent;
        if (!isTreeLevelAvailable( parentInstance ))
        {
            throw new RuntimeException( "Couldn't add node, because no tree level was defined for this tree level!" );
        }

        TreeNodeImpl newTreeNode = new TreeNodeImpl( nameOfChildNode, parentInstance.getTree() );

        modelElementPool.insertModelElement( newTreeNode );

        return parentInstance.addTreeNode( newTreeNode );
    }

    @Override
    public void removeTreeNode(TreeNode node)
    {
        if (!(node instanceof TreeNodeImpl))
        {
            throw new RuntimeException( "Type of TreeNode is not TreeNodeImplNew. " );
        }

        TreeNodeImpl nodeInstance = (TreeNodeImpl)node;
        nodeInstance.removeTreeNode();

    }

    @Override
    public void addStructureModificationListener(StructureModificationListener listener)
    {
        m_structureModificationListener.add( listener );
    }

    @Override
    public void removeStructureModificationListener(StructureModificationListener listener)
    {
        m_structureModificationListener.remove( listener );
    }

    @Override
    public boolean addStructureProperty(TreeNode node, Object key, Object value, String description)
    {
        if (node == null)
        {
            S_LOGGER.warn( "Couldn't add structure properties for a TreeNode which is null!" );
            return false;
        }

        if (!(node instanceof TreeNodeImpl))
        {
            S_LOGGER.error( "Couldn't add structure properties for a TreeNode which isn't instance of TreeNodeImplNew!" );
            return false;
        }

        TreeNodeImpl nodeImpl = (TreeNodeImpl)node;
        if (nodeImpl.getProperties().getKeys().contains( key ))
        {
            S_LOGGER.warn( "Couldn't not add a properties key, which is already there!" );
            return false;
        }

        nodeImpl.setProperty( key, value, description );
        return true;
    }

    @Override
    public boolean changeStructureProperty(TreeNode node, Object key, Object value, String description)
    {
        if (node == null)
        {
            S_LOGGER.warn( "Couldn't add structure properties for a TreeNode which is null!" );
            return false;
        }

        if (!(node instanceof TreeNodeImpl))
        {
            S_LOGGER.error( "Couldn't add structure properties for a TreeNode which isn't instance of TreeNodeImplNew!" );
            return false;
        }

        TreeNodeImpl nodeImpl = (TreeNodeImpl)node;
        if (!(nodeImpl.getProperties().getKeys().contains( key )))
        {
            S_LOGGER.warn( "Couldn't not change a property, which isn't there!" );
            return false;
        }
        nodeImpl.setProperty( key, value, description );

        return true;
    }

    /**
     * Checks if there is a {@link TreeLevelDef} for a new {@link TreeNodeImpl} available.
     * 
     * @param parent
     *            The {@link TreeNodeImpl} which the new child node should be added.
     * @return true, if a {@link TreeLevelDef} is available, else false.
     */
    private boolean isTreeLevelAvailable(TreeNodeImpl parent)
    {
        int indexOfParentTreeLevelDef = parent.getTree().getTreeDef().getTreeLevelDefs()
                .indexOf( parent.getTreeLevel() );
        int numberOfTreeLevelDefs = parent.getTree().getTreeDef().getTreeLevelDefs().size();

        if (indexOfParentTreeLevelDef >= (numberOfTreeLevelDefs - 1))
        {
            return false;
        }

        return true;
    }

    /**
     * Notifies every {@link StructureModificationListener} that a {@link Tree} was added.
     * 
     * @param newTree
     *            the new {@link Tree} which was added-
     */
    private void notifyTreeAdding(Tree newTree)
    {
        for (StructureModificationListener listener : m_structureModificationListener)
        {
            listener.onTreeCreated( newTree );
        }
    }

    /**
     * Notifies every {@link StructureModificationListener} that a {@link Tree} was removed.
     * 
     * @param newTree
     *            the new {@link Tree} which was added-
     */
    private void notifyTreeRemoving(Tree removedTree)
    {
        for (StructureModificationListener listener : m_structureModificationListener)
        {
            listener.onTreeRemoved( removedTree );
        }
    }

    @Override
    public void changeNameOfTreeNode(TreeNode node, String newTreeNodeName)
    {
        if (node != null && node instanceof TreeNodeImpl)
        {
            ((TreeNodeImpl)node).changeName( newTreeNodeName );
        }
    }

    @Override
    public void removeProperty(TreeNode node, Object key)
    {
        if (node == null)
        {
            S_LOGGER.warn( "Couldn't remove property of TreeNode, which is null!" );
            return;
        }
        if (key == null)
        {
            S_LOGGER.warn( "Couldn't remove property with key, which is null!" );
            return;
        }
        if (!(node instanceof TreeNodeImpl))
        {
            S_LOGGER.error( "Couldn't remove properties for a TreeNode which isn't instance of TreeNodeImpl!" );
            return;
        }
        PropertiesImpl properties = (PropertiesImpl)node.getProperties();
        properties.remove( key );
    }

    @Override
    public void onClearChunkData()
    {
        for (int i = 0; i < m_treeList.size(); i++)
        {
            Tree nextTree = m_treeList.get( i );
            removeTree( nextTree );
        }
        m_treeList.clear();
        modelElementPool.performReset();
    }
}
