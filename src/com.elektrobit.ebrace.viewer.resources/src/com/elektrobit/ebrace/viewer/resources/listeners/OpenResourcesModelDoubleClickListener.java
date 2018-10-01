/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.listeners;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTree;
import com.elektrobit.ebrace.viewer.resources.handler.OpenRaceResourceHandler;

public class OpenResourcesModelDoubleClickListener implements IDoubleClickListener
{
    private final OpenRaceResourceHandler openHandler;
    private final CommonFilteredTree commonTree;

    public OpenResourcesModelDoubleClickListener(OpenRaceResourceHandler openResourceHandler,
            CommonFilteredTree filterTree)
    {
        this.openHandler = openResourceHandler;
        this.commonTree = filterTree;
    }

    @Override
    public void doubleClick(DoubleClickEvent event)
    {
        StructuredSelection selection = (StructuredSelection)event.getSelection();
        Object selectedObj = selection.getFirstElement();

        if (selectedObj instanceof ResourceModel)
        {
            openHandler.openResource( (ResourceModel)selectedObj );
        }
        else
        {
            boolean treeState = commonTree.getViewer().getExpandedState( selectedObj );
            commonTree.getViewer().setExpandedState( selectedObj, !treeState );
        }
    }
}
