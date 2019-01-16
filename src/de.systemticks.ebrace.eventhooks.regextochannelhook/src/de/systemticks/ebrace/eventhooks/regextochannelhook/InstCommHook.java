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

import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventEdge;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.eventhooks.regextochannelhook.api.RegExToChannelEventHook;

@Component(service = EventHook.class)
public class InstCommHook implements RegExToChannelEventHook
{
    private JsonEventHandler jsonEventHandler = null;
    // private final String expression = "CPU usage in interval : (?<cpu>\\d+.\\d+)% iowait: (?<iowait>\\d+)% cpu since
    // boot : (?<cpuboot>\\d+.\\d+)% Total thread cpu load : (?<cputhread>\\d+.\\d+)%";
    // private final String expression = "(?<source>.*)->(?<dest>.*)\\|(?<type>[REQ|RES])\\|(?<payload>.*)";
    private final String expression = "(?<source>([A-Za-z]|[0-9]|[\\.])+)->(?<dest>([A-Za-z]|[0-9]|[\\.])+)\\|(?<type>(REQ|RES)+)\\|(?<payload>.*)";
    private final Pattern pattern;
    private Matcher matcher;
    private final JsonParser parser = new JsonParser();

    public InstCommHook()
    {
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
        if (event.getRuntimeEventChannel().getName().toLowerCase().contains( "trace.dlt.log.sina.sinc" ))
        {

            JsonEvent oldEvent = new Gson().fromJson( event.getValue().toString(), JsonEvent.class );
            String summaryString = oldEvent.getValue().getDetails().getAsJsonObject().get( "payload" ).getAsJsonObject()
                    .get( "0" ).toString();
            summaryString = summaryString.substring( 1, summaryString.length() - 7 );
            if (summaryString.endsWith( "\\n" ))
            {
                summaryString = summaryString.substring( 0, summaryString.length() - 2 );
            }

            matcher = pattern.matcher( summaryString );
            if (matcher.find())
            {
                System.out.println( matcher.group( "source" ) );
                System.out.println( matcher.group( "dest" ) );
                System.out.println( matcher.group( "type" ) );
                String payload = matcher.group( "payload" );
                System.out.println( payload.trim() );

                String rawType = matcher.group( "type" );
                String type = "request";
                String summary = "->";
                if (rawType.equals( "RES" ))
                {
                    type = "response";
                    summary = "<-";
                }

                summary += " " + payload.replaceAll( "\"", "" );

                JsonEvent newEvent = new JsonEvent( event.getTimestamp(),
                                                    "com.someip",
                                                    new JsonEventValue( summary, null ),
                                                    null,
                                                    new JsonEventEdge( matcher.group( "source" ),
                                                                       matcher.group( "dest" ),
                                                                       type ) );

                jsonEventHandler.handle( newEvent );

            }

        }

    }

}
