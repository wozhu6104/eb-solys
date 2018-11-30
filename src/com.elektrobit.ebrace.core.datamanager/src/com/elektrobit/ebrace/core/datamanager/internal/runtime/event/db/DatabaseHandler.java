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

import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

import de.systemticks.solys.db.sqlite.api.BaseEvent;
import de.systemticks.solys.db.sqlite.api.DataStorageAccess;

public class DatabaseHandler
{

    private final DataStorageAccess access;
    private final Map<String, String> channels = new HashMap<>();
    private int globalEventId;
    private final boolean fileMode;
    private final List<BaseEvent<Double>> cpuEvents = new ArrayList<>();
    private final List<BaseEvent<Integer>> memEvents = new ArrayList<>();
    private final static int BULK_IMPORT_THRESHOLD = 1000;
    private final static String DB_NAME = "runtimeevent.db";

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

    public void release()
    {
        if (cpuEvents.size() > 0)
        {
            access.bulkImportBaseEvents( "cpu", cpuEvents, Double.class );
        }
        access.commit();
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
                        access.bulkImportBaseEvents( "cpu", cpuEvents, Double.class );
                        cpuEvents.clear();
                    }
                }
                else if (cName.startsWith( "mem" ))
                {
                    memEvents.add( createMemoryEvent( cName, ((Long)value).intValue(), globalEventId, timestamp ) );
                    if (memEvents.size() == BULK_IMPORT_THRESHOLD)
                    {
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

}
