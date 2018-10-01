/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.timemarker;

import java.util.SortedSet;

public interface TimeMarkerManager
{
    TimeMarker createNewTimeMarker(long timestamp);

    void removeTimeMarker(TimeMarker timeMarkerToRemove);

    TimeMarker getTimeMarkerForTimestamp(long timestamp);

    boolean removeTimeMarkerForClickedPosition(double d, double e, long minTimeAxisInAnalysisMode,
            long maxTimeAxisInAnalysisMode, int xPosOfMouse);

    TimeMarker getTimeMarkerForClickedPosition(double d, double e, long minTimeAxisInAnalysisMode,
            long maxTimeAxisInAnalysisMode, int xPosOfMouse);

    SortedSet<TimeMarker> getAllTimeMarkers();

    TimeMarker getCurrentSelectedTimeMarker();

    void setCurrentSelectedTimeMarker(TimeMarker currentSelectedTimeMarker);

    void setCurrentSelectedTimeMarker(TimeMarker currentSelectedTimeMarker, boolean updateAnalysisTimespan);

    void removeAllTimeMarkers();

    SortedSet<TimeMarker> getAllTimeMarkersGreaterThanTimestamp(long timestamp);

    SortedSet<TimeMarker> getAllVisibleTimeMarkersGreaterThanTimestamp(long timestamp);

    SortedSet<TimeMarker> getAllTimeMarkersBetweenTimestamp(long start, long end);

    SortedSet<TimeMarker> getAllVisibleTimeMarkersBetweenTimestamp(long start, long end);

    void toggleVisibility();

    boolean getVisibility();

    void removeAllTimeMarkersWithName(String name);

    void removeAllTimeMarkersWithPrefix(String prefix);

    SortedSet<TimeMarker> getAllVisibleTimemarkers();

    public void registerListener(TimeMarkersChangedListener listener);

    public void unregisterListener(TimeMarkersChangedListener listener);

    void toggleVisibility(TimeMarker timemarker);
}
