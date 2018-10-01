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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.IEvaluationService;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class ToggleVisibilityHandler extends AbstractHandler
{
    GenericOSGIServiceTracker<TimeMarkerManager> timeMarkerManagerTracker = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection currentSelection = (IStructuredSelection)HandlerUtil.getCurrentSelection( event );
        Iterator<?> iterator = currentSelection.iterator();
        while (iterator.hasNext())
        {
            Object object = iterator.next();
            if (object instanceof TimeMarker)
            {
                TimeMarker timeMarker = (TimeMarker)object;
                timeMarkerManagerTracker.getService().toggleVisibility( timeMarker );
            }
        }
        IEvaluationService evServ = (IEvaluationService)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService( IEvaluationService.class );
        evServ.requestEvaluation( "com.elektrobit.ebrace.viewer.common.timemarker.isTimeMarkerVisible.isTimeMarkerVisible" );

        return null;
    }
}
