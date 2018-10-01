/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.structure;

import java.util.List;
import java.util.TimerTask;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureModificationListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeModificationListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckStateListener;

public class StructureNotifyUseCaseImpl
        implements
            StructureNotifyUseCase,
            StructureModificationListener,
            TreeModificationListener,
            TreeNodesCheckStateListener
{
    private static final int STRUCTURE_CHANGED_REFRESH_ACCUMULATION_TIME_MS = 3000;
    private StructureNotifyCallback callback;
    private final StructureProvider structureProvider;
    private final TreeNodeCheckStateService treeNodeCheckStateService;
    private volatile boolean refreshScheduled = false;

    public StructureNotifyUseCaseImpl(StructureNotifyCallback callback, StructureProvider structureProvider,
            TreeNodeCheckStateService treeNodeCheckStateService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "structureProvider", structureProvider );
        RangeCheckUtils.assertReferenceParameterNotNull( "treeNodeCheckStateService", treeNodeCheckStateService );
        this.callback = callback;
        this.structureProvider = structureProvider;
        this.treeNodeCheckStateService = treeNodeCheckStateService;

        register();
        collectAndPostData();
    }

    private void register()
    {
        treeNodeCheckStateService.registerListener( this );
        structureProvider.addStructureModificationListener( this );
        for (Tree tree : structureProvider.getTrees())
        {
            tree.addTreeModificationListener( this );
        }
    }

    private void collectAndPostData()
    {
        final List<Tree> allTrees = structureProvider.getTrees();
        final TreeNodesCheckState checkStates = treeNodeCheckStateService.getCheckStates();
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onStructureChanged( allTrees, checkStates );
            }
        } );
    }

    private void collectAndPostDataDelayed()
    {
        if (refreshScheduled)
            return;
        refreshScheduled = true;
        UseCaseExecutor.scheduleDelayed( new TimerTask()
        {

            @Override
            public void run()
            {
                collectAndPostData();
                refreshScheduled = false;
            }
        }, STRUCTURE_CHANGED_REFRESH_ACCUMULATION_TIME_MS );
    }

    @Override
    public void unregister()
    {
        treeNodeCheckStateService.unregisterListener( this );
        structureProvider.removeStructureModificationListener( this );

        for (Tree tree : structureProvider.getTrees())
        {
            tree.removeTreeModificationListener( this );
        }
        callback = null;
    }

    @Override
    public void added(TreeNode node)
    {
        collectAndPostDataDelayed();
    }

    @Override
    public void removed(TreeNode node)
    {
        collectAndPostDataDelayed();
    }

    @Override
    public void changed(TreeNode node)
    {
        collectAndPostDataDelayed();
    }

    @Override
    public void treeLevelDefAppended(TreeLevelDef appendedTreeLevelDef)
    {
    }

    @Override
    public void onTreeCreated(Tree tree)
    {
        tree.addTreeModificationListener( this );
        collectAndPostDataDelayed();
    }

    @Override
    public void onTreeRemoved(Tree tree)
    {
        tree.removeTreeModificationListener( this );
        collectAndPostDataDelayed();
    }

    @Override
    public void onCheckStateChanged()
    {
        collectAndPostData();
    }
}
