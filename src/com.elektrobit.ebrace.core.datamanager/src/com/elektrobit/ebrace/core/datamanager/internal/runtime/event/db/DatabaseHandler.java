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
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventObjectImpl;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.systemticks.solys.data.api.DataServiceHost;
import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.Channel;
import de.systemticks.solys.db.sqlite.api.DataStorageAccess;

public class DatabaseHandler
{

    private final DataStorageAccess access;
    private final Map<String, ChannelInfo> channels = new HashMap<>();
    private int globalEventId;
    private final boolean fileMode;
    private final List<BaseEvent<?>> anyEvents = new ArrayList<>();
    private List<Channel> channelsFromDb;
    private final static int BULK_IMPORT_SIZE = 1000;
    private final static String DB_NAME = ":memory:";
    private final Gson gson;
    private DataServiceHost service;

    public DatabaseHandler(DataStorageAccess access)
    {
        this.access = access;
        globalEventId = 0;
        fileMode = true;
        gson = new Gson();
    }

    public void init()
    {
        access.openReadAndWrite( DB_NAME );
        openRemoteService();
    }

    private void openRemoteService()
    {
        service = new DataServiceHost();

        Executors.newSingleThreadExecutor().execute( new Runnable()
        {
            @Override
            public void run()
            {
                service.start( access );
            }
        } );
    }

    private void stopRemoteService()
    {
        service.stop();
    }

    public void commit()
    {
        System.out.println( "commit()" );

        if (anyEvents.size() > 0)
        {
            access.bulkImportAnyBaseEvents( anyEvents );
            anyEvents.clear();
        }
        access.commit();
        if (channelsFromDb == null || channelsFromDb.size() == 0)
        {
            channelsFromDb = access.getAllChannels().stream().map( e -> toChannel( e ) ).collect( Collectors.toList() );
        }
    }

    public void release()
    {
        commit();

        long t1 = System.currentTimeMillis();
        access.backup( "backup.db" );
        access.shutDown();
        long t2 = System.currentTimeMillis();
        System.out.println( "Backup in-memory DB into file : " + (t2 - t1) + " msec" );
        stopRemoteService();
    }

    public <T> void manageDataSources(DataSourceContext context, String name, Unit<T> unit, List<String> keySet)
    {
        String key = name;

        if (context != null)
        {
            key = context.getSourceName() + name;
        }

        if (!channels.containsKey( key ))
        {
            int cId = -1;
            if (keySet == null)
            {
                cId = access.createChannel( name, unit.getDataType().getSimpleName() );
            }
            else
            {
                cId = access.createChannel( name, keySet );
            }
            channels.put( key, new ChannelInfo( unit.getDataType().getSimpleName(), name, keySet, cId ) );
        }
    }

