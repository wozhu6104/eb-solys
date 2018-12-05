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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.google.gson.Gson;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.eventhooks.regextochannelhook.api.RegExToChannelEventHook;

@Component(service = EventHook.class)
public class SysMonCpupHook implements RegExToChannelEventHook
{
    private static final Logger LOG = Logger.getLogger( SysMonCpupHook.class );
    private JsonEventHandler jsonEventHandler = null;
    private final String expression = "(?<cpu>\\d+.\\d+) % cpu consumed by process (?<processname>[^\\s]+) .*pid : (?<pid>[0-9]+) .*";
    private final Pattern pattern;
    private Matcher matcher;

    public SysMonCpupHook()
    {
        pattern = Pattern.compile( expression );
        LOG.info( "initialized RegEx to Channel Event Hook with expression: " + expression );
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
        if (event.getRuntimeEventChannel().getName().toLowerCase().contains( "trace.dlt.log.mon.cpup" ))
        {
            JsonEvent oldEvent = new Gson().fromJson( event.getValue().toString(), JsonEvent.class );
            String summaryString = oldEvent.getValue().getDetails().getAsJsonObject().get( "payload" ).getAsJsonObject()
                    .get( "0" ).toString();
            summaryString = summaryString.substring( 1, summaryString.length() - 7 );
            matcher = pattern.matcher( summaryString );
            if (matcher.find())
            {
                JsonEvent newEvent = new JsonEvent( event.getTimestamp(),
                                                    "process.cpu." + matcher.group( "processname" ) + ":"
                                                            + matcher.group( "pid" ),
                                                    new JsonEventValue( Double.parseDouble( matcher.group( "cpu" ) ),
                                                                        null ),
                                                    null,
                                                    null );
                jsonEventHandler.handle( newEvent );
            }

        }

    }

}
