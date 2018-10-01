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

public class UnitConverter
{
    private final static double BYTES_IN_KB = 1024;
    private final static double BYTES_IN_MB = 1048576;
    private final static double BYTES_IN_GB = 1073741824;

    public final static long convertBytesToMB(long bytes)
    {
        return Math.round( bytes / BYTES_IN_MB );
    }

    public final static long convertMBToBytes(long mb)
    {
        return (long)(BYTES_IN_MB * mb);
    }

    public final static long convertBytesToKB(long bytes)
    {
        return Math.round( bytes / BYTES_IN_KB );
    }

    public final static long convertBytesToGB(long bytes)
    {
        return Math.round( bytes / BYTES_IN_GB );
    }

}
