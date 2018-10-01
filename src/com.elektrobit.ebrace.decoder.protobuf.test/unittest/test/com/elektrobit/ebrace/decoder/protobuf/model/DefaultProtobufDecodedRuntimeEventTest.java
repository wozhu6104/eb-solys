/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.decoder.protobuf.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.decoder.protobuf.model.DefaultProtobufDecodedRuntimeEvent;
import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventChannelMock;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ProcessInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.ResourceInfo.Builder;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.protobuf.TargetAgentProtocolResMon.SystemInfo;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue;

public class DefaultProtobufDecodedRuntimeEventTest
{
    private static final String PROCESS_NAME_1 = "abc";
    private static final String PROCESS_NAME_2 = "def";
    private DecodedTree resultTree;

    @Before
    public void setup()
    {
        Builder resourceInfoBuilder = ResourceInfo.newBuilder();

        SystemInfo.Builder systemInfoBuilder = SystemInfo.newBuilder();
        systemInfoBuilder.setNumberOfCores( 1 );
        systemInfoBuilder.setTotalVmMemBytes( 2 );
        systemInfoBuilder.setUsedVmMemBytes( 3 );
        systemInfoBuilder.setTotalPmMemBytes( 4 );
        systemInfoBuilder.setUsedPmMemBytes( 5 );

        resourceInfoBuilder.setSystem( systemInfoBuilder.build() );

        ProcessInfo.Builder processInfoBuilder1 = ProcessInfo.newBuilder();
        processInfoBuilder1.setPid( 6 );
        processInfoBuilder1.setName( PROCESS_NAME_1 );

        ProcessInfo.Builder processInfoBuilder2 = ProcessInfo.newBuilder();
        processInfoBuilder2.setPid( 7 );
        processInfoBuilder2.setName( PROCESS_NAME_2 );

        resourceInfoBuilder.addProcess( processInfoBuilder1.build() );
        resourceInfoBuilder.addProcess( processInfoBuilder2.build() );

        ResourceInfo resourceInfo = resourceInfoBuilder.build();

        @SuppressWarnings("unchecked")
        RuntimeEvent<ProtoMessageValue> runtimeEventMock = mock( RuntimeEvent.class );

        when( runtimeEventMock.getValue() ).thenReturn( new ProtoMessageValue( "summary", resourceInfo ) );
        when( runtimeEventMock.getRuntimeEventChannel() ).thenReturn( new RuntimeEventChannelMock<>( "channel name" ) );

        DefaultProtobufDecodedRuntimeEvent SUTdecodedRuntimeEvent = new DefaultProtobufDecodedRuntimeEvent( runtimeEventMock );
        resultTree = SUTdecodedRuntimeEvent.getDecodedTree();
    }

    @Test
    public void testHightLevelStructure() throws Exception
    {
        Assert.assertEquals( 3, resultTree.getRootNode().getChildren().size() );
        Assert.assertEquals( 5, resultTree.getRootNode().getChildren().get( 0 ).getChildren().size() );
        Assert.assertEquals( 2, resultTree.getRootNode().getChildren().get( 1 ).getChildren().size() );
        Assert.assertEquals( 2, resultTree.getRootNode().getChildren().get( 2 ).getChildren().size() );
    }

    @Test
    public void testSystemInfo() throws Exception
    {
        DecodedNode systemInfoNode = resultTree.getRootNode().getChildren().get( 0 );

        Assert.assertEquals( "system", systemInfoNode.getName() );
        Assert.assertEquals( "1", systemInfoNode.getChildren().get( 0 ).getValue() );
    }

    @Test
    public void testProcessInfo() throws Exception
    {
        DecodedNode processInfo1 = resultTree.getRootNode().getChildren().get( 1 );
        DecodedNode processInfo2 = resultTree.getRootNode().getChildren().get( 2 );

        Assert.assertEquals( "pid", processInfo1.getChildren().get( 0 ).getName() );
        Assert.assertEquals( "6", processInfo1.getChildren().get( 0 ).getValue() );
        Assert.assertEquals( "name", processInfo1.getChildren().get( 1 ).getName() );
        Assert.assertEquals( "abc", processInfo1.getChildren().get( 1 ).getValue() );

        Assert.assertEquals( "pid", processInfo2.getChildren().get( 0 ).getName() );
        Assert.assertEquals( "7", processInfo2.getChildren().get( 0 ).getValue() );
        Assert.assertEquals( "name", processInfo2.getChildren().get( 1 ).getName() );
        Assert.assertEquals( "def", processInfo2.getChildren().get( 1 ).getValue() );
    }
}