    public <T> void manageEvent(long timestamp, RuntimeEventChannel<T> channel, T value)
    {
        if (fileMode)
        {
            ChannelInfo dbChannel = channels.get( channel.getName() );

            if (dbChannel != null)
            {
                globalEventId++;
                // primitive values
                if (dbChannel.keySet == null)
                {
                    if (dbChannel.type.contentEquals( "Double" ))
                    {
                        anyEvents.add( createDoubleEvent( dbChannel.name,
                                                          (Double)value,
                                                          globalEventId,
                                                          dbChannel.id,
                                                          timestamp ) );
                    }
                    else if (dbChannel.type.contentEquals( "Long" ))
                    {
                        anyEvents.add( createIntegerEvent( dbChannel.name,
                                                           ((Long)value).intValue(),
                                                           globalEventId,
                                                           dbChannel.id,
                                                           timestamp ) );
                    }
                    else if (dbChannel.type.contentEquals( "String" ))
                    {
                        anyEvents.add( createStringEvent( dbChannel.name,
                                                          ((String)value),
                                                          globalEventId,
                                                          dbChannel.id,
                                                          timestamp ) );
                    }
                    else
                    {
                        // drop event
                    }
                }
                // structured events
                else
                {
                    anyEvents.add( createStringEvent( dbChannel.name,
                                                      value.toString(),
                                                      globalEventId,
                                                      dbChannel.id,
                                                      timestamp ) );
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

    private String getContainer(String fullChannelname)
    {
        return fullChannelname.split( "\\." )[0];
    }

    private BaseEvent<Double> createDoubleEvent(String channelName, double doubleValue, int eventId, int cId,
            long timestamp)
    {

        BaseEvent<Double> event = new BaseEvent<>();
        event.setChannelname( channelName );
        event.setValue( doubleValue );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );
        event.setOrigin( getContainer( channelName ) );
        event.setChannelId( cId );

        return event;
    }

    private BaseEvent<Integer> createIntegerEvent(String channelName, int intValue, int eventId, int cId,
            long timestamp)
    {

        BaseEvent<Integer> event = new BaseEvent<>();
        event.setChannelname( channelName );
        event.setValue( intValue );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );
        event.setOrigin( getContainer( channelName ) );
        event.setChannelId( cId );

        return event;
    }

    private BaseEvent<String> createStringEvent(String channelName, String text, int eventId, int cId, long timestamp)
    {

        BaseEvent<String> event = new BaseEvent<>();
        event.setChannelname( channelName );
        event.setValue( text );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );
        event.setOrigin( getContainer( channelName ) );
        event.setChannelId( cId );

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

    private long milliToMicro(long milli)
    {
        return milli * 1000;
    }

    // For the charts
    public LineChartData createLineChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {
        List<List<ChartData>> allEvents = new ArrayList<>();

        // Overview Chart
        if (aggregationTime != null)
        {
            for (RuntimeEventChannel<?> c : channels)
            {
                List<ChartData> events = access
                        .getStatisticOverTime( this.channels.get( c.getName() ).storage,
                                               getChannelId( c.getName() ),
                                               (int)microToMilli( aggregationTime ) )
                        .stream().map( e -> statsItemToChartData( e ) ).collect( Collectors.toList() );

                allEvents.add( events );
            }
        }
        // Zoom Chart
        else
        {
            for (RuntimeEventChannel<?> c : channels)
            {
                List<ChartData> events = access
                        .getAllEventsFromChannel( this.channels.get( c.getName() ).storage,
                                                  getChannelId( c.getName() ),
                                                  microToMilli( startTimestamp ),
                                                  microToMilli( endTimestamp ) )
                        .stream().map( e -> baseEventToChartData( e ) ).collect( Collectors.toList() );

                allEvents.add( events );
            }
        }

        LineChartDataFromDB lineChartDataCreator = new LineChartDataFromDB( allEvents, channels );
        lineChartDataCreator.build();

        return lineChartDataCreator;
    }

    // For tables
    public List<RuntimeEvent<?>> getRuntimeEventForTimeStampIntervalForChannels(long start, long end,
            List<RuntimeEventChannel<?>> channelsList)
    {
        List<RuntimeEvent<?>> events = new ArrayList<>();

        for (RuntimeEventChannel<?> c : channelsList)
        {

            events.addAll( access
                    .getAllEventsFromChannel( this.channels.get( c.getName() ).storage,
                                              getChannelId( c.getName() ),
                                              microToMilli( start ),
                                              microToMilli( end ) )
                    .parallelStream().map( event -> toRuntimeEvent( event, c ) ).collect( Collectors.toList() ) );

        }

        return events;
    }

    private Channel toChannel(String raw)
    {
        JsonObject obj = gson.fromJson( raw, JsonElement.class ).getAsJsonObject();
        Channel ch = new Channel();
        ch.setId( obj.get( "cId" ).getAsInt() );
        ch.setName( obj.get( "cName" ).getAsString() );
        ch.setNature( obj.get( "cNature" ).getAsString() );
        ch.setType( obj.get( "cType" ).getAsString() );
        return ch;
    }

    private ChartData statsItemToChartData(String raw)
    {
        JsonObject obj = gson.fromJson( raw, JsonElement.class ).getAsJsonObject();
        return new ChartData( obj.get( "timestamp" ).getAsLong(), obj.get( "max_v" ).getAsNumber() );
    }

    private ChartData baseEventToChartData(String raw)
    {
        JsonObject obj = gson.fromJson( raw, JsonElement.class ).getAsJsonObject();
        return new ChartData( obj.get( "eTimestamp" ).getAsLong(), obj.get( "eValue" ).getAsNumber() );
    }

    private RuntimeEvent<?> toRuntimeEvent(String rawJson, RuntimeEventChannel<?> channel)
    {
        JsonObject obj = gson.fromJson( rawJson, JsonElement.class ).getAsJsonObject();
        long timestamp = milliToMicro( obj.get( "eTimestamp" ).getAsLong() );
        RuntimeEvent<?> evt = null;

        switch (channel.getUnit().getDataType().getSimpleName())
        {
            case "Double" :
                evt = new RuntimeEventObjectImpl<Double>( timestamp,
                                                          (RuntimeEventChannel<Double>)channel,
                                                          0,
                                                          obj.get( "eValue" ).getAsDouble(),
                                                          "",
                                                          null );
                break;
            case "Long" :
                evt = new RuntimeEventObjectImpl<Long>( timestamp,
                                                        (RuntimeEventChannel<Long>)channel,
                                                        0,
                                                        obj.get( "eValue" ).getAsLong(),
                                                        "",
                                                        null );
                break;
            case "String" :
                evt = new RuntimeEventObjectImpl<String>( timestamp,
                                                          (RuntimeEventChannel<String>)channel,
                                                          0,
                                                          obj.get( "eValue" ).getAsString(),
                                                          "",
                                                          null );
                break;
        }

        return evt;
    }

}

class ChannelInfo
{
    public String storage;
    public String type;
    public String name;
    public int id;
    public List<String> keySet;

    public ChannelInfo(String type, String name, List<String> keySet, int id)
    {
        super();
        this.storage = name.split( "\\." )[0];
        this.type = type;
        this.name = name;
        this.keySet = keySet;
        this.id = id;
    }

}
