/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.memory;

import org.apache.log4j.Logger;

public class MemoryObserver
{
    private static final int MAX_PERCENT_MEMORY_USED = 75;
    private static final int MAX_PERCENT_MEMORY_USED_AFTER_GC = 70;
    private static final int REQUESTS_BEFORE_NEW_CHECK = 300;
    private final static Logger LOG = Logger.getLogger( MemoryObserver.class );
    private static int requestCounter = 0;

    public static boolean isEnoughFreeMemoryAvailable()
    {
        if (requestCounter < REQUESTS_BEFORE_NEW_CHECK)
        {
            requestCounter++;
            return true;
        }
        requestCounter = 0;

        if (getUsedMemoryPercent() < MAX_PERCENT_MEMORY_USED)
            return true;
        System.gc();
        boolean enoughMemoryAfterGC = getUsedMemoryPercent() < MAX_PERCENT_MEMORY_USED_AFTER_GC;
        return enoughMemoryAfterGC;
    }

    public static int getUsedMemoryPercent()
    {
        long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory == Long.MAX_VALUE)
        {
            LOG.error( "Max memory info not available" );
            return 0;
        }
        double percentUsed = ((double)allocatedMemory) / ((double)maxMemory) * 100.0;
        return (int)Math.round( percentUsed );
    }
}
