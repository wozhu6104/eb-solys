/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventTimstampTransfer;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;

/** Drop target adapter for the implementation for the drag and drop functionality for runtime event channels. */
public class RuntimeeventTimestampDropTargetAdapter extends DropTargetAdapter
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();
    private final List<Long> timestamps = new ArrayList<Long>();

    protected final UserInteractionPreferences userInteractionPreferences = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class )
            .getService();

    private Object getTransferredData(DropTargetEvent event)
    {
        TransferData tData = event.currentDataType;
        Object transferredData = RuntimeEventTimstampTransfer.getInstance().nativeToJava( tData );
        return transferredData;
    }

    @Override
    public void dragEnter(DropTargetEvent event)
    {
        Object transferredData = getTransferredData( event );
        if (transferredData instanceof List<?>)
        {
            List<?> objects = (List<?>)transferredData;
            for (Object o : objects)
            {
                if (o instanceof Long)
                {
                    if (userInteractionPreferences.isLiveMode())
                    {
                        event.detail = DND.DROP_NONE;
                        return;
                    }
                    timestamps.add( (Long)o );
                }
            }
        }
    }

    @Override
    public void drop(DropTargetEvent event)
    {
        TimeMarker lastTimeMarker = null;
        if (!timestamps.isEmpty())
        {
            for (long ts : timestamps)
            {
                lastTimeMarker = timeMarkerManager.createNewTimeMarker( ts );
            }
            if (lastTimeMarker != null)
            {
                timeMarkerManager.setCurrentSelectedTimeMarker( lastTimeMarker );
            }
            timestamps.clear();
        }
    }

}
