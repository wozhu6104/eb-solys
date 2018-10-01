/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.util.RuntimeEventTimestampComparator;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimeMarkerMixer
{
    public static List<TimebasedObject> mixAndSortTimemarkers(List<TimebasedObject> items, List<TimeMarker> timeMarkers)
    {
        List<TimebasedObject> result = new ArrayList<TimebasedObject>( timeMarkers );
        result.addAll( items );
        Collections.sort( result, new RuntimeEventTimestampComparator() );
        return result;
    }
}
