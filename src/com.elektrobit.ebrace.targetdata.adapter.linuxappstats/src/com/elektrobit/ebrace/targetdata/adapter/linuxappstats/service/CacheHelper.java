/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.linuxappstats.service;

import java.util.HashMap;
import java.util.Map;

public class CacheHelper<T>
{
    private final Map<String, T> cachedElements = new HashMap<String, T>();

    public boolean needsUpdate(String key, T value)
    {
        if (cachedElements.containsKey( key ))
        {
            if (cachedElements.get( key ).equals( value ))
            {
                return false;
            }
            else
            {
                cachedElements.put( key, value );
                return true;
            }

        }
        else
        {
            cachedElements.put( key, value );
            return true;
        }
    }

}
