/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.timesegmentmanager.api;

import java.util.List;
import java.util.Map;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

public interface TimeSegmentProviderService
{
    public List<STimeSegment> getTimeSegments(RuntimeEventChannel<STimeSegment> timeSegmentChannel);

    public Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> getTimeSegmentsForChannel(
            List<RuntimeEventChannel<STimeSegment>> timeSegmentChannel);

}
