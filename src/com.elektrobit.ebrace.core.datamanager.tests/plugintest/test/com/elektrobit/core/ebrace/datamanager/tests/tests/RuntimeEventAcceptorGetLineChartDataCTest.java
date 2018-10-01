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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class RuntimeEventAcceptorGetLineChartDataCTest extends RuntimeEventAcceptorAbstractCTest
{

    private RuntimeEventChannel<Double> uselessChannel;
    private RuntimeEventChannel<Double> channel1;
    private RuntimeEventChannel<Double> channel2;
    private RuntimeEventChannel<Double> channelToAggregate1;
    private RuntimeEventChannel<Double> channelToAggregate2;
    private RuntimeEventChannel<Integer> integerChannel;
    private RuntimeEventChannel<Double> doubleChannel;

    public RuntimeEventAcceptorGetLineChartDataCTest(int waitForCommitTime)
    {
        super( waitForCommitTime );
    }

    @Before
    public void createTestData()
    {
        uselessChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "useless channel", Unit.PERCENT, "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, uselessChannel, ModelElement.NULL_MODEL_ELEMENT, 1.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, uselessChannel, ModelElement.NULL_MODEL_ELEMENT, 19.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, uselessChannel, ModelElement.NULL_MODEL_ELEMENT, 299.0 );

        channel1 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "channel1", Unit.PERCENT, "" );
        runtimeEventAcceptor.acceptEventMicros( 999, channel1, ModelElement.NULL_MODEL_ELEMENT, 11.0 );
        runtimeEventAcceptor.acceptEventMicros( 1000, channel1, ModelElement.NULL_MODEL_ELEMENT, 12.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, channel1, ModelElement.NULL_MODEL_ELEMENT, 13.0 );

        channel2 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "channel2", Unit.PERCENT, "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channel2, ModelElement.NULL_MODEL_ELEMENT, 21.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, channel2, ModelElement.NULL_MODEL_ELEMENT, 22.0 );
        runtimeEventAcceptor.acceptEventMicros( 1003, channel2, ModelElement.NULL_MODEL_ELEMENT, 23.0 );
    }

    @Test
    public void testGetChartData()
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( new RuntimeEventChannel<?>[]{channel1, channel2} );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 1001, 1005, false, null, false );

        Assert.assertEquals( 23.0, chartData.getMaxValue(), 0.0 );
        Assert.assertEquals( 12.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 33.0, chartData.getMaxValueStacked(), 0.0 );

        Assert.assertArrayEquals( new Long[]{1000L, 1001L, 1002L, 1003L}, chartData.getTimestamps().toArray() );

        Assert.assertEquals( 2, chartData.getSeriesData().values().size() );

        List<Number> channel1ChartValues = chartData.getSeriesData().get( channel1 );
        Assert.assertArrayEquals( new Number[]{12.0, 13.0, null, null}, channel1ChartValues.toArray() );

        List<Number> channel2ChartValues = chartData.getSeriesData().get( channel2 );
        Assert.assertArrayEquals( new Number[]{21.0, null, 22.0, 23.0}, channel2ChartValues.toArray() );
    }

    @Test
    public void testNoDataInSelectedTimeSpan()
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( new RuntimeEventChannel<?>[]{channel1, channel2} );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 2000, 2005, false, null, false );

        Assert.assertEquals( 0.0, chartData.getMaxValue(), 0.0 );
        Assert.assertEquals( 0.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 0.0, chartData.getMaxValueStacked(), 0.0 );

        Assert.assertArrayEquals( new Long[]{2000L}, chartData.getTimestamps().toArray() );

        Assert.assertEquals( 2, chartData.getSeriesData().values().size() );

        List<Number> channel1ChartValues = chartData.getSeriesData().get( channel1 );
        Assert.assertArrayEquals( new Number[]{0.0}, channel1ChartValues.toArray() );

        List<Number> channel2ChartValues = chartData.getSeriesData().get( channel2 );
        Assert.assertArrayEquals( new Number[]{0.0}, channel2ChartValues.toArray() );
    }

    @Test
    public void testGetChartDataAsBars()
    {
        List<RuntimeEventChannel<?>> channels = Arrays.asList( new RuntimeEventChannel<?>[]{channel1, channel2} );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 1001, 1002, true, null, false );

        Assert.assertEquals( 23.0, chartData.getMaxValue(), 0.0 );
        Assert.assertEquals( 12.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 33.0, chartData.getMaxValueStacked(), 0.0 );

        Assert.assertArrayEquals( new Long[]{1000L, 1000L, 1000L, 1001L, 1001L, 1002L, 1002L, 1003L},
                                  chartData.getTimestamps().toArray() );

        Assert.assertEquals( 2, chartData.getSeriesData().values().size() );

        List<Number> channel1ChartValues = chartData.getSeriesData().get( channel1 );
        Assert.assertArrayEquals( new Number[]{null, 12.0, 13.0, 13.0, null, null, null, null},
                                  channel1ChartValues.toArray() );

        List<Number> channel2ChartValues = chartData.getSeriesData().get( channel2 );
        Assert.assertArrayEquals( new Number[]{null, 21.0, null, null, 22.0, 22.0, 23.0, 23.0},
                                  channel2ChartValues.toArray() );
    }

    @Test
    public void testAggregation() throws Exception
    {
        createAggregationTestData();
        List<RuntimeEventChannel<?>> channels = Arrays
                .asList( new RuntimeEventChannel<?>[]{channelToAggregate1, channelToAggregate2} );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 1000, 2009, false, 5L, false );
        Assert.assertEquals( 10.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 129.0, chartData.getMaxValue(), 0.0 );

        List<Number> channel1AggregatedChartValues = chartData.getSeriesData().get( channelToAggregate1 );
        Assert.assertArrayEquals( new Number[]{10.0, 19.0, 20.0, 29.0, null, null},
                                  channel1AggregatedChartValues.toArray() );

        List<Number> channel2AggregatedChartValues = chartData.getSeriesData().get( channelToAggregate2 );
        Assert.assertArrayEquals( new Number[]{110.0, 119.0, null, null, 120.0, 129.0},
                                  channel2AggregatedChartValues.toArray() );

        Assert.assertArrayEquals( new Long[]{1002L, 1002L, 1007L, 1007L, 2007L, 2007L},
                                  chartData.getTimestamps().toArray() );
    }

    @Test
    public void testAggregationWithStacked() throws Exception
    {
        createAggregationWithStackedTestData();
        List<RuntimeEventChannel<?>> channels = Arrays
                .asList( new RuntimeEventChannel<?>[]{channelToAggregate1, channelToAggregate2} );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 1000, 2009, false, 5L, true );
        Assert.assertEquals( 25.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 150.0, chartData.getMaxValue(), 0.0 );
        Assert.assertEquals( 175.0, chartData.getMaxValueStacked(), 0.0 );

        List<Number> channel1AggregatedChartValues = chartData.getSeriesData().get( channelToAggregate1 );
        Assert.assertArrayEquals( new Number[]{25.0, 28.0}, channel1AggregatedChartValues.toArray() );

        List<Number> channel2AggregatedChartValues = chartData.getSeriesData().get( channelToAggregate2 );
        Assert.assertArrayEquals( new Number[]{150.0, 140.0}, channel2AggregatedChartValues.toArray() );

        Assert.assertArrayEquals( new Long[]{1002L, 1007L}, chartData.getTimestamps().toArray() );

    }

    @Test
    public void testAggregationWithStackedSparslyEvents() throws Exception
    {
        createAggregationWithStackedSparseTestData();
        List<RuntimeEventChannel<?>> channels = Arrays
                .asList( new RuntimeEventChannel<?>[]{channelToAggregate1, channelToAggregate2} );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 1000, 1200, false, 5L, true );
        Assert.assertEquals( 0.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 125.0, chartData.getMaxValue(), 0.0 );
        Assert.assertEquals( 150.0, chartData.getMaxValueStacked(), 0.0 );

        List<Number> channel1AggregatedChartValues = chartData.getSeriesData().get( channelToAggregate1 );
        Assert.assertArrayEquals( new Number[]{12.0, 0.0, 25.0}, channel1AggregatedChartValues.toArray() );

        List<Number> channel2AggregatedChartValues = chartData.getSeriesData().get( channelToAggregate2 );
        Assert.assertArrayEquals( new Number[]{null, null, 125.0}, channel2AggregatedChartValues.toArray() );

        Assert.assertArrayEquals( new Long[]{1000L, 1010L, 1100L}, chartData.getTimestamps().toArray() );
    }

    @Test
    public void testAggregationWithMixedTypes() throws Exception
    {
        createAggregationWithStackedMixedTypesTestData();
        List<RuntimeEventChannel<?>> channels = Arrays
                .asList( new RuntimeEventChannel<?>[]{integerChannel, doubleChannel} );
        channels = new ArrayList<>( channels );
        LineChartData chartData = runtimeEventAcceptor.getLineChartData( channels, 1000, 2009, false, 5L, true );
        Assert.assertEquals( 25.0, chartData.getMinValue(), 0.0 );
        Assert.assertEquals( 150.0, chartData.getMaxValue(), 0.0 );
        Assert.assertEquals( 175.0, chartData.getMaxValueStacked(), 0.0 );

        List<Number> channel1AggregatedChartValues = chartData.getSeriesData().get( integerChannel );
        Assert.assertArrayEquals( new Number[]{25, 28}, channel1AggregatedChartValues.toArray() );

        List<Number> channel2AggregatedChartValues = chartData.getSeriesData().get( doubleChannel );
        Assert.assertArrayEquals( new Number[]{150.0, 140.0}, channel2AggregatedChartValues.toArray() );

        Assert.assertArrayEquals( new Long[]{1002L, 1007L}, chartData.getTimestamps().toArray() );
    }

    private void createAggregationTestData()
    {
        channelToAggregate1 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate1",
                                                                                   Unit.PERCENT,
                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 12.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 10.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 13.0 );
        runtimeEventAcceptor.acceptEventMicros( 1003, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 19.0 );
        runtimeEventAcceptor.acceptEventMicros( 1004, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 15.0 );
        runtimeEventAcceptor.acceptEventMicros( 1005, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 24.0 );
        runtimeEventAcceptor.acceptEventMicros( 1006, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 28.0 );
        runtimeEventAcceptor.acceptEventMicros( 1007, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 20.0 );
        runtimeEventAcceptor.acceptEventMicros( 1008, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 29.0 );
        runtimeEventAcceptor.acceptEventMicros( 1009, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 28.0 );

        channelToAggregate2 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate2",
                                                                                   Unit.PERCENT,
                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 112.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 110.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 113.0 );
        runtimeEventAcceptor.acceptEventMicros( 1003, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 119.0 );
        runtimeEventAcceptor.acceptEventMicros( 1004, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 115.0 );
        runtimeEventAcceptor.acceptEventMicros( 2005, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 124.0 );
        runtimeEventAcceptor.acceptEventMicros( 2006, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 128.0 );
        runtimeEventAcceptor.acceptEventMicros( 2007, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 120.0 );
        runtimeEventAcceptor.acceptEventMicros( 2008, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 129.0 );
        runtimeEventAcceptor.acceptEventMicros( 2009, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 128.0 );
    }

    private void createAggregationWithStackedTestData()
    {
        channelToAggregate1 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate1",
                                                                                   Unit.PERCENT,
                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 12.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 0.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 26.0 );
        runtimeEventAcceptor.acceptEventMicros( 1003, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 19.0 );
        runtimeEventAcceptor.acceptEventMicros( 1004, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 25.0 );
        runtimeEventAcceptor.acceptEventMicros( 1005, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 24.0 );
        runtimeEventAcceptor.acceptEventMicros( 1006, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 28.0 );
        runtimeEventAcceptor.acceptEventMicros( 1007, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 20.0 );
        runtimeEventAcceptor.acceptEventMicros( 1008, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 29.0 );
        runtimeEventAcceptor.acceptEventMicros( 1009, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 28.0 );

        channelToAggregate2 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate2",
                                                                                   Unit.PERCENT,
                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 112.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 151.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 113.0 );
        runtimeEventAcceptor.acceptEventMicros( 1003, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 119.0 );
        runtimeEventAcceptor.acceptEventMicros( 1004, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 150.0 );
        runtimeEventAcceptor.acceptEventMicros( 1005, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 124.0 );
        runtimeEventAcceptor.acceptEventMicros( 1006, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 140.0 );
        runtimeEventAcceptor.acceptEventMicros( 1007, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 120.0 );
        runtimeEventAcceptor.acceptEventMicros( 1008, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 109.0 );
        runtimeEventAcceptor.acceptEventMicros( 1009, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 128.0 );
    }

    private void createAggregationWithStackedSparseTestData()
    {
        channelToAggregate1 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate1",
                                                                                   Unit.PERCENT,
                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 12.0 );
        runtimeEventAcceptor.acceptEventMicros( 1010, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 0.0 );
        runtimeEventAcceptor.acceptEventMicros( 1100, channelToAggregate1, ModelElement.NULL_MODEL_ELEMENT, 25.0 );

        channelToAggregate2 = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate2",
                                                                                   Unit.PERCENT,
                                                                                   "" );
        runtimeEventAcceptor.acceptEventMicros( 1100, channelToAggregate2, ModelElement.NULL_MODEL_ELEMENT, 125.0 );
    }

    private void createAggregationWithStackedMixedTypesTestData()
    {
        Unit<Integer> integerUnit = Unit.createCustomUnit( "integer unit", Integer.class );
        integerChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate1", integerUnit, "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 12 );
        runtimeEventAcceptor.acceptEventMicros( 1001, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 26 );
        runtimeEventAcceptor.acceptEventMicros( 1003, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 19 );
        runtimeEventAcceptor.acceptEventMicros( 1004, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 25 );
        runtimeEventAcceptor.acceptEventMicros( 1005, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 24 );
        runtimeEventAcceptor.acceptEventMicros( 1006, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 28 );
        runtimeEventAcceptor.acceptEventMicros( 1007, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 20 );
        runtimeEventAcceptor.acceptEventMicros( 1008, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 29 );
        runtimeEventAcceptor.acceptEventMicros( 1009, integerChannel, ModelElement.NULL_MODEL_ELEMENT, 28 );

        doubleChannel = runtimeEventAcceptor.createOrGetRuntimeEventChannel( "chanToAggregate2", Unit.PERCENT, "" );
        runtimeEventAcceptor.acceptEventMicros( 1000, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 112.0 );
        runtimeEventAcceptor.acceptEventMicros( 1001, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 151.0 );
        runtimeEventAcceptor.acceptEventMicros( 1002, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 113.0 );
        runtimeEventAcceptor.acceptEventMicros( 1003, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 119.0 );
        runtimeEventAcceptor.acceptEventMicros( 1004, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 150.0 );
        runtimeEventAcceptor.acceptEventMicros( 1005, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 124.0 );
        runtimeEventAcceptor.acceptEventMicros( 1006, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 140.0 );
        runtimeEventAcceptor.acceptEventMicros( 1007, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 120.0 );
        runtimeEventAcceptor.acceptEventMicros( 1008, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 109.0 );
        runtimeEventAcceptor.acceptEventMicros( 1009, doubleChannel, ModelElement.NULL_MODEL_ELEMENT, 128.0 );
    }
}
