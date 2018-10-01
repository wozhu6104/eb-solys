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

import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.SystemInfo;

public class ResourceMonitorMessage
{
    public static ResourceInfo resourceMonitorDummyMessage()
    {
        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();

        processInfoBuilder.setName( "InitialProcess" );
        processInfoBuilder.setPid( 123 );
        processInfoBuilder.setVmUsageBytes( 1 );
        processInfoBuilder.setTimeInUserModeMs( 10 );
        processInfoBuilder.setTimeInKernelModeMs( 20 );

        ResourceInfo.Builder resourceInfoBuilder = ResourceInfo.newBuilder();
        resourceInfoBuilder.addProcess( processInfoBuilder.build() );

        SystemInfo.Builder sysInfoBuilder = SystemInfo.newBuilder();
        sysInfoBuilder.setNumberOfCores( 1 );
        sysInfoBuilder.setTotalVmMemBytes( 50 );
        sysInfoBuilder.setUsedVmMemBytes( 45 );
        sysInfoBuilder.setTotalPmMemBytes( 122 );
        sysInfoBuilder.setUsedPmMemBytes( 20 );

        resourceInfoBuilder.setSystem( sysInfoBuilder.build() );

        return resourceInfoBuilder.build();
    }
}
