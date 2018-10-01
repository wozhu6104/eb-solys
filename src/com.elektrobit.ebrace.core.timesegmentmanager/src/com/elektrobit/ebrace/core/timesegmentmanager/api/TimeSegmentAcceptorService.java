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

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

public interface TimeSegmentAcceptorService extends TimeSegmentProviderService
{
    public RuntimeEventChannel<STimeSegment> createOrGetTimeSegmentChannel(String name, String description);

    public RuntimeEventChannel<STimeSegment> createTimeSegmentChannel(String name, String description);

    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent);

    public STimeSegment add(RuntimeEventChannel<STimeSegment> timeSegmentChannel, RuntimeEvent<?> startEvent,
            RuntimeEvent<?> endEvent, String label);

    public void setColor(STimeSegment segment, SColor color);

    public void clear();

    public void removeSegmentsForChannel(RuntimeEventChannel<STimeSegment> channel);

    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channels);

    public void unregisterListener(ChannelsContentChangedListener listener);
}
