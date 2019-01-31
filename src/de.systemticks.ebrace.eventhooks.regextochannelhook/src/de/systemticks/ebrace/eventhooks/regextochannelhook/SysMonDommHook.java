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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonChannel;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.google.gson.Gson;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.eventhooks.regextochannelhook.api.RegExToChannelEventHook;
import lombok.extern.log4j.Log4j;

@Log4j
@Component(service = EventHook.class)
public class SysMonDommHook implements RegExToChannelEventHook
{
    private JsonEventHandler jsonEventHandler = null;
    private final String expression = "RSS memory used by domain \\\\\"(?<domainname>[^\\s]+)\\\\\" : (?<rss>[0-9]+) KB.*";

    private final Pattern pattern;
    private Matcher matcher;

    public SysMonDommHook()
    {
        log.debug( "initialized RegEx to Channel Event Hook with expression: " + expression );
        pattern = Pattern.compile( expression );
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
        if (event.getRuntimeEventChannel().getName().toLowerCase().contains( "trace.dlt.log.mon.domm" ))
        {
            JsonEvent oldEvent = new Gson().fromJson( event.getValue().toString(), JsonEvent.class );
            String summaryString = oldEvent.getValue().getDetails().getAsJsonObject().get( "payload" ).getAsJsonObject()
                    .get( "0" ).toString();
            summaryString = summaryString.substring( 1, summaryString.length() - 7 );
            matcher = pattern.matcher( summaryString );
            if (matcher.find())
            {
                long valueAsMB = Long.parseLong( matcher.group( "rss" ) ) / 1000;
                JsonEvent newEvent = new JsonEvent( event.getTimestamp(),
                                                    new JsonChannel( "domain." + "mem." + matcher.group( "domainname" ),
                                                                     "",
                                                                     "MB" ),
                                                    new JsonEventValue( valueAsMB, null ),
                                                    null,
                                                    null );
                jsonEventHandler.handle( newEvent );
            }

        }

    }

}
