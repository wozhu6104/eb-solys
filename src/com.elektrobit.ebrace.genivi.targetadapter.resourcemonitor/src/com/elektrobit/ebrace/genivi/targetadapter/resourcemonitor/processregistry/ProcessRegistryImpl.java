/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.processregistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessInfoChangedListenerIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ProcessState;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ReadProcessRegistryIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.WriteProcessRegistryIF;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ThreadInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;

@Component(service = {WriteProcessRegistryIF.class, ReadProcessRegistryIF.class, ClearChunkDataListener.class})
public final class ProcessRegistryImpl implements WriteProcessRegistryIF, ClearChunkDataListener
{
    private final Map<DataSourceContext, Map<Integer, ProcessInfo>> contextToPidToProcessInfoMap = new HashMap<DataSourceContext, Map<Integer, ProcessInfo>>();
    private final Map<DataSourceContext, Set<Integer>> contextToActiveProcesses = new HashMap<DataSourceContext, Set<Integer>>();
    private final OSGIWhiteBoardPatternCaller<ProcessInfoChangedListenerIF> processInfoChangedCommandCaller = new OSGIWhiteBoardPatternCaller<ProcessInfoChangedListenerIF>( ProcessInfoChangedListenerIF.class );

    private Map<Integer, ProcessInfo> getProcessMapForContext(DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = contextToPidToProcessInfoMap.get( dataSourceContext );
        if (pidToProcessInfoMap != null)
        {
            return pidToProcessInfoMap;
        }
        else
        {
            pidToProcessInfoMap = new HashMap<Integer, ProcessInfo>();
            contextToPidToProcessInfoMap.put( dataSourceContext, pidToProcessInfoMap );
            return pidToProcessInfoMap;
        }
    }

    @Override
    public ProcessState getProcessStateOfPID(final int pid, DataSourceContext dataSourceContext)
    {
        Set<Integer> activeProcesses = getActiveProcessPIDs( dataSourceContext );
        ProcessState resultState = ProcessState.DEAD;
        if (activeProcesses.contains( pid ))
        {
            resultState = ProcessState.RUNNING;
        }
        return resultState;
    }

    @Override
    public Set<Integer> getActiveProcessPIDs(DataSourceContext dataSourceContext)
    {
        Set<Integer> activeProcesses = contextToActiveProcesses.get( dataSourceContext );
        if (activeProcesses != null)
        {
            return activeProcesses;
        }
        else
        {
            activeProcesses = new HashSet<Integer>();
            contextToActiveProcesses.put( dataSourceContext, activeProcesses );
            return activeProcesses;
        }
    }

    @Override
    public Set<Integer> getAllProcessPIDs(DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = getProcessMapForContext( dataSourceContext );
        return pidToProcessInfoMap.keySet();
    }

    @Override
    public boolean isInfoForProcessAvailable(int pid, DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = getProcessMapForContext( dataSourceContext );
        return pidToProcessInfoMap.containsKey( pid );
    }

    @Override
    public ProcessInfo getProcessInfo(int pid, DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = getProcessMapForContext( dataSourceContext );
        ProcessInfo processInfo = null;
        if (pidToProcessInfoMap.containsKey( pid ))
        {
            processInfo = pidToProcessInfoMap.get( pid );
        }
        return processInfo;
    }

    @Override
    public void setProcessInfoOfActiveProcesses(List<ProcessInfo> processList,
            final DataSourceContext dataSourceContext)
    {
        Set<Integer> activeProcesses = getActiveProcessPIDs( dataSourceContext );
        activeProcesses.clear();
        for (ProcessInfo nextProcessInfo : processList)
        {
            setNewProcessInfo( nextProcessInfo, dataSourceContext );
            activeProcesses.add( (int)nextProcessInfo.getPid() );
        }

        notifyProcessInfoChanged( dataSourceContext );
    }

    private void notifyProcessInfoChanged(final DataSourceContext dataSourceContext)
    {
        processInfoChangedCommandCaller
                .callOSGIService( new OSGIWhiteBoardPatternCommand<ProcessInfoChangedListenerIF>()
                {
                    @Override
                    public void callOSGIService(ProcessInfoChangedListenerIF osgiService)
                    {
                        osgiService.processInfoChanged( dataSourceContext );
                    }
                } );
    }

    private void setNewProcessInfo(ProcessInfo processInfo, DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = getProcessMapForContext( dataSourceContext );
        if (!pidToProcessInfoMap.containsKey( processInfo.getPid() ))
        {
            pidToProcessInfoMap.put( (int)processInfo.getPid(), processInfo );
        }
    }

    @Override
    public void removeProcessInfo(int pid, DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = getProcessMapForContext( dataSourceContext );
        pidToProcessInfoMap.remove( pid );
    }

    @Override
    public ProcessInfo getProcessByThreadID(int threadID, DataSourceContext dataSourceContext)
    {
        Map<Integer, ProcessInfo> pidToProcessInfoMap = getProcessMapForContext( dataSourceContext );

        for (ProcessInfo processInfo : pidToProcessInfoMap.values())
        {
            List<ThreadInfo> threads = processInfo.getThreadsList();
            for (ThreadInfo thread : threads)
            {
                if (thread.getTid() == threadID)
                {
                    return processInfo;
                }
            }
        }
        return null;
    }

    @Override
    public void onClearChunkData()
    {
        contextToActiveProcesses.clear();
        contextToPidToProcessInfoMap.clear();
    }
}
