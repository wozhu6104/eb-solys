/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.checker;

import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimeMarkerInRangeChecker implements Checker<TimeMarker>
{
    protected final long start;
    protected long end;
    protected boolean endIsSet;
    protected final boolean validateJustEnabledTimeMarkers;

    public TimeMarkerInRangeChecker(long start, boolean validateJustEnabledTimeMarkers)
    {
        this.start = start;
        this.validateJustEnabledTimeMarkers = validateJustEnabledTimeMarkers;
    }

    public TimeMarkerInRangeChecker(long start, long end, boolean validateJustEnabledTimeMarkers)
    {
        this( start, validateJustEnabledTimeMarkers );
        this.end = end;
        this.endIsSet = true;
    }

    @Override
    public boolean validate(TimeMarker timeMarker)
    {
        if (validateJustEnabledTimeMarkers && !timeMarker.isEnabled())
        {
            return false;
        }
        if (timeMarker.getTimestamp() < start)
        {
            return false;
        }
        if (endIsSet && timeMarker.getTimestamp() > end)
        {
            return false;
        }
        return true;
    }
}
