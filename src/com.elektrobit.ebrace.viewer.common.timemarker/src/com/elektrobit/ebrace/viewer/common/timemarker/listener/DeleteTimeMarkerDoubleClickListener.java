/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.listener;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

public class DeleteTimeMarkerDoubleClickListener implements IDoubleClickListener
{

    GenericOSGIServiceTracker<TimeMarkerManager> timeMarkerManagerTracker = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class );

    @Override
    public void doubleClick(DoubleClickEvent event)
    {
        StructuredSelection selection = (StructuredSelection)event.getSelection();
        if (selection.getFirstElement() instanceof TimeMarker)
        {
            TimeMarker timeMarker = (TimeMarker)selection.getFirstElement();
            timeMarkerManagerTracker.getService().toggleVisibility( timeMarker );
        }
    }

}
