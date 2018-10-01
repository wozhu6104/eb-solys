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
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ThreadInfo;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.WriteProcessRegistryMock;

public class ResMonFordGen2Test
{
    private ResourceInfo initialRessourceInfo, resourceInfoGen2;
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private StructureAcceptor structureAcceptor;

    private final static String GEN2_PROCESSNAME = "Gen2 Process";
    private final static int PID = 123;
    private final static long PROC_USED_BYTES = 2048;
    private final static long SYS_TOTAL_MEM_BYTES = 4096 * 10;
    private final static long SYS_USED_MEM_BYTES = 1024 * 10;
    private final static long SYS_UNUSED_METRIC = 0;

    private final static String THREAD_NAME = "ThreadName";
    private final static int TID = 1001;
    private final static int THREAD_PRIORITY = 123;

    private final static long TIMESTAMP_FIRST_THREADINFO = 1000;
    private final static long FIRST_TIME_KERNEL_MODE = 200;
    private final static long FIRST_TIME_USER_MODE = 100;

    private final static long TIMESTAMP_SECOND_THREADINFO = 2000;
    private final static long SECOND_TIME_KERNEL_MODE = 400;
    private final static long SECOND_TIME_USER_MODE = 300;

    private final double EXPECTED_CPU_EVENT_VALUE = 100.0
            * ((SECOND_TIME_KERNEL_MODE - FIRST_TIME_KERNEL_MODE) + (SECOND_TIME_USER_MODE - FIRST_TIME_USER_MODE))
            / (TIMESTAMP_SECOND_THREADINFO - TIMESTAMP_FIRST_THREADINFO);
    private DataSourceContext dataSourceContext;

    @Before
    public void setup()
    {
        runtimeEventAcceptor = CoreServiceHelper.getRuntimeEventAcceptor();
        dataSourceContext = new DataSourceContext( SOURCE_TYPE.FILE, "test." );
        structureAcceptor = CoreServiceHelper.getStructureAcceptor();
        ResetNotifier resetNotifier = CoreServiceHelper.getResetNotifier();
        resetNotifier.performReset();
    }

    @Test
    public void testResMonForGen2()
    {
        createInitialResourceInfo();
        createResourceInfoForGen2Process();

        ResMonDataEventDecoder resMonDataEventDecoder = new ResMonDataEventDecoder( new WriteProcessRegistryMock( initialRessourceInfo ),
                                                                                    structureAcceptor,
                                                                                    runtimeEventAcceptor,
                                                                                    dataSourceContext );

        resMonDataEventDecoder.initIfNot();
        resMonDataEventDecoder.newResMonApplicationMessageReceived( TimestampMocker.mock( 150, 10, 11, 100 ),
                                                                    initialRessourceInfo );

        resMonDataEventDecoder.newResMonApplicationMessageReceived( TimestampMocker.mock( 12, 182, 13, 130 ),
                                                                    resourceInfoGen2 );

        RuntimeEventChannel<?> memSystem = runtimeEventAcceptor.getRuntimeEventChannel( "test.mem.system" );
        RuntimeEventChannel<?> cpuSystem = runtimeEventAcceptor.getRuntimeEventChannel( "test.cpu.system" );

        RuntimeEventChannel<?> memGen2 = runtimeEventAcceptor.getRuntimeEventChannel( "test.mem.proc."
                + GEN2_PROCESSNAME + ":" + ("" + Long.toHexString( PID )).toUpperCase() );
        RuntimeEventChannel<?> cpuGen2 = runtimeEventAcceptor.getRuntimeEventChannel( "test.cpu.proc."
                + GEN2_PROCESSNAME + ":" + ("" + Long.toHexString( PID )).toUpperCase() );

        RuntimeEventChannel<?> threadPriorityGen2 = runtimeEventAcceptor.getRuntimeEventChannel( "test.prio.proc."
                + GEN2_PROCESSNAME + ":" + ("" + Long.toHexString( PID )).toUpperCase() + ".t:" + THREAD_NAME + ":"
                + ("" + Long.toHexString( TID )).toUpperCase() );

        assertNotNull( "Channel mem.system should be created", memSystem );
        assertNotNull( "Channel cpu.system should be created", cpuSystem );

        assertEquals( SYS_USED_MEM_BYTES / 1024,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( memSystem ).get( 0 ).getValue() );

        assertEquals( PROC_USED_BYTES / 1024,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( memGen2 ).get( 0 ).getValue() );

        assertEquals( EXPECTED_CPU_EVENT_VALUE,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpuSystem ).get( 0 ).getValue() );

