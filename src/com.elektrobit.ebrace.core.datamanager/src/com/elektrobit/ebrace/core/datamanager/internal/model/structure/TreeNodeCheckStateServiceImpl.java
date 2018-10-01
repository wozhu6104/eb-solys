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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureModificationListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeModificationListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckStateListener;

@Component(service = {TreeNodeCheckStateService.class, ResetListener.class})
public class TreeNodeCheckStateServiceImpl
        implements
            TreeNodeCheckStateService,
            StructureModificationListener,
            TreeModificationListener,
            ResetListener
{
    private final Map<TreeNode, CHECKED_STATE> checkedNodes = Collections
            .synchronizedMap( new HashMap<TreeNode, TreeNodeCheckStateService.CHECKED_STATE>() );

    private final Set<TreeNodesCheckStateListener> listeners = new HashSet<TreeNodesCheckStateListener>();
    private StructureProvider structureProvider;

    public TreeNodeCheckStateServiceImpl()
    {
    }

    @Reference
    public void bind(StructureProvider structureProvider)
    {
        this.structureProvider = structureProvider;
    }

    public void unbind(StructureProvider structureProvider)
    {
        this.structureProvider = null;
    }

    @Activate
    public void activate()
    {
        register();
    }

    private void register()
    {
        structureProvider.addStructureModificationListener( this );
    }

    @Override
    public void toggleCheckState(TreeNode node)
    {
        CHECKED_STATE state = getNodeCheckState( node );
        switch (state)
        {
            case CHECKED :
            case PARTIALLY_CHECKED :
                updateCheckedStateDownwardsRecursively( node, CHECKED_STATE.UNCHECKED );
                break;
            case UNCHECKED :
                updateCheckedStateDownwardsRecursively( node, CHECKED_STATE.CHECKED );
                break;
        }
        TreeNode parent = node.getParent();
        if (parent != null)
        {
            updateCheckedStateUpwardsRecursively( parent );
        }
        notifyListeners();
    }

    private void setState(TreeNode node, CHECKED_STATE state)
    {
        if (state == CHECKED_STATE.UNCHECKED)
        {
            checkedNodes.remove( node );
        }
        else
        {
            checkedNodes.put( node, state );
        }
    }

    CHECKED_STATE getNodeCheckState(TreeNode node)
    {
        CHECKED_STATE state = checkedNodes.get( node );
        if (state == null)
        {
            return CHECKED_STATE.UNCHECKED;
        }
        else
        {
            return state;
        }
    }

    @Override
    public TreeNodesCheckState getCheckStates()
    {
        return new TreeNodesCheckStateImpl( this );
    }

    @Override
    public void added(TreeNode node)
    {
        setNodeStateAccordingToParent( node );
    }

    private void setNodeStateAccordingToParent(TreeNode node)
    {
        if (node.getParent() != null)
        {
            CHECKED_STATE parentState = getNodeCheckState( node.getParent() );
            if (parentState == CHECKED_STATE.CHECKED)
            {
                setState( node, CHECKED_STATE.CHECKED );
            }
        }
    }

    void updateCheckedStateUpwardsRecursively(TreeNode node)
    {
        setNodeStateAccordingToChildren( node );

        if (node.getParent() != null)
        {
            updateCheckedStateUpwardsRecursively( node.getParent() );
        }
    }

    private void setNodeStateAccordingToChildren(TreeNode node)
    {
        int fullyCheckedChildren = 0;
        int partiallyCheckedChildren = 0;
        for (TreeNode child : node.getChildren())
        {
            if (getNodeCheckState( child ) == CHECKED_STATE.CHECKED)
            {
                fullyCheckedChildren++;
            }
            else if (getNodeCheckState( child ) == CHECKED_STATE.PARTIALLY_CHECKED)
            {
                partiallyCheckedChildren++;
            }
        }

        int checkedAndPartiallyCheckedChildren = fullyCheckedChildren + partiallyCheckedChildren;

        if (checkedAndPartiallyCheckedChildren == 0)
        {
            setState( node, CHECKED_STATE.UNCHECKED );
        }
        else if (fullyCheckedChildren == node.getChildren().size())
        {
            setState( node, CHECKED_STATE.CHECKED );
        }
        else
        {
            setState( node, CHECKED_STATE.PARTIALLY_CHECKED );
        }
    }

    void updateCheckedStateDownwardsRecursively(TreeNode node, CHECKED_STATE state)
    {
        switch (state)
        {
            case CHECKED :
            case UNCHECKED :
                setState( node, state );
                for (TreeNode child : node.getChildren())
                {
                    updateCheckedStateDownwardsRecursively( child, state );
                }
                break;

            case PARTIALLY_CHECKED : {
                break;
            }
            default : {
                break;
            }
        }
    }

    @Override
    public void removed(TreeNode node)
    {
        checkedNodes.remove( node );
    }

    @Override
    public void changed(TreeNode node)
    {
    }

    @Override
    public void treeLevelDefAppended(TreeLevelDef appendedTreeLevelDef)
    {
    }

    @Override
    public void onTreeCreated(Tree tree)
    {
        tree.addTreeModificationListener( this );
    }

    @Override
    public void onTreeRemoved(Tree tree)
    {
        tree.removeTreeModificationListener( this );
        for (TreeNode node : tree.toList())
        {
            checkedNodes.remove( node );
        }
    }

    @Override
    public void registerListener(TreeNodesCheckStateListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public void unregisterListener(TreeNodesCheckStateListener listener)
    {
        listeners.remove( listener );
    }

    @Override
    public void checkTreeNodes(List<TreeNode> nodes)
    {
        for (TreeNode treeNode : nodes)
        {
            setState( treeNode, CHECKED_STATE.CHECKED );
            updateCheckedStateDownwardsRecursively( treeNode, CHECKED_STATE.CHECKED );
            TreeNode parent = treeNode.getParent();
            if (parent != null)
            {
                updateCheckedStateUpwardsRecursively( parent );
            }
        }
        notifyListeners();
    }

    private void notifyListeners()
    {
        for (TreeNodesCheckStateListener listener : listeners)
        {
            listener.onCheckStateChanged();
        }
    }

    @Override
    public void onReset()
    {
        checkedNodes.clear();
        notifyListeners();
    }
}
