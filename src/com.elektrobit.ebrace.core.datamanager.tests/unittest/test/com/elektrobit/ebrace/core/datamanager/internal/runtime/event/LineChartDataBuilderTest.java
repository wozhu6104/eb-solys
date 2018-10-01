/// *******************************************************************************
// * Copyright (C) 2018 Elektrobit Automotive GmbH
// *
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// ******************************************************************************/
// package test.com.elektrobit.ebrace.core.datamanager.internal.runtime.event;
//
// import static org.junit.Assert.assertArrayEquals;
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertTrue;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
//
// import org.junit.Test;
//
// import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.LineChartDataBuilder;
// import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventChannelObjectImpl;
// import com.elektrobit.ebrace.dev.test.util.datamanager.RuntimeEventMocker;
// import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
// import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
// import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
// import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
//
// public class LineChartDataBuilderTest
// {
// private static final String DEFAULT_CHANNEL_NAME = "Channel";
//
// private LineChartDataBuilder chartDataBuilder;
//
// private RuntimeEventChannel<Double> createChannel(int index)
// {
// String channelName = DEFAULT_CHANNEL_NAME + index;
// return new RuntimeEventChannelObjectImpl<Double>( channelName, channelName, Unit.PERCENT );
// }
//
// private List<RuntimeEventChannel<?>> createChannels(int numOfChannels)
// {
// List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
// for (int i = 0; i < numOfChannels; i++)
// {
// channels.add( createChannel( i ) );
// }
// return channels;
// }
//
// @Test(expected = NullPointerException.class)
// public void createWithNull()
// {
// chartDataBuilder = new LineChartDataBuilder( null, 0L, 0L, null, false, 0L, false );
// chartDataBuilder.build();
// }
//
// @Test
// public void createWithEmptyChannelsAndEmptyEventsReturnsEmptyChart()
// {
// List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// long startTimestamp = 0L, endTimestamp = 0L, aggregationTime = 0L;
// boolean dataAsBars = false, aggregateForStackedMode = false;
//
// chartDataBuilder = new LineChartDataBuilder( channels,
// startTimestamp,
// endTimestamp,
// sortedEvents,
// dataAsBars,
// aggregationTime,
// aggregateForStackedMode );
// LineChartData data = chartDataBuilder.build();
//
// assertNotNull( data );
// assertEquals( 0.0, data.getMinValue(), 0.0 );
// assertEquals( 0.0, data.getMaxValue(), 0.0 );
// assertEquals( 0.0, data.getMaxValueStacked(), 0.0 );
// assertNotNull( data.getTimestamps() );
// assertEquals( 1, data.getTimestamps().size() );
// assertNotNull( data.getSeriesData() );
// assertEquals( channels.size(), data.getSeriesData().size() );
// }
//
// @Test
// public void createWithOneChannelAndEmptyEventsReturnsEmptyChart()
// {
// List<RuntimeEventChannel<?>> channels = createChannels( 1 );
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// long startTimestamp = 0L, endTimestamp = 0L, aggregationTime = 0L;
// boolean dataAsBars = false, aggregateForStackedMode = false;
//
// chartDataBuilder = new LineChartDataBuilder( channels,
// startTimestamp,
// endTimestamp,
// sortedEvents,
// dataAsBars,
// aggregationTime,
// aggregateForStackedMode );
// LineChartData data = chartDataBuilder.build();
//
// assertNotNull( data );
// assertEquals( 0.0, data.getMinValue(), 0.0 );
// assertEquals( 0.0, data.getMaxValue(), 0.0 );
// assertEquals( 0.0, data.getMaxValueStacked(), 0.0 );
// assertNotNull( data.getTimestamps() );
// assertEquals( 1, data.getTimestamps().size() );
// assertNotNull( data.getSeriesData() );
// assertEquals( channels.size(), data.getSeriesData().size() );
// }
//
// @Test
// public void createWithTwoChannelsAndEmptyEventsReturnsEmptyChart()
// {
// List<RuntimeEventChannel<?>> channels = createChannels( 2 );
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// long startTimestamp = 0L, endTimestamp = 0L, aggregationTime = 0L;
// boolean dataAsBars = false, aggregateForStackedMode = false;
//
// chartDataBuilder = new LineChartDataBuilder( channels,
// startTimestamp,
// endTimestamp,
// sortedEvents,
// dataAsBars,
// aggregationTime,
// aggregateForStackedMode );
// LineChartData data = chartDataBuilder.build();
//
// assertNotNull( data );
// assertEquals( 0.0, data.getMinValue(), 0.0 );
// assertEquals( 0.0, data.getMaxValue(), 0.0 );
// assertEquals( 0.0, data.getMaxValueStacked(), 0.0 );
// assertNotNull( data.getTimestamps() );
// assertEquals( 1, data.getTimestamps().size() );
// assertNotNull( data.getSeriesData() );
// assertEquals( channels.size(), data.getSeriesData().size() );
// }
//
// @Test
// public void createWithOneChannelAndSomeEventsReturnsChartWithPoints()
// {
// double value1 = 23.0;
// double value2 = 32.0;
// double value3 = 46.0;
// List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
// RuntimeEventChannel<Double> channel = createChannel( 0 );
// channels.add( channel );
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// sortedEvents.add( RuntimeEventMocker.mock( 0L, value1, channel ) );
// sortedEvents.add( RuntimeEventMocker.mock( 0L, value2, channel ) );
// sortedEvents.add( RuntimeEventMocker.mock( 0L, value3, channel ) );
// long startTimestamp = 0L, endTimestamp = 0L;
// boolean dataAsBars = false, aggregateForStackedMode = false;
//
// chartDataBuilder = new LineChartDataBuilder( channels,
// startTimestamp,
// endTimestamp,
// sortedEvents,
// dataAsBars,
// null,
// aggregateForStackedMode );
// LineChartData data = chartDataBuilder.build();
//
// assertNotNull( data );
// assertEquals( value1, data.getMinValue(), 0.0 );
// assertEquals( value3, data.getMaxValue(), 0.0 );
// assertEquals( value1 + value2 + value3, data.getMaxValueStacked(), 0.0 );
// assertNotNull( data.getTimestamps() );
// assertEquals( 3, data.getTimestamps().size() );
// assertNotNull( data.getSeriesData() );
// assertEquals( channels.size(), data.getSeriesData().size() );
//
// Map<RuntimeEventChannel<?>, List<Number>> seriesData = data.getSeriesData();
// List<Number> listOfValues = seriesData.values().iterator().next();
// assertArrayEquals( new Double[]{value1, value2, value3}, listOfValues.toArray() );
// }
//
// @Test
// /**
// * This test is related to ticket EBRACE-3122
// */
// public void checkIfIntervalAddsPointsToBorders()
// {
// int value1 = 0;
// int value2 = 50;
// int value3 = 100;
// long timestamp1 = 0;
// long timestamp2 = 5000000;
// long timestamp3 = 10000000;
// List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
// RuntimeEventChannel<Integer> channel = new RuntimeEventChannelObjectImpl<Integer>( DEFAULT_CHANNEL_NAME,
// "",
// Unit.createCustomUnit( "Integer Unit",
// Integer.class ) );
// channels.add( channel );
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// sortedEvents.add( RuntimeEventMocker.mock( timestamp1, value1, channel ) );
// sortedEvents.add( RuntimeEventMocker.mock( timestamp2, value2, channel ) );
// sortedEvents.add( RuntimeEventMocker.mock( timestamp3, value3, channel ) );
// chartDataBuilder = new LineChartDataBuilder( channels, 238569L, 3238569L, sortedEvents, true, null, false );
// LineChartData data = chartDataBuilder.build();
//
// assertNotNull( data );
// assertEquals( value1, data.getMinValue(), 0.0 );
// assertEquals( value2, data.getMaxValue(), 0.0 );
// assertEquals( value2, data.getMaxValueStacked(), 0.0 );
// assertNotNull( data.getTimestamps() );
// assertEquals( 4, data.getTimestamps().size() );
// assertNotNull( data.getSeriesData() );
// assertEquals( channels.size(), data.getSeriesData().size() );
//
// Map<RuntimeEventChannel<?>, List<Number>> seriesData = data.getSeriesData();
// List<Number> listOfValues = seriesData.values().iterator().next();
// assertArrayEquals( new Integer[]{null, value1, value2, value2}, listOfValues.toArray() );
// }
//
// @Test
// public void zeroChannelMustBeRemoved()
// {
// long timestamp1 = 0;
// long timestamp2 = 5000000;
// long timestamp3 = 10000000;
// List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
// RuntimeEventChannel<Integer> channel = new RuntimeEventChannelObjectImpl<Integer>( DEFAULT_CHANNEL_NAME,
// "",
// Unit.createCustomUnit( "Integer Unit",
// Integer.class ) );
// RuntimeEventChannel<Integer> zeroChannel = new RuntimeEventChannelObjectImpl<Integer>( DEFAULT_CHANNEL_NAME,
// "",
// Unit.createCustomUnit( "Integer Unit",
// Integer.class ) );
// channels.add( channel );
// channels.add( zeroChannel );
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// sortedEvents.add( RuntimeEventMocker.mock( timestamp1, 0, channel ) );
// sortedEvents.add( RuntimeEventMocker.mock( timestamp1, 0, zeroChannel ) );
// sortedEvents.add( RuntimeEventMocker.mock( timestamp2, 50, channel ) );
// sortedEvents.add( RuntimeEventMocker.mock( timestamp2, 0, zeroChannel ) );
// sortedEvents.add( RuntimeEventMocker.mock( timestamp3, 100, channel ) );
// chartDataBuilder = new LineChartDataBuilder( channels, 0L, timestamp3 + 1L, sortedEvents, true, null, false );
// LineChartData result = chartDataBuilder.build();
//
// assertNotNull( result );
// Set<RuntimeEventChannel<?>> channelsInResult = result.getSeriesData().keySet();
// assertEquals( 1, channelsInResult.size() );
// assertTrue( channelsInResult.contains( channel ) );
// }
//
// @Test
// public void aggregationWithMissingValues() throws Exception
// {
// List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>();
// RuntimeEventChannel<Integer> channel1 = new RuntimeEventChannelObjectImpl<Integer>( "channel1",
// "",
// Unit.createCustomUnit( "Integer Unit",
// Integer.class ) );
// RuntimeEventChannel<Integer> channel2 = new RuntimeEventChannelObjectImpl<Integer>( "channel2",
// "",
// Unit.createCustomUnit( "Integer Unit",
// Integer.class ) );
// channels.add( channel1 );
// channels.add( channel2 );
//
// List<RuntimeEvent<?>> sortedEvents = new ArrayList<RuntimeEvent<?>>();
// sortedEvents.add( RuntimeEventMocker.mock( 0L, 10, channel1 ) );
//
// sortedEvents.add( RuntimeEventMocker.mock( 1000L, 20, channel1 ) );
// sortedEvents.add( RuntimeEventMocker.mock( 1000L, 100, channel2 ) );
//
// sortedEvents.add( RuntimeEventMocker.mock( 2000L, 30, channel1 ) );
// sortedEvents.add( RuntimeEventMocker.mock( 2000L, 110, channel2 ) );
//
// sortedEvents.add( RuntimeEventMocker.mock( 3000L, 40, channel1 ) );
// sortedEvents.add( RuntimeEventMocker.mock( 3000L, 110, channel2 ) );
//
// sortedEvents.add( RuntimeEventMocker.mock( 4000L, 50, channel1 ) );
// sortedEvents.add( RuntimeEventMocker.mock( 4000L, 120, channel2 ) );
//
// sortedEvents.add( RuntimeEventMocker.mock( 5000L, 60, channel1 ) );
// sortedEvents.add( RuntimeEventMocker.mock( 5000L, 130, channel2 ) );
//
// chartDataBuilder = new LineChartDataBuilder( channels, 0L, 4000L, sortedEvents, false, 2200L, true );
// LineChartData data = chartDataBuilder.build();
// Map<RuntimeEventChannel<?>, List<Number>> seriesData = data.getSeriesData();
//
// System.out.println( "" );
// System.out.println( "TEST LOG" );
// System.out.println( "Timestamps " + data.getTimestamps() );
// for (RuntimeEventChannel<?> channel : seriesData.keySet())
// {
// List<Number> values = seriesData.get( channel );
// System.out.println( channel.getName() + " " + values );
// }
// data.getMaxValue();
// }
//
// }
