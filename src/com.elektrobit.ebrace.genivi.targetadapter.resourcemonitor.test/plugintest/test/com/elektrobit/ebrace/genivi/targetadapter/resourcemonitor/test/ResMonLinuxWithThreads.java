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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

import test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.WriteProcessRegistryMock;

public class ResMonLinuxWithThreads
{

    private ResourceInfo initialRessourceInfo, resourceInfoLinuxWithThreads;

    private RuntimeEventAcceptor runtimeEventAcceptor;
    private StructureAcceptor structureAcceptor;

    private final static String PROCESSNAME = "Linux Process with Threads";
    private final static int NOR_OF_CORES = 1;
    private final static int PID = 123;
    private final static long PROC_USED_BYTES = 2048;
    private final static long SYS_TOTAL_MEM_BYTES = 4096 * 10;
    private final static long SYS_USED_MEM_BYTES = 1024 * 10;
    private final static long UNUSED_METRIC = 0;

    private final static String THREAD_NAME = "ThreadName";
    private final static int TID = 1001;
    private final static long THREAD_VM_BYTES = 1;
    private final static long FIRST_THREAD_TIME_KERNEL_MODE = 200;
    private final static long FIRST_THREAD_TIME_USER_MODE = 150;
    private final static long SECOND_THREAD_TIME_KERNEL_MODE = 300;
    private final static long SECOND_THREAD_TIME_USER_MODE = 225;

    private final static long TIMESTAMP_FIRST_THREADINFO = 1000;
    private final static long FIRST_TIME_KERNEL_MODE = 200;
    private final static long FIRST_TIME_USER_MODE = 150;

    private final static long TIMESTAMP_SECOND_THREADINFO = 2000;
    private final static long SECOND_TIME_KERNEL_MODE = 400;
    private final static long SECOND_TIME_USER_MODE = 300;

    private final static Timestamp firstTimestamp = TimestampMocker.mock( 1000000 );
    private final static Timestamp secondTimestamp = TimestampMocker.mock( 2000000 );

    private final static double EXPECTED_PROCESS_CPU_EVENT_VALUE = 100.0
            * ((SECOND_TIME_KERNEL_MODE + SECOND_TIME_USER_MODE - FIRST_TIME_KERNEL_MODE - FIRST_TIME_USER_MODE))
            / (secondTimestamp.getTimeInMillis() - firstTimestamp.getTimeInMillis());

    private final static double EXPECTED_THREAD_CPU_EVENT_VALUE = 100.0
            * ((SECOND_THREAD_TIME_USER_MODE + SECOND_THREAD_TIME_KERNEL_MODE - FIRST_THREAD_TIME_KERNEL_MODE
                    - FIRST_THREAD_TIME_USER_MODE))
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
    public void testResMonForLinuxWithThreads()
    {
        createInitialResourceInfo();
        createResourceInfoForLinuxProcessWithThreads();

        ResMonDataEventDecoder resMonDataEventDecoder = new ResMonDataEventDecoder( new WriteProcessRegistryMock( initialRessourceInfo ),
                                                                                    structureAcceptor,
                                                                                    runtimeEventAcceptor,
                                                                                    dataSourceContext );

        resMonDataEventDecoder.initIfNot();
        resMonDataEventDecoder.newResMonApplicationMessageReceived( firstTimestamp, initialRessourceInfo );
        resMonDataEventDecoder.newResMonApplicationMessageReceived( secondTimestamp, resourceInfoLinuxWithThreads );

        RuntimeEventChannel<?> memSystem = runtimeEventAcceptor.getRuntimeEventChannel( "test.mem.system",
                                                                                        Double.class );
        RuntimeEventChannel<?> cpuSystem = runtimeEventAcceptor.getRuntimeEventChannel( "test.cpu.system" );

        RuntimeEventChannel<?> mem_p = runtimeEventAcceptor.getRuntimeEventChannel( "test.mem.proc." + PROCESSNAME + ":"
                + ("" + Long.toHexString( PID )).toUpperCase() );
        RuntimeEventChannel<?> cpu_p = runtimeEventAcceptor.getRuntimeEventChannel( "test.cpu.proc." + PROCESSNAME + ":"
                + ("" + Long.toHexString( PID )).toUpperCase() );

        RuntimeEventChannel<?> cpu_t = runtimeEventAcceptor.getRuntimeEventChannel( "test.cpu.proc." + PROCESSNAME + ":"
                + ("" + Long.toHexString( PID )).toUpperCase() + ".t:" + THREAD_NAME + ":"
                + ("" + Long.toHexString( TID )).toUpperCase() );

        assertNotNull( "Channel mem.system should be created", memSystem );
        assertNotNull( "Channel cpu.system should be created", cpuSystem );

        assertEquals( SYS_USED_MEM_BYTES / 1024,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( memSystem ).get( 0 ).getValue() );

        assertEquals( PROC_USED_BYTES / 1024,
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( mem_p ).get( 0 ).getValue() );

        assertEquals( formatCpuSystem( EXPECTED_PROCESS_CPU_EVENT_VALUE ),
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpu_p ).get( 0 ).getValue() );

