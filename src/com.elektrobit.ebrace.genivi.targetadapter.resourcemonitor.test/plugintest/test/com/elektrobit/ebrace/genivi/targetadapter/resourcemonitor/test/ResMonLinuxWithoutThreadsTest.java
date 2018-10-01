/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.datamanager.TimestampMocker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.impl.ResMonDataEventDecoder;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.SystemInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.WriteProcessRegistryMock;

public class ResMonLinuxWithoutThreadsTest
{
    private ResourceInfo initialRessourceInfo, resourceInfoLinuxWithoutThreads;

    private RuntimeEventAcceptor runtimeEventAcceptor;
    private StructureAcceptor structureAcceptor;

    private final static String PROCESSNAME = "Linux Process without Threads";
    private final static int PID = 123;
    private final static long PROC_USED_BYTES = 2048;
    private final static long SYS_TOTAL_MEM_BYTES = 4096 * 10;
    private final static long SYS_USED_MEM_BYTES = 1024 * 10;
    private final static long SYS_UNUSED_METRIC = 0;

    private final static int FIRST_TIME_USER_MODE = 10;
    private final static int FIRST_TIME_KERNEL_MODE = 20;

    private final static int SECOND_TIME_USER_MODE = 20;
    private final static int SECOND_TIME_KERNEL_MODE = 40;

    private final static Timestamp firstTimestamp = TimestampMocker.mock( 1000 );
    private final static Timestamp secondTimestamp = TimestampMocker.mock( 2000 );

    private final static double EXPECTED_CPU_EVENT_VALUE = 100.0
            * ((SECOND_TIME_KERNEL_MODE + SECOND_TIME_USER_MODE - FIRST_TIME_KERNEL_MODE - FIRST_TIME_USER_MODE))
            / (secondTimestamp.getTimeInMillis() - firstTimestamp.getTimeInMillis());

    private DataSourceContext dataSourceContext;

    @Before
    public void setup()
    {
        runtimeEventAcceptor = CoreServiceHelper.getRuntimeEventAcceptor();
        structureAcceptor = CoreServiceHelper.getStructureAcceptor();
        dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, "test." );

        ResetNotifier resetNotifier = CoreServiceHelper.getResetNotifier();
        resetNotifier.performReset();
    }

    @Test
    public void testResMonForLinuxWithoutThreads()
    {
        createInitialResourceInfo();
        createResourceInfoForLinuxProcessWithoutThreads();

        ResMonDataEventDecoder resMonDataEventDecoder = new ResMonDataEventDecoder( new WriteProcessRegistryMock( initialRessourceInfo ),
                                                                                    structureAcceptor,
                                                                                    runtimeEventAcceptor,
                                                                                    dataSourceContext );

        resMonDataEventDecoder.initIfNot();
        resMonDataEventDecoder.newResMonApplicationMessageReceived( firstTimestamp, initialRessourceInfo );

        resMonDataEventDecoder.newResMonApplicationMessageReceived( secondTimestamp, resourceInfoLinuxWithoutThreads );

        RuntimeEventChannel<?> memSystem = runtimeEventAcceptor.getRuntimeEventChannel( "test.mem.system" );
        RuntimeEventChannel<?> cpuSystem = runtimeEventAcceptor.getRuntimeEventChannel( "test.cpu.system" );

        RuntimeEventChannel<?> mem_p = runtimeEventAcceptor
                .getRuntimeEventChannel( "test.mem.proc." + PROCESSNAME + ":" + PID );
        RuntimeEventChannel<?> cpu_p = runtimeEventAcceptor
                .getRuntimeEventChannel( "test.cpu.proc." + PROCESSNAME + ":" + PID );

        assertNotNull( "Channel mem.system should be created", memSystem );
        assertNotNull( "Channel cpu.system should be created", cpuSystem );

        assertEquals( SYS_USED_MEM_BYTES / 1024,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( memSystem ).get( 0 ).getValue() );

        assertEquals( PROC_USED_BYTES / 1024,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( mem_p ).get( 0 ).getValue() );

        assertEquals( EXPECTED_CPU_EVENT_VALUE / 2,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpuSystem ).get( 0 ).getValue() );

        assertEquals( EXPECTED_CPU_EVENT_VALUE / 2,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpu_p ).get( 0 ).getValue() );

        assertEquals( 4, runtimeEventAcceptor.getRuntimeEventChannels().size() );
    }

    private void createInitialResourceInfo()
    {
        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();

        processInfoBuilder.setName( "InitialProcess" );
        processInfoBuilder.setPid( PID );
        processInfoBuilder.setVmUsageBytes( 1 );
        processInfoBuilder.setTimeInUserModeMs( FIRST_TIME_USER_MODE );
        processInfoBuilder.setTimeInKernelModeMs( FIRST_TIME_KERNEL_MODE );

        ResourceInfo.Builder resourceInfoBuilder = ResourceInfo.newBuilder();
        resourceInfoBuilder.addProcess( processInfoBuilder.build() );

        SystemInfo.Builder sysInfoBuilder = SystemInfo.newBuilder();
        sysInfoBuilder.setNumberOfCores( 1 );
        sysInfoBuilder.setTotalVmMemBytes( SYS_UNUSED_METRIC );
        sysInfoBuilder.setUsedVmMemBytes( SYS_UNUSED_METRIC );
        sysInfoBuilder.setTotalPmMemBytes( SYS_TOTAL_MEM_BYTES );
        sysInfoBuilder.setUsedPmMemBytes( SYS_USED_MEM_BYTES );

        resourceInfoBuilder.setSystem( sysInfoBuilder.build() );

        initialRessourceInfo = resourceInfoBuilder.build();
    }

    private void createResourceInfoForLinuxProcessWithoutThreads()
    {
        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();

        processInfoBuilder.setName( PROCESSNAME );
        processInfoBuilder.setPid( PID );
        processInfoBuilder.setVmUsageBytes( PROC_USED_BYTES );
        processInfoBuilder.setTimeInUserModeMs( SECOND_TIME_USER_MODE );
        processInfoBuilder.setTimeInKernelModeMs( SECOND_TIME_KERNEL_MODE );

        ResourceInfo.Builder resourceInfoBuilder = ResourceInfo.newBuilder();
        resourceInfoBuilder.addProcess( processInfoBuilder.build() );

        SystemInfo.Builder sysInfoBuilder = SystemInfo.newBuilder();
        sysInfoBuilder.setNumberOfCores( 2 );
        sysInfoBuilder.setTotalVmMemBytes( SYS_UNUSED_METRIC );
        sysInfoBuilder.setUsedVmMemBytes( SYS_UNUSED_METRIC );
        sysInfoBuilder.setTotalPmMemBytes( SYS_TOTAL_MEM_BYTES );
        sysInfoBuilder.setUsedPmMemBytes( SYS_USED_MEM_BYTES );

        resourceInfoBuilder.setSystem( sysInfoBuilder.build() );

        resourceInfoLinuxWithoutThreads = resourceInfoBuilder.build();
    }
}
