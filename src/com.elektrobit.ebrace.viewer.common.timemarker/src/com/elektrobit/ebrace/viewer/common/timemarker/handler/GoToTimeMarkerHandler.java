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
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class GoToTimeMarkerHandler extends AbstractHandler
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection( event );
        if (currentSelection.getFirstElement() instanceof TimeMarker)
        {
            TimeMarker timeMarker = (TimeMarker)currentSelection.getFirstElement();
            timeMarkerManager.setCurrentSelectedTimeMarker( timeMarker );
        }
        return null;
    }
}
