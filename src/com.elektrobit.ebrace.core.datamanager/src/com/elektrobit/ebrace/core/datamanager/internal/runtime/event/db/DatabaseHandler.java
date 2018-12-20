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

public class DatabaseHandler
{

    private final DataStorageAccess access;
    private final Map<String, String> channels = new HashMap<>();
    private int globalEventId;
    private final boolean fileMode;
    // private final List<BaseEvent<Double>> cpuEvents = new ArrayList<>();
    // private final List<BaseEvent<Integer>> memEvents = new ArrayList<>();
    private final List<BaseEvent<?>> anyEvents = new ArrayList<>();
    private List<Channel> channelsFromDb;
    private final static int BULK_IMPORT_SIZE = 1000;
    // private final static String DB_NAME = "runtimeevent.db";
    private final static String DB_NAME = ":memory:";
    // private final static String DB_NAME = "multitable.db";

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
        if (anyEvents.size() > 0)
        {
            access.bulkImportAnyBaseEvents( anyEvents );
            anyEvents.clear();
        }
        access.commit();
        if (channelsFromDb == null || channelsFromDb.size() == 0)
        {
            channelsFromDb = access.getAllChannels();
        }
    }

    public void release()
    {
        long t1 = System.currentTimeMillis();
        access.backup( "backup.db" );
        access.shutDown();
        long t2 = System.currentTimeMillis();
        System.out.println( "Backup in-memory DB into file : " + (t2 - t1) + " msec" );

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
                    anyEvents.add( createCpuEvent( cName, (Double)value, globalEventId, timestamp ) );
                }
                else if (cName.startsWith( "mem" ))
                {
                    anyEvents.add( createMemoryEvent( cName, ((Long)value).intValue(), globalEventId, timestamp ) );
                }
                else
                {
                    // Ignore event. Not written into DB
                }

                if (anyEvents.size() == BULK_IMPORT_SIZE)
                {
                    long t1 = System.currentTimeMillis();
                    access.bulkImportAnyBaseEvents( anyEvents );
                    long t2 = System.currentTimeMillis();
                    System.out.println( "Bulk import : " + (t2 - t1) + " msec" );
                    anyEvents.clear();
                }
            }
        }
    }

    private BaseEvent<Double> createCpuEvent(String channelName, double cpu, int eventId, long timestamp)
    {

        BaseEvent<Double> event = new BaseEvent<>();
        event.setChannelname( channelName );
        event.setValue( cpu );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );
        event.setOrigin( "cpu" );

        return event;
    }

    private BaseEvent<Integer> createMemoryEvent(String channelName, int mem, int eventId, long timestamp)
    {

        BaseEvent<Integer> event = new BaseEvent<>();
        event.setChannelname( channelName );
        event.setValue( mem );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );
        event.setOrigin( "mem" );

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

    private long microToMilli(long micro)
    {
        return micro / 1000;
    }

    public LineChartData createLineChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {
        List<List<ChartData>> allEvents = new ArrayList<>();

        // Overview Chart
        if (aggregationTime != null)
        {
            for (RuntimeEventChannel<?> c : channels)
            {
                ChannelInfo cInfo = getChannelInfo( c.getName() );
                List<ChartData> events = access
                        .getStatisticOverTime( cInfo.storage,
                                               getChannelId( c.getName() ),
                                               (int)microToMilli( aggregationTime ),
                                               cInfo._class )
                        .stream().map( e -> new ChartData( e.getTimestamp(), (Number)e.getMaximum() ) )
                        .collect( Collectors.toList() );

                allEvents.add( events );
            }
        }
        // Zoom Chart
        else
        {
            for (RuntimeEventChannel<?> c : channels)
            {
                ChannelInfo cInfo = getChannelInfo( c.getName() );
                List<ChartData> events = access
                        .getAllEventsFromChannel( cInfo.storage,
                                                  getChannelId( c.getName() ),
                                                  microToMilli( startTimestamp ),
                                                  microToMilli( endTimestamp ),
                                                  cInfo._class )
                        .stream().map( e -> new ChartData( e.getTimestamp(), (Number)e.getValue() ) )
                        .collect( Collectors.toList() );

                allEvents.add( events );
            }
        }

        LineChartDataFromDB lineChartDataCreator = new LineChartDataFromDB( allEvents, channels );
        lineChartDataCreator.build();

        return lineChartDataCreator;
    }

    private ChannelInfo getChannelInfo(String channelName)
    {
        if (channelName.matches( ".*cpu\\..*" ))
        {
            return new ChannelInfo( "cpu", Double.class );
        }
        else if (channelName.matches( ".*mem\\..*" ))
        {
            return new ChannelInfo( "mem", Integer.class );
        }
        else
        {
            return null;
        }
    }

}

class ChannelInfo
{
    public String storage;
    public Class<?> _class;

    public ChannelInfo(String storage, Class<?> _class)
    {
        super();
        this.storage = storage;
        this._class = _class;
    }

}
