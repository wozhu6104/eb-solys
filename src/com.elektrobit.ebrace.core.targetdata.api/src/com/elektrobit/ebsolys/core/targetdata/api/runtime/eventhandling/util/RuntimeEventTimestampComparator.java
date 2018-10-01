/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.util;

import java.util.Comparator;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class RuntimeEventTimestampComparator implements Comparator<TimebasedObject>
{
    @Override
    public int compare(TimebasedObject obj1, TimebasedObject obj2)
    {
        if (obj1.getTimestamp() < obj2.getTimestamp())
            return -1;
        if (obj1.getTimestamp() > obj2.getTimestamp())
            return 1;
        return 0;
    }
}
