/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.timesegmentmanager.impl;

import java.util.Comparator;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

public class STimeSegmentComparator implements Comparator<STimeSegment>
{
    @Override
    public int compare(STimeSegment o1, STimeSegment o2)
    {
        long o1StartTime = o1.getStartTime();
        long o2StartTime = o2.getStartTime();
        return Long.compare( o1StartTime, o2StartTime );
    }
}
