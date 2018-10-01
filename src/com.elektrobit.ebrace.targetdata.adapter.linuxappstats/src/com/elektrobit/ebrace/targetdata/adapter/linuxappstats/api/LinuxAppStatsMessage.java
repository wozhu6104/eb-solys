/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.adapter.linuxappstats.api;

import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.LinuxAppStatsTAProto;
import com.elektrobit.ebrace.targetdata.adapter.linuxappstats.LinuxAppStatsTAProto.AppStatistics.Builder;

public class LinuxAppStatsMessage
{

    public static LinuxAppStatsTAProto.AppStatistics getProcessDummyMessage()
    {
        Builder builder = LinuxAppStatsTAProto.AppStatistics.newBuilder();
        builder.setTrace( "CC:23253658 PI:23243 PN:\"eclipse\" PT:128.0" );
        return builder.build();
    }

    public static LinuxAppStatsTAProto.AppStatistics getThreadDummyMessage()
    {
        Builder builder = LinuxAppStatsTAProto.AppStatistics.newBuilder();
        builder.setTrace( "CC:23253658 PI:23243 TI:23243 TN:\"eclipse\" TP:2475072 TC:95513 TT:0.0" );
        return builder.build();
    }
}
