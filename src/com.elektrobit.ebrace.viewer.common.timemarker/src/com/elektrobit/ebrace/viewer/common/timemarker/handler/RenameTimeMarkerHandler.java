/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.viewer.common.timemarker.views.TimeMarkersView;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class RenameTimeMarkerHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection( event );
        IWorkbenchPart activePart = HandlerUtil.getActivePart( event );
        if (activePart instanceof ITableViewerView && currentSelection.getFirstElement() instanceof TimeMarker)
        {
            ITableViewerView activeView = (ITableViewerView)activePart;
            TimeMarker timemarker = (TimeMarker)currentSelection.getFirstElement();
            if (activeView instanceof TimeMarkersView)
            {
                activeView.getTreeViewer().editElement( timemarker, 0 );
            }
            else
            {
                activeView.getTreeViewer().editElement( timemarker, 1 );
            }
        }
        return null;
    }
}