        assertEquals( EXPECTED_CPU_EVENT_VALUE,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpuGen2 ).get( 0 ).getValue() );

        assertEquals( THREAD_PRIORITY,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( threadPriorityGen2 ).get( 0 )
                              .getValue() );

        assertEquals( 6, runtimeEventAcceptor.getRuntimeEventChannels().size() );
    }

    private void createInitialResourceInfo()
    {
        ThreadInfo.Builder threadInfoBuilder = ThreadInfo.newBuilder();

        threadInfoBuilder.setName( THREAD_NAME );
        threadInfoBuilder.setTid( TID );
        threadInfoBuilder.setTimestamp( TIMESTAMP_FIRST_THREADINFO );
        threadInfoBuilder.setTimeInKernelModeMs( FIRST_TIME_KERNEL_MODE );
        threadInfoBuilder.setTimeInUserModeMs( FIRST_TIME_USER_MODE );
        threadInfoBuilder.setPriority( THREAD_PRIORITY );

        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();
        processInfoBuilder.setName( "InitialProcess" );
        processInfoBuilder.setPid( PID );
        processInfoBuilder.setVmUsageBytes( 50 );
        processInfoBuilder.setTimeInUserModeMs( 2 );
        processInfoBuilder.setTimeInKernelModeMs( 3 );

        processInfoBuilder.addThreads( threadInfoBuilder.build() );

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

    private void createResourceInfoForGen2Process()
    {
        ThreadInfo.Builder threadInfoBuilder = ThreadInfo.newBuilder();

        threadInfoBuilder.setName( THREAD_NAME );
        threadInfoBuilder.setTid( TID );
        threadInfoBuilder.setTimestamp( TIMESTAMP_SECOND_THREADINFO );
        threadInfoBuilder.setTimeInKernelModeMs( SECOND_TIME_KERNEL_MODE );
        threadInfoBuilder.setTimeInUserModeMs( SECOND_TIME_USER_MODE );
        threadInfoBuilder.setPriority( THREAD_PRIORITY );

        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();
        processInfoBuilder.setName( GEN2_PROCESSNAME );
        processInfoBuilder.setPid( PID );
        processInfoBuilder.setVmUsageBytes( PROC_USED_BYTES );

        processInfoBuilder.addThreads( threadInfoBuilder.build() );

        ResourceInfo.Builder resourceInfoBuilder = ResourceInfo.newBuilder();
        resourceInfoBuilder.addProcess( processInfoBuilder.build() );

        SystemInfo.Builder sysInfoBuilder = SystemInfo.newBuilder();
        sysInfoBuilder.setNumberOfCores( 1 );
        sysInfoBuilder.setTotalVmMemBytes( SYS_UNUSED_METRIC );
        sysInfoBuilder.setUsedVmMemBytes( SYS_UNUSED_METRIC );
        sysInfoBuilder.setTotalPmMemBytes( SYS_TOTAL_MEM_BYTES );
        sysInfoBuilder.setUsedPmMemBytes( SYS_USED_MEM_BYTES );
        resourceInfoBuilder.setSystem( sysInfoBuilder.build() );

        resourceInfoGen2 = resourceInfoBuilder.build();
    }

}
