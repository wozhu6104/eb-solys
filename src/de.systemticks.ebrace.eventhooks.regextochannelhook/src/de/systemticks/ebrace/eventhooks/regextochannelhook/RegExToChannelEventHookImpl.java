/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package de.systemticks.ebrace.eventhooks.regextochannelhook;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.UnitConverter;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonChannel;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebrace.targetdata.dlt.api.DltProcStatStatmEventConverter;
import com.elektrobit.ebrace.targetdata.dlt.api.Measurement;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcCpuEntry;
import com.elektrobit.ebrace.targetdata.dlt.api.ProcMemEntry;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.google.gson.Gson;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.eventhooks.regextochannelhook.api.RegExToChannelEventHook;

@Component(service = EventHook.class)
public class RegExToChannelEventHookImpl implements RegExToChannelEventHook
{
    private JsonEventHandler jsonEventHandler = null;
    private final DltProcStatStatmEventConverter parser = new DltProcStatStatmEventConverter();

    public RegExToChannelEventHookImpl()
    {
    }

    @Reference
    public void bindJsonService(JsonEventHandler runtimeEventAcceptor)
    {
        this.jsonEventHandler = runtimeEventAcceptor;
    }

    public void unbindJsonService(JsonEventHandler runtimeEventAcceptor)
    {
        this.jsonEventHandler = null;
    }

    @Override
    public void onEvent(RuntimeEvent<?> event)
    {
        if (event.getRuntimeEventChannel().getName().toLowerCase().contains( "trace.dlt.log.mon.stat" ))
        {
            JsonEvent oldEvent = new Gson().fromJson( event.getValue().toString(), JsonEvent.class );
            String summaryString = oldEvent.getValue().getDetails().getAsJsonObject().get( "payload" ).getAsJsonObject()
                    .get( "0" ).toString();
            summaryString = summaryString.substring( 1, summaryString.length() - 7 );

            Measurement<ProcCpuEntry> parseCpuData = null;
            Measurement<ProcMemEntry> parseMemData = null;

            if (summaryString.split( " " )[1].toLowerCase().trim().equals( "stat" ))
            {
                parseCpuData = parser.parseCpuData( event.getTimestamp(), summaryString );
            }
            else if (summaryString.split( " " )[1].toLowerCase().trim().equals( "statm" ))
            {
                parseMemData = parser.parseMemData( event.getTimestamp(), summaryString );
            }

            if (parseCpuData != null)
            {
                Map<Integer, ProcCpuEntry> pidToMeasurement = parseCpuData.getPidToMeasurement();
                for (ProcCpuEntry entry : pidToMeasurement.values())
                {
                    double perProcessCpuUsage = (100000 * entry.getCpuUsage() / entry.getTimestamp());
                    JsonEvent newEvent = new JsonEvent( event.getTimestamp(),
                                                        new JsonChannel( "cpu.proc." + entry.getProcName(),
                                                                         "",
                                                                         "PERCENT" ),
                                                        new JsonEventValue( perProcessCpuUsage, null ),
                                                        null,
                                                        null );
                    jsonEventHandler.handle( newEvent );

                }
            }
            else if (parseMemData != null)
            {
                Map<Integer, ProcMemEntry> pidToMeasurement = parseMemData.getPidToMeasurement();
                for (ProcMemEntry entry : pidToMeasurement.values())
                {
                    JsonEvent newEvent = new JsonEvent( event.getTimestamp(),
                                                        new JsonChannel( "mem.proc." + entry.getProcName(),
                                                                         "",
                                                                         "KILOBYTE" ),
                                                        new JsonEventValue( UnitConverter
                                                                .convertBytesToKB( entry.getMemoryUsage() ), null ),
                                                        null,
                                                        null );
                    jsonEventHandler.handle( newEvent );
                }
            }
        }
    }

}
