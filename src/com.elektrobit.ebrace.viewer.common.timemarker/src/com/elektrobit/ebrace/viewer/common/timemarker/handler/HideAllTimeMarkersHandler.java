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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class HideAllTimeMarkersHandler extends AbstractHandler

{
    private final GenericOSGIServiceTracker<TimeMarkerManager> timeMarkerManagerServiceTracker = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        timeMarkerManagerServiceTracker.getService().toggleVisibility();
        ICommandService commandService = (ICommandService)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService( ICommandService.class );
        commandService.refreshElements( event.getCommand().getId(), null );
        return null;
    }
}
