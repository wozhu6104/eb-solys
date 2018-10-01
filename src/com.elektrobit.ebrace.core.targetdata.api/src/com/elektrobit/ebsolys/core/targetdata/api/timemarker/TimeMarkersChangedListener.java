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

public interface TimeMarkersChangedListener
{
    public void newTimeMarkerCreated(TimeMarker timeMarker);

    public void timeMarkerRemoved(TimeMarker timeMarker);

    public void timeMarkerRenamed(TimeMarker timeMarker);

    public void timeMarkerSelected(TimeMarker timeMarker);

    public void timeMarkerVisibilityChanged(TimeMarker timeMarker);

    public void timeMarkerNameChanged(TimeMarker timeMarker);

    public void timeMarkerTimestampChanged(TimeMarker timeMarker);

    public void allTimeMarkersRemoved();

    public void allTimeMarkersVisibilityToggled();
}
