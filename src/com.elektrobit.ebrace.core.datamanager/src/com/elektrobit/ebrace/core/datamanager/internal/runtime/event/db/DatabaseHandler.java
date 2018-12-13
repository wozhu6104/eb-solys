/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.Channel;
import de.systemticks.solys.db.sqlite.api.DataStorageAccess;
import de.systemticks.solys.db.sqlite.api.StatsItem;

public class DatabaseHandler
{

    private final DataStorageAccess access;
    private final Map<String, String> channels = new HashMap<>();
    private int globalEventId;
    private final boolean fileMode;
    private final List<BaseEvent<Double>> cpuEvents = new ArrayList<>();
    private final List<BaseEvent<Integer>> memEvents = new ArrayList<>();
    private List<Channel> channelsFromDb;
    private final static int BULK_IMPORT_THRESHOLD = 1000;
    // private final static String DB_NAME = "runtimeevent.db";
    private final static String DB_NAME = ":memory:";

    public DatabaseHandler(DataStorageAccess access)
    {
        this.access = access;
        globalEventId = 0;
        fileMode = true;
    }

    public void init()
    {
        access.openReadAndWrite( DB_NAME );
    }

    public void commit()
    {
        if (cpuEvents.size() > 0)
        {
            access.bulkImportBaseEvents( "cpu", cpuEvents, Double.class );
            cpuEvents.clear();
        }
        if (memEvents.size() > 0)
        {
            access.bulkImportBaseEvents( "mem", memEvents, Integer.class );
            memEvents.clear();
        }
        access.commit();
        if (channelsFromDb == null || channelsFromDb.size() == 0)
        {
            channelsFromDb = access.getAllChannels();
        }
    }

    public void release()
    {
        access.shutDown();
    }

    public <T> void manageDataSources(DataSourceContext context, String name, Unit<T> unit)
    {
        String key = context.getSourceName() + name;

        if (!channels.containsKey( key ))
        {
            channels.put( key, name );
        }
    }

    public <T> void manageEvent(long timestamp, RuntimeEventChannel<T> channel, T value)
    {
        if (fileMode)
        {
            String cName = channels.get( channel.getName() );
            if (cName != null)
            {
                globalEventId++;
                if (cName.startsWith( "cpu" ))
                {
                    cpuEvents.add( createCpuEvent( cName, (Double)value, globalEventId, timestamp ) );
                    if (cpuEvents.size() == BULK_IMPORT_THRESHOLD)
                    {
                        long t1 = System.currentTimeMillis();
                        // sendCpuEvents( cpuEvents.stream().collect( Collectors.toList() ) );
                        access.bulkImportBaseEvents( "cpu", cpuEvents, Double.class );
                        long t2 = System.currentTimeMillis();
                        System.out.println( "Bulk import : " + (t2 - t1) + " msec" );
                        cpuEvents.clear();
                    }
                }
                else if (cName.startsWith( "mem" ))
                {
                    memEvents.add( createMemoryEvent( cName, ((Long)value).intValue(), globalEventId, timestamp ) );
                    if (memEvents.size() == BULK_IMPORT_THRESHOLD)
                    {
                        // sendMemEvents( memEvents.stream().collect( Collectors.toList() ) );
                        access.bulkImportBaseEvents( "mem", memEvents, Integer.class );
                        memEvents.clear();
                    }
                }
                else
                {
                    // Ignore event. Not written into DB
                }
            }
        }
    }

    private void sendCpuEvents(List<BaseEvent<Double>> lcpuEvents)
    {
        new Thread( () -> {
            access.bulkImportBaseEvents( "cpu", lcpuEvents, Double.class );
        } ).start();
    }

    private void sendMemEvents(List<BaseEvent<Integer>> lmemEvents)
    {
        new Thread( () -> {
            access.bulkImportBaseEvents( "mem", lmemEvents, Integer.class );
        } ).start();
    }

    private BaseEvent<Double> createCpuEvent(String cName, double cpu, int eventId, long timestamp)
    {

        BaseEvent<Double> event = new BaseEvent<>();
        event.setChannelName( cName );
        event.setValue( cpu );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );

