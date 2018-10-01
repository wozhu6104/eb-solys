/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import java.util.HashMap;
import java.util.Map;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

public class IdNumberGenerator
{
    private final static Map<String, Long> nameToCounterIdMap = new HashMap<String, Long>();

    public static synchronized long getNextId(final String counterName)
    {
        RangeCheckUtils.assertStringParameterNotNullOrEmpty( "counterName", counterName );

        long generatedId = 0;

        if (nameToCounterIdMap.containsKey( counterName ))
        {
            generatedId = nameToCounterIdMap.get( counterName );
        }

        generatedId++;
        nameToCounterIdMap.put( counterName, generatedId );

        return generatedId;
    }
}
