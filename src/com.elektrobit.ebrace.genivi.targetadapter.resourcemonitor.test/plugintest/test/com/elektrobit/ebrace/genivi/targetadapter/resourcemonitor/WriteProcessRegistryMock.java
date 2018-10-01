/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor;

import java.util.List;
import java.util.Set;

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessState;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.WriteProcessRegistryIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;

public class WriteProcessRegistryMock implements WriteProcessRegistryIF
{

    private final ResourceInfo resourceInfo;

    public WriteProcessRegistryMock(ResourceInfo resourceInfo)
    {
        this.resourceInfo = resourceInfo;
    }

    @Override
    public Set<Integer> getAllProcessPIDs(DataSourceContext dataSourceContext)
    {
        return null;
    }

    @Override
    public ProcessInfo getProcessInfo(int pid, DataSourceContext dataSourceContext)
    {
        return resourceInfo.getProcess( 0 );
    }

    @Override
    public boolean isInfoForProcessAvailable(int pid, DataSourceContext dataSourceContext)
    {
        return false;
    }

    @Override
    public Set<Integer> getActiveProcessPIDs(DataSourceContext dataSourceContext)
    {
        return null;
    }

    @Override
    public ProcessState getProcessStateOfPID(int pid, DataSourceContext dataSourceContext)
    {
        return null;
    }

    @Override
    public void removeProcessInfo(int pid, DataSourceContext dataSourceContext)
    {
    }

    @Override
    public void setProcessInfoOfActiveProcesses(List<ProcessInfo> processList, DataSourceContext dataSourceContext)
    {
    }

    @Override
    public ProcessInfo getProcessByThreadID(int threadID, DataSourceContext dataSourceContext)
    {
        return null;
    }
}
