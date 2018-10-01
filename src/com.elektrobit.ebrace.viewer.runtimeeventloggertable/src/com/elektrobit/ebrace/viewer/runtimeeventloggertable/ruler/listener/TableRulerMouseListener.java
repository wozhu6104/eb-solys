/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.listener;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public abstract class TableRulerMouseListener implements MouseTrackListener, MouseListener
{
    private static final int MAX_CURSOR_TO_TARGET_DISTANCE = 5;

    protected Map<TimebasedObject, Integer> timestampPositions = Collections.emptyMap();

    public void setTimestampPositions(Map<TimebasedObject, Integer> timestampPositions)
    {
        this.timestampPositions = timestampPositions;
    }

    private int computeMouseToTargetDistance(int mouseYPosition, Integer closestTimeMarkerPosition)
    {
        return Math.abs( mouseYPosition - closestTimeMarkerPosition );
    }

    private boolean isTargetTooFar(Integer closestTimeMarkerPosition, int mouseYPosition)
    {
        return Math.abs( closestTimeMarkerPosition - mouseYPosition ) > MAX_CURSOR_TO_TARGET_DISTANCE;
    }

    private Entry<TimebasedObject, Integer> findClosestTimeStamp(int mouseYPosition)
    {
        Entry<TimebasedObject, Integer> closestTimestamp = timestampPositions.entrySet().iterator().next();
        int closestDistance = computeMouseToTargetDistance( mouseYPosition, closestTimestamp.getValue() );

        for (Entry<TimebasedObject, Integer> entry : timestampPositions.entrySet())
        {
            int currentDistance = computeMouseToTargetDistance( mouseYPosition, entry.getValue() );
            if (currentDistance < closestDistance)
            {
                closestDistance = currentDistance;
                closestTimestamp = entry;
            }
        }
        return closestTimestamp;
    }

    protected TimebasedObject findNearbyTimeStamp(int mouseYPosition)
    {
        TimebasedObject result = null;
        if (!timestampPositions.isEmpty())
        {
            Entry<TimebasedObject, Integer> closestTimeStamp = findClosestTimeStamp( mouseYPosition );
            if (!isTargetTooFar( closestTimeStamp.getValue(), mouseYPosition ))
            {
                result = closestTimeStamp.getKey();
            }
        }

        return result;
    }

    @Override
    public void mouseEnter(MouseEvent e)
    {
    }

    @Override
    public void mouseExit(MouseEvent e)
    {
    }

    @Override
    public void mouseHover(MouseEvent e)
    {
    }

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
    }

    @Override
    public void mouseDown(MouseEvent e)
    {
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
    }

}
