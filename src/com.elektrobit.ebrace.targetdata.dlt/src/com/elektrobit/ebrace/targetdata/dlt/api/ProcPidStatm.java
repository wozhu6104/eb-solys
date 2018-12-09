/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.api;

import lombok.Data;

@Data
public class ProcPidStatm
{
    public final static int RESIDENT_SET_SIZE_FIELD_NR = 1;
    private int PageSize;
    private int Rss;
    private int Text;
    private int Data;
    private int Lib;
    private int Shared;
    private int Size;
    private int Dirty;

    @Override
    public String toString()
    {
        return "{MemInfo:   " + Rss + "}";
    }

}
