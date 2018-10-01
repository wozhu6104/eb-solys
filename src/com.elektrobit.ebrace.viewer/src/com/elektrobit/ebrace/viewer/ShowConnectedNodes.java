/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.structure.CheckConnectedNodesCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.CheckConnectedNodesInteractionUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class ShowConnectedNodes extends AbstractHandler implements CheckConnectedNodesCallback
{
    private final CheckConnectedNodesInteractionUseCase useCase = UseCaseFactoryInstance.get()
            .makeCheckConnectedNodesInteractionUseCase( this );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        List<TreeNode> selectedTreeNodes = getSelectedTreeNodes( event );
        for (TreeNode treeNode : selectedTreeNodes)
        {
            useCase.checkConnectedNodes( treeNode );
        }

        return null;
    }

    private List<TreeNode> getSelectedTreeNodes(ExecutionEvent event)
    {
        List<TreeNode> resultViewerNodes = new ArrayList<TreeNode>();
        IStructuredSelection selection = (IStructuredSelection)HandlerUtil.getCurrentSelection( event );
        for (Object nextSelectedViewerNodeObject : selection.toList())
        {
            if (nextSelectedViewerNodeObject instanceof TreeNode)
            {
                TreeNode selectedViewerNode = (TreeNode)nextSelectedViewerNodeObject;
                resultViewerNodes.add( selectedViewerNode );
            }
        }
        return resultViewerNodes;
    }
}
