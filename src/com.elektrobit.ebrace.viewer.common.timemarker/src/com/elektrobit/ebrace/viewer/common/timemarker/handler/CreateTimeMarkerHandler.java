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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class CreateTimeMarkerHandler extends AbstractHandler
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        List<RuntimeEvent<?>> selectedRuntimeEvents = getSelectedRuntimeEvents( event );
        TimeMarker lastTimeMarker = null;
        for (RuntimeEvent<?> rtEvent : selectedRuntimeEvents)
        {
            lastTimeMarker = timeMarkerManager.createNewTimeMarker( rtEvent.getTimestamp() );
        }
        if (lastTimeMarker != null)
        {
            timeMarkerManager.setCurrentSelectedTimeMarker( lastTimeMarker );
        }
        return null;
    }

    private List<RuntimeEvent<?>> getSelectedRuntimeEvents(ExecutionEvent event)
    {
        List<RuntimeEvent<?>> selectedRuntimeEvents = new ArrayList<RuntimeEvent<?>>();
        StructuredSelection selection = getSelection( event );
        for (Object o : selection.toList())
        {
            if (o instanceof RuntimeEvent<?>)
            {
                selectedRuntimeEvents.add( (RuntimeEvent<?>)o );
            }
        }
        return selectedRuntimeEvents;
    }

    private StructuredSelection getSelection(ExecutionEvent event)
    {
        IWorkbenchPart part = HandlerUtil.getActivePart( event );
        StructuredSelection selection = null;
        if (part instanceof ITableViewerView)
        {
            selection = (StructuredSelection)((ITableViewerView)part).getTreeViewer().getSelection();
        }
        else
        {
            selection = (StructuredSelection)HandlerUtil.getCurrentSelection( event );
        }
        return selection;
    }
}