        assertEquals( formatCpuSystem( EXPECTED_PROCESS_CPU_EVENT_VALUE ),
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpuSystem ).get( 0 ).getValue() );

        assertEquals( formatCpuSystem( EXPECTED_PROCESS_CPU_EVENT_VALUE ),
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpu_p ).get( 0 ).getValue() );

        assertEquals( formatCpuSystem( EXPECTED_PROCESS_CPU_EVENT_VALUE ),
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpuSystem ).get( 0 ).getValue() );

        assertEquals( formatCpuSystem( EXPECTED_THREAD_CPU_EVENT_VALUE ),
                      runtimeEventAcceptor.getRuntimeEventsOfRuntimeEventChannel( cpu_t ).get( 0 ).getValue() );

        assertEquals( 5, runtimeEventAcceptor.getRuntimeEventChannels().size() );
    }

    private double formatCpuSystem(double cpuConsumption)
    {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator( '.' );
        DecimalFormat df = new DecimalFormat( "#.##", symbols );
        return Double.parseDouble( df.format( cpuConsumption ) );
    }

    private void createInitialResourceInfo()
    {
        ThreadInfo.Builder threadInfoBuilder = ThreadInfo.newBuilder();

        threadInfoBuilder.setName( THREAD_NAME );
        threadInfoBuilder.setTid( TID );
        threadInfoBuilder.setTimestamp( TIMESTAMP_FIRST_THREADINFO );
        threadInfoBuilder.setTimeInUserModeMs( FIRST_THREAD_TIME_USER_MODE );
        threadInfoBuilder.setTimeInKernelModeMs( FIRST_THREAD_TIME_KERNEL_MODE );
        threadInfoBuilder.setVmBytes( THREAD_VM_BYTES );

        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();
        processInfoBuilder.setName( "InitialProcess" );
        processInfoBuilder.setPid( PID );
        processInfoBuilder.setVmUsageBytes( THREAD_VM_BYTES );
        processInfoBuilder.setTimeInUserModeMs( FIRST_TIME_USER_MODE );
        processInfoBuilder.setTimeInKernelModeMs( FIRST_TIME_KERNEL_MODE );

        processInfoBuilder.addThreads( threadInfoBuilder.build() );

        ResourceInfo.Builder resourceInfoBuilder = ResourceInfo.newBuilder();
        resourceInfoBuilder.addProcess( processInfoBuilder.build() );

        SystemInfo.Builder sysInfoBuilder = SystemInfo.newBuilder();
        sysInfoBuilder.setNumberOfCores( NOR_OF_CORES );
        sysInfoBuilder.setTotalVmMemBytes( UNUSED_METRIC );
        sysInfoBuilder.setUsedVmMemBytes( UNUSED_METRIC );
        sysInfoBuilder.setTotalPmMemBytes( SYS_TOTAL_MEM_BYTES );
        sysInfoBuilder.setUsedPmMemBytes( SYS_USED_MEM_BYTES );

        resourceInfoBuilder.setSystem( sysInfoBuilder.build() );

        initialRessourceInfo = resourceInfoBuilder.build();
    }

    private void createResourceInfoForLinuxProcessWithThreads()
    {
        ThreadInfo.Builder threadInfoBuilder = ThreadInfo.newBuilder();

        threadInfoBuilder.setName( THREAD_NAME );
        threadInfoBuilder.setTid( TID );
        threadInfoBuilder.setVmBytes( THREAD_VM_BYTES );
        threadInfoBuilder.setTimestamp( TIMESTAMP_SECOND_THREADINFO );
        threadInfoBuilder.setTimeInUserModeMs( SECOND_THREAD_TIME_USER_MODE );
        threadInfoBuilder.setTimeInKernelModeMs( SECOND_THREAD_TIME_KERNEL_MODE );

        ProcessInfo.Builder processInfoBuilder = ProcessInfo.newBuilder();
        processInfoBuilder.setName( PROCESSNAME );
        processInfoBuilder.setPid( PID );
        processInfoBuilder.setVmUsageBytes( PROC_USED_BYTES );
        processInfoBuilder.setTimeInUserModeMs( SECOND_TIME_USER_MODE );
        processInfoBuilder.setTimeInKernelModeMs( SECOND_TIME_KERNEL_MODE );

        processInfoBuilder.addThreads( threadInfoBuilder.build() );

        ResourceInfo.Builder resourceInfoBuilder = ResourceInfo.newBuilder();
        resourceInfoBuilder.addProcess( processInfoBuilder.build() );

        SystemInfo.Builder sysInfoBuilder = SystemInfo.newBuilder();
        sysInfoBuilder.setNumberOfCores( NOR_OF_CORES );
        sysInfoBuilder.setTotalVmMemBytes( UNUSED_METRIC );
        sysInfoBuilder.setUsedVmMemBytes( UNUSED_METRIC );
        sysInfoBuilder.setTotalPmMemBytes( SYS_TOTAL_MEM_BYTES );
        sysInfoBuilder.setUsedPmMemBytes( SYS_USED_MEM_BYTES );

        resourceInfoBuilder.setSystem( sysInfoBuilder.build() );

        resourceInfoLinuxWithThreads = resourceInfoBuilder.build();
    }
}
