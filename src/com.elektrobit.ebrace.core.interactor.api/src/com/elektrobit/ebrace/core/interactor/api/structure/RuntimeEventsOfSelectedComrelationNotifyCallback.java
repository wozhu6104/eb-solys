/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.structure;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public interface RuntimeEventsOfSelectedComrelationNotifyCallback
{
    public void onNewComRelationEventData(List<?> events);

    public void onJumpToTimeMarker(TimeMarker timeMarker);

    public void onTimeMarkerRenamed(TimeMarker timeMarker);
}