        return event;
    }

    private BaseEvent<Integer> createMemoryEvent(String cName, int mem, int eventId, long timestamp)
    {

        BaseEvent<Integer> event = new BaseEvent<>();
        event.setChannelName( cName );
        event.setValue( mem );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );

        return event;
    }

    private int getChannelId(String name)
    {

        for (Channel c : channelsFromDb)
        {
            if (name.endsWith( c.getName() ))
            {
                return c.getId();
            }
        }

        return -1;
    }

    private <T> List<ChartData> getAggregatedEventsFromChannel(String storage, int channelId, int aggregationTime,
            Class<T> _class)
    {
        return access.getStatisticOverTime( storage, channelId, aggregationTime / 1000, _class ).stream()
                .map( e -> new ChartData( e.getTimestamp(), (Number)e.getMaximum() ) ).collect( Collectors.toList() );

    }

    public LineChartData createLineChartDataOverview2(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {

        List<List<ChartData>> allEvents = new ArrayList<>();

        for (RuntimeEventChannel<?> c : channels)
        {
            List<ChartData> events = getAggregatedEventsFromChannel( "cpu",
                                                                    getChannelId( c.getName() ),
                                                                    aggregationTime.intValue(),
                                                                    Double.class );

            allEvents.add( events );
        }

        LineChartDataFromDB lineChartDataCreator = new LineChartDataFromDB( allEvents, channels );
        lineChartDataCreator.build();

        return lineChartDataCreator;
    }

    private <T> List<ChartData> getAllEventsFromChannel(String storage, int channelId, long startTimestamp,
            long endTimestamp, Class<T> _class)
    {
        return access.getAllEventsFromChannel( storage, channelId, startTimestamp / 1000, endTimestamp / 1000, _class )
                .stream().map( e -> new ChartData( e.getTimestamp(), (Number)e.getValue() ) )
                .collect( Collectors.toList() );
    }

    public LineChartData createLineChartDataZoom2(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {

        List<List<ChartData>> allEvents = new ArrayList<>();

        for (RuntimeEventChannel<?> c : channels)
        {
            List<ChartData> events = getAllEventsFromChannel( "cpu",
                                                             getChannelId( c.getName() ),
                                                             startTimestamp,
                                                             endTimestamp,
                                                             Double.class );

            allEvents.add( events );
        }

        LineChartDataFromDB lineChartDataCreator = new LineChartDataFromDB( allEvents, channels );
        lineChartDataCreator.build();

        return lineChartDataCreator;
    }

    public LineChartData createLineChartDataZoom(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {

        int cId = getChannelId( channels.get( 0 ).getName() );

        // List<StatsItem<Double>> aggregatedStats = access.getStatisticOverTime( "cpu", cId, (int) (aggregationTime /
        // 1000), Double.class );

        List<BaseEvent<Double>> events = access
                .getAllEventsFromChannel( "cpu", cId, startTimestamp / 1000, endTimestamp / 1000, Double.class );

        LineChartData chartData = new LineChartData()
        {

            @Override
            public List<Long> getTimestamps()
            {
                return events.stream().map( e -> e.getTimestamp() * 1000 ).collect( Collectors.toList() );
            }

            @Override
            public Map<RuntimeEventChannel<?>, List<Number>> getSeriesData()
            {
                List<Number> series = events.stream().map( e -> (Number)e.getValue() ).collect( Collectors.toList() );
                Map<RuntimeEventChannel<?>, List<Number>> result = new HashMap<>();
                result.put( channels.get( 0 ), series );
                return result;
            }

            @Override
            public double getMinValue()
            {
                return 0;
            }

            @Override
            public double getMaxValueStacked()
            {
                return events.stream().map( e -> e.getValue() ).max( Comparator.comparing( Double::valueOf ) ).get();
            }

            @Override
            public double getMaxValue()
            {
                return events.stream().map( e -> e.getValue() ).max( Comparator.comparing( Double::valueOf ) ).get();
            }
        };

        return chartData;
    }

    public LineChartData createLineChartDataOverview(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {

        int cId = getChannelId( channels.get( 0 ).getName() );

        List<StatsItem<Double>> aggregatedStats = access
                .getStatisticOverTime( "cpu", cId, (int)(aggregationTime / 1000), Double.class );

        LineChartData chartData = new LineChartData()
        {

            @Override
            public List<Long> getTimestamps()
            {
                return aggregatedStats.stream().map( e -> e.getTimestamp() * 1000 ).collect( Collectors.toList() );
            }

            @Override
            public Map<RuntimeEventChannel<?>, List<Number>> getSeriesData()
            {
                List<Number> series = aggregatedStats.stream().map( e -> (Number)e.getMaximum() )
                        .collect( Collectors.toList() );
                Map<RuntimeEventChannel<?>, List<Number>> result = new HashMap<>();
                result.put( channels.get( 0 ), series );
                return result;
            }

            @Override
            public double getMinValue()
            {
                return 0;
            }

            @Override
            public double getMaxValueStacked()
            {
                return aggregatedStats.stream().map( e -> e.getMaximum() )
                        .max( Comparator.comparing( Double::valueOf ) ).get();
            }

            @Override
            public double getMaxValue()
            {
                return aggregatedStats.stream().map( e -> e.getMaximum() )
                        .max( Comparator.comparing( Double::valueOf ) ).get();
            }
        };

        return chartData;
    }

}
