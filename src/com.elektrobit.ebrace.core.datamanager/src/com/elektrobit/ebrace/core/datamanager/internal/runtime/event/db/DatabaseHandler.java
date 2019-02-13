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
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.systemticks.solys.data.api.DataServiceHost;
import de.systemticks.solys.db.sqlite.api.Channel;
import de.systemticks.solys.db.sqlite.api.DataStorageAccess;
import de.systemticks.solys.db.sqlite.api.DetailedField;
import de.systemticks.solys.db.sqlite.api.FieldMapping;
import de.systemticks.solys.db.sqlite.api.GenericJsonEvent;

public class DatabaseHandler
{

    private final DataStorageAccess access;
    private final Map<String, Channel> channels = new HashMap<>();
    private int globalEventId;
    private final boolean fileMode;
    private final List<GenericJsonEvent> anyEvents = new ArrayList<>();
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
            List<DetailedField> details = new ArrayList<>();
            FieldMapping mapping;
            int cId = -1;
            if (keySet == null)
            {
                mapping = new FieldMapping( details, unit.getDataType().getSimpleName() );
            }
            else
            {
                keySet.stream().forEach( k -> details.add( new DetailedField( k, "String", false ) ) );
                if (name.startsWith( "trace.dlt" ))
                {
                    details.remove( details.size() - 1 );
                    details.add( new DetailedField( "payload", "String", true ) );
                }
                mapping = new FieldMapping( details, "String" );
            }

            cId = access.createChannel( name, mapping );

            channels.put( key, new Channel( name, cId, mapping ) );
        }
    }

    public <T> void manageEvent(long timestamp, RuntimeEventChannel<T> channel, T value)
    {
        if (fileMode)
        {
            Channel dbChannel = channels.get( channel.getName() );

            if (dbChannel != null)
            {
                globalEventId++;
                // primitive values
                if (dbChannel.fieldMapping.getDetails().size() == 0)
                {
                    switch (dbChannel.fieldMapping.getValueType())
                    {
                        case "Double" :
                        case "Long" :
                        case "String" :
                            anyEvents.add( createGenericJsonEvent( dbChannel.name,
                                                                   value,
                                                                   globalEventId,
                                                                   dbChannel.id,
                                                                   timestamp ) );
                            break;
                        default :
                            System.out.println( "Drop Event" );
                            break;

                    }
                }
                // structured events
                else
                {
                    // FIXME - to be removed, when incoming json is already compliant
                    GenericJsonEvent event = toGenericJson( value.toString() );
                    event.setEventId( globalEventId );
                    event.setChannelId( dbChannel.id );
                    anyEvents.add( event );
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

    private GenericJsonEvent toGenericJson(String origJson)
    {
        JsonEvent srcJson = gson.fromJson( origJson, JsonEvent.class );
        GenericJsonEvent targetJson = new GenericJsonEvent();

        targetJson.setTimestamp( srcJson.getUptime() );
        targetJson.setChannel( srcJson.getChannel() );
        targetJson.setValue( srcJson.getValue().getSummary() );
        targetJson.setDetails( (JsonObject)srcJson.getValue().getDetails() );

        return targetJson;
    }

    private GenericJsonEvent createGenericJsonEvent(String channelName, Object value, int eventId, int cId,
            long timestamp)
    {

        GenericJsonEvent event = new GenericJsonEvent();
        event.setChannel( channelName );
        event.setChannelId( cId );
        event.setEventId( eventId );
        event.setTimestamp( timestamp );
        event.setValue( value );

        return event;
    }

    private int getChannelId(String name)
    {

        for (Channel c : channelsFromDb)
        {
            if (name.endsWith( c.name ))
            {
                return c.id;
            }
        }

        return -1;
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
                                               aggregationTime.intValue() )
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
                                                  startTimestamp,
                                                  endTimestamp )
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
                                              start,
                                              end )
                    .stream().map( event -> toRuntimeEvent( event, c ) ).collect( Collectors.toList() ) );

        }

        return events;
    }

    private Channel toChannel(String raw)
    {
        JsonObject obj = gson.fromJson( raw, JsonElement.class ).getAsJsonObject();
        FieldMapping mapping = gson.fromJson( obj.get( "cMapping" ).getAsString(), FieldMapping.class );
        return new Channel( obj.get( "cName" ).getAsString(), obj.get( "cId" ).getAsInt(), mapping );
    }

    private ChartData statsItemToChartData(String raw)
    {
        JsonObject obj = gson.fromJson( raw, JsonElement.class ).getAsJsonObject();
        return new ChartData( obj.get( "timestamp" ).getAsLong() / 1000, obj.get( "max_v" ).getAsNumber() );
    }

    private ChartData baseEventToChartData(String raw)
    {
        JsonObject obj = gson.fromJson( raw, JsonElement.class ).getAsJsonObject();
        return new ChartData( JsonResult.toTimestamp( obj ) / 1000, JsonResult.toNumber( obj ) );
    }

    private RuntimeEvent<?> toRuntimeEvent(String rawJson, RuntimeEventChannel<?> channel)
    {
        JsonObject obj = gson.fromJson( rawJson, JsonElement.class ).getAsJsonObject();
        long timestamp = JsonResult.toTimestamp( obj );
        RuntimeEvent<?> evt = null;

        switch (channel.getUnit().getDataType().getSimpleName())
        {
            case "Double" :
                evt = new RuntimeEventObjectImpl<Double>( timestamp,
                                                          (RuntimeEventChannel<Double>)channel,
                                                          0,
                                                          JsonResult.toDouble( obj ),
                                                          "",
                                                          null );
                break;
            case "Long" :
                evt = new RuntimeEventObjectImpl<Long>( timestamp,
                                                        (RuntimeEventChannel<Long>)channel,
                                                        0,
                                                        JsonResult.toLong( obj ),
                                                        "",
                                                        null );
                break;
            case "String" :
                evt = new RuntimeEventObjectImpl<String>( timestamp,
                                                          (RuntimeEventChannel<String>)channel,
                                                          0,
                                                          JsonResult.toString( obj ),
                                                          "",
                                                          null );
                break;
        }

        return evt;
    }

}
