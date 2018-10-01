/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

import lombok.Getter;
import lombok.Setter;

public class TimeSegmentImpl implements STimeSegment
{
    @Getter
    private final RuntimeEvent<?> startEvent;
    @Getter
    private final RuntimeEvent<?> endEvent;
    @Getter
    private final String label;
    @Getter
    private final long startTime;
    @Getter
    private final long endTime;
    @Getter
    private final long duration;
    @Getter
    @Setter
    private STimeSegmentClickAction clickAction = null;
    @Setter
    @Getter
    private SColor color;
    @Getter
    private final RuntimeEventChannel<STimeSegment> channel;

    public TimeSegmentImpl(RuntimeEvent<?> startEvent, RuntimeEvent<?> endEvent, String label, SColor color,
            RuntimeEventChannel<STimeSegment> channel)
    {
        this.startEvent = startEvent;
        this.endEvent = endEvent;
        this.label = label;
        this.color = color;
        this.channel = channel;
        this.startTime = startEvent.getTimestamp();
        this.endTime = endEvent.getTimestamp();
        this.duration = endTime - startTime;
    }

}
