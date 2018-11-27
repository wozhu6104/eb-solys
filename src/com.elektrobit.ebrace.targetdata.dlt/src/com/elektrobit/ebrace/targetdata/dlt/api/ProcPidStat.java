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
public class ProcPidStat
{
    public final static int PID_FIELD_NR = 1;
    public final static int FULLNAME_FIELD_NR = 2;
    public final static int STATE_FIELD_NR = 3;
    public final static int PPPID_FIELD_NR = 4;
    public final static int PGRP_FIELD_NR = 5;
    public final static int SESSION_FIELD_NR = 6;
    public final static int TTY_FIELD_NR = 7;
    public final static int TPGID_FIELD_NR = 8;
    public final static int FLAGS_FIELD_NR = 9;
    public final static int MINFLT_FIELD_NR = 10;
    public final static int MAJFLT_FIELD_NR = 11;
    public final static int CMINFLT_FIELD_NR = 12;
    public final static int CMAJFLT_FIELD_NR = 13;
    public final static int UTIME_FIELD_NR = 14;
    public final static int STIME_FIELD_NR = 15;
    public final static int CUTIME_FIELD_NR = 16;
    public final static int CSTIME_FIELD_NR = 17;
    public final static int MSEC_PER_CLOCK = 10;
    private String fullName;
    private long utime;
    private long stime;
    private char state;
    private int pid;
    private int ppid;
    private int pgrp;
    private int session;
    private int tty;
    private int tpgid;
    private long flags;
    private long minflt;
    private long majflt;
    private long cminflt;
    private long cmajflt;
    private int cutime;
    private int cstime;

    @Override
    public String toString()
    {

        return "{ProcID: " + pid + "}   {ProcName:   " + fullName + "}   {CpuUsage:   "
                + (utime + stime + cutime + cstime) + "}";
    }
}
