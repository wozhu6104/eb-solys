/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.viewer.resources.ResourceExplorerView;

public class RenameResourceHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection( event );
        IWorkbenchPart activePart = HandlerUtil.getActivePart( event );

        if (activePart instanceof ResourceExplorerView)
        {
            ResourceExplorerView activeView = (ResourceExplorerView)activePart;
            boolean isChart = currentSelection.getFirstElement() instanceof ChartModel;
            boolean isTable = currentSelection.getFirstElement() instanceof TableModel;
            boolean isSnapshot = currentSelection.getFirstElement() instanceof SnapshotModel;
            boolean isTimelineView = currentSelection.getFirstElement() instanceof TimelineViewModel;
            if (isChart || isTable || isSnapshot || isTimelineView)
            {
                activeView.editElement( currentSelection.getFirstElement(), 0 );
            }
        }
        return null;
    }

}
