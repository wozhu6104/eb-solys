/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.internal.runtimeEventCreator;

import com.elektrobit.ebrace.targetdata.dlt.internal.DltMessage;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.Measurement;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.ProcCpuEntry;
import com.elektrobit.ebrace.targetdata.dlt.internal.procfsparser.ProcMemEntry;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;

public interface DltRuntimeEventCreator
{

    void createRawDltMsg(DltMessage dltMsg);

    void createTaggedDltMsg(DltMessage dltMsg, RuntimeEventTag tag, String description);

    void createCpuUsageDltMsg(Measurement<ProcCpuEntry> cpuResults);

    void createCpuUsageDltMsg(Measurement<ProcCpuEntry> cpuResults, String channelPrefix, int sessionID);

    void createMemUsageDltMsg(Measurement<ProcMemEntry> memResults);

    void createMemUsageDltMsg(Measurement<ProcMemEntry> memResults, String channelPrefix, int sessionID);

}
