/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.reset;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dev.test.util.datamanager.MockedTimestampCreator;
import com.elektrobit.ebrace.dev.test.util.memory.CyclicMemoryChecker;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.SystemInfo;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext.SOURCE_TYPE;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DynamicTargetAdaptorResult;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

public class ResetMemoryTest
{
    private Timestamp timestamp;
    private ResetNotifier resetNotfier;
    private TargetAdapter resourceMonitorController;

    @Before
    public void setup()
    {
        resetNotfier = CoreServiceHelper.getResetNotifier();
        TargetAdaptorFactory factory = CoreServiceHelper.getResourceMonitorControllerFactory();
        DynamicTargetAdaptorResult result = factory
                .createNewInstance( new DataSourceContext( SOURCE_TYPE.FILE, "test." ) );
        resourceMonitorController = result.getAdaptor();
    }

    @Test
    public void isMemoryConstantAfterResetOf1000Messages() throws Exception
    {
        final byte[] testMessageInBytes = createInitialResourceInfo().toByteArray();

        Runnable testCode = new Runnable()
        {

            @Override
            public void run()
            {

                for (int i = 0; i < 1000; i++)
                {
                    timestamp = new TimestampMock( i );

                    resourceMonitorController.onProtocolMessageReceived( timestamp,
                                                                         MessageType.MSG_TYPE_RESOURCE_MONITOR,
                                                                         testMessageInBytes,
                                                                         new MockedTimestampCreator() );
                    timestamp = null;
                }
                resetNotfier.performReset();

            }
        };

        MatcherAssert.assertThat( new CyclicMemoryChecker( true ).heapSizeStdDevInPercent( testCode ),
                                  Matchers.lessThan( 0.01 ) );

    }

    private ResourceInfo createInitialResourceInfo()
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
