/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.core.ebrace.datamanager.tests.tests;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.GanttChartEntryImpl;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartEntry;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetGanttChartDataCTest extends RuntimeEventAcceptorAbstractCTest
{
    public RuntimeEventAcceptorGetGanttChartDataCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    private RuntimeEventChannel<Boolean> longEventChannel;
    private RuntimeEventChannel<Boolean> channelToAggregate;
    private RuntimeEventChannel<Boolean> channel1;
    private RuntimeEventChannel<Boolean> channel2;
    private RuntimeEventChannel<Boolean> channel3;

    @Before
    public void createTestData()
    {
        longEventChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "longEventChannel", Unit.BOOLEAN, "" );
        runtimeEventAcceptor.acceptEventMicros( 200, longEventChannel, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 300, longEventChannel, ModelElement.NULL_MODEL_ELEMENT, false );
        runtimeEventAcceptor.acceptEventMicros( 500, longEventChannel, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1100, longEventChannel, ModelElement.NULL_MODEL_ELEMENT, false );

        channelToAggregate = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "channel ag", Unit.BOOLEAN, "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1003, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, false );

        runtimeEventAcceptor.acceptEventMicros( 1005, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1010, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, false );

        runtimeEventAcceptor.acceptEventMicros( 1012, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1015, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, false );

        runtimeEventAcceptor.acceptEventMicros( 1100, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1200, channelToAggregate, ModelElement.NULL_MODEL_ELEMENT, false );

        channel1 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "channel1", Unit.BOOLEAN, "" );
        runtimeEventAcceptor.acceptEventMicros( 999, channel1, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1000, channel1, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1001, channel1, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1002, channel1, ModelElement.NULL_MODEL_ELEMENT, false );

        channel2 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "channel2", Unit.BOOLEAN, "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channel2, ModelElement.NULL_MODEL_ELEMENT, false );
        runtimeEventAcceptor.acceptEventMicros( 1002, channel2, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1003, channel2, ModelElement.NULL_MODEL_ELEMENT, false );
        runtimeEventAcceptor.acceptEventMicros( 1004, channel2, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1005, channel2, ModelElement.NULL_MODEL_ELEMENT, true );

        channel3 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "channel3", Unit.BOOLEAN, "" );
        runtimeEventAcceptor.acceptEventMicros( 998, channel3, ModelElement.NULL_MODEL_ELEMENT, false );
        runtimeEventAcceptor.acceptEventMicros( 999, channel3, ModelElement.NULL_MODEL_ELEMENT, true );
        runtimeEventAcceptor.acceptEventMicros( 1004, channel3, ModelElement.NULL_MODEL_ELEMENT, false );
        runtimeEventAcceptor.acceptEventMicros( 1005, channel3, ModelElement.NULL_MODEL_ELEMENT, false );
    }

    @Test
    public void testGetChartData()
    {
        List<RuntimeEventChannel<?>> channels = Arrays
                .asList( new RuntimeEventChannel<?>[]{channel1, channel2, channel3} );
        GanttChartData chartData = runtimeEventAcceptor.getGanttChartData( channels, 1000, 1005, null );

        Assert.assertEquals( 2, chartData.getData().size() );

        GanttChartEntryImpl[] expectedArray0 = new GanttChartEntryImpl[]{new GanttChartEntryImpl( 999, 1002 ),
                new GanttChartEntryImpl( 1002, 1003 ), new GanttChartEntryImpl( 999, 1004 )};
        Assert.assertArrayEquals( expectedArray0, chartData.getData().get( 0 ) );

        GanttChartEntryImpl[] expectedArray1 = new GanttChartEntryImpl[]{null, new GanttChartEntryImpl( 1004, 1005 ),
                null};
        Assert.assertArrayEquals( expectedArray1, chartData.getData().get( 1 ) );
    }

    @Test
    public void testGetChannels()
    {
        List<RuntimeEventChannel<?>> channels = Arrays
                .asList( new RuntimeEventChannel<?>[]{channel1, channel2, channel3} );
        GanttChartData chartData = runtimeEventAcceptor.getGanttChartData( channels, 1000, 1005, null );

        Assert.assertEquals( channels, chartData.getChannels() );
    }

    @Test
    public void testAggreagation() throws Exception
    {
        List<RuntimeEventChannel<?>> channel = Arrays.asList( new RuntimeEventChannel<?>[]{channelToAggregate} );
        long aggregationTime = 10;
        GanttChartData chartData = runtimeEventAcceptor.getGanttChartData( channel, 1000, 1200, aggregationTime );

        Assert.assertEquals( 3, chartData.getData().size() );

        GanttChartEntry firstInterval = chartData.getData().get( 0 )[0];
        GanttChartEntry secondInterval = chartData.getData().get( 1 )[0];
        GanttChartEntry thirdInterval = chartData.getData().get( 2 )[0];

        Assert.assertEquals( new GanttChartEntryImpl( 1000, 1010 ), firstInterval );
        Assert.assertEquals( new GanttChartEntryImpl( 1012, 1015 ), secondInterval );
        Assert.assertEquals( new GanttChartEntryImpl( 1100, 1200 ), thirdInterval );
    }

    @Test
    public void testStartEndOutsideOfSelectedTime() throws Exception
    {
        List<RuntimeEventChannel<?>> channel = Arrays.asList( new RuntimeEventChannel<?>[]{longEventChannel} );
        GanttChartData chartData = runtimeEventAcceptor.getGanttChartData( channel, 600, 700, null );

        Assert.assertEquals( 1, chartData.getData().size() );
        GanttChartEntry interval = chartData.getData().get( 0 )[0];
        Assert.assertEquals( new GanttChartEntryImpl( 500, 1100 ), interval );
    }
}
