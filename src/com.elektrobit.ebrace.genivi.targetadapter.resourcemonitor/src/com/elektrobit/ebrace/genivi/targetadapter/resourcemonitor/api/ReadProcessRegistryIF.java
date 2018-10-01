/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api;

import java.util.Set;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;

public interface ReadProcessRegistryIF
{
    public Set<Integer> getAllProcessPIDs(DataSourceContext dataSourceContext);

    public ProcessInfo getProcessInfo(int pid, DataSourceContext dataSourceContext);

    public boolean isInfoForProcessAvailable(int pid, DataSourceContext dataSourceContext);

    public Set<Integer> getActiveProcessPIDs(DataSourceContext dataSourceContext);

    public ProcessState getProcessStateOfPID(int pid, DataSourceContext dataSourceContext);

    public void removeProcessInfo(int pid, DataSourceContext dataSourceContext);

    public ProcessInfo getProcessByThreadID(int threadID, DataSourceContext dataSourceContext);
}
