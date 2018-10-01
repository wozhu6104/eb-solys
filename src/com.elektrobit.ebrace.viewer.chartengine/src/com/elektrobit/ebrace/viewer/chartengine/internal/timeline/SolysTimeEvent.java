/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.timeline;

import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

import lombok.Getter;

public class SolysTimeEvent extends TimeEvent
{
    @Getter
    private final STimeSegment timeSegment;

    public SolysTimeEvent(STimeSegment timeSegment, ITimeGraphEntry entry, long time, long duration)
    {
        super( entry, time, duration );
        RangeCheckUtils.assertReferenceParameterNotNull( "timeSegment", timeSegment );
        this.timeSegment = timeSegment;
    }
}
