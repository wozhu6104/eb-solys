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

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventHandler;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventValue;
import com.google.gson.Gson;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.eventhooks.regextochannelhook.api.RegExToChannelEventHook;

@Component(service = EventHook.class)
public class RegExToChannelEventHookImpl implements RegExToChannelEventHook
{
    private static final Logger LOG = Logger.getLogger( RegExToChannelEventHookImpl.class );
    private JsonEventHandler jsonEventHandler = null;
    private String expression;
    private Pattern pattern;
    private Matcher matcher;

    public RegExToChannelEventHookImpl()
    {
        try
        {
            expression = FileHelper.readFileToString( "regExHook.txt" );
            pattern = Pattern.compile( expression );
            LOG.info( "initialized RegEx to Channel Event Hook with expression: " + expression );
        }
        catch (FileNotFoundException e)
        {
            LOG.error( e.getMessage() + "-> needs path/to/ebsolys/regExHook.txt" );
        }
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
    public void onEvent(String event)
    {
        LOG.debug( this.getClass().getName() + ".onEvent(" + event + ")" );
        JsonEvent oldEvent = new Gson().fromJson( event, JsonEvent.class );
        JsonEvent newEvent = mapEvent( oldEvent );
        if (newEvent != null)
        {
            jsonEventHandler.handle( newEvent );
        }
    }

    private JsonEvent mapEvent(JsonEvent oldEvent)
    {
        JsonEvent newEvent = null;
        String summaryString = oldEvent.getValue().getSummary().toString();
        matcher = pattern.matcher( summaryString );
        if (matcher.find())
        {
            newEvent = new JsonEvent( oldEvent.getUptime(),
                                      "cpu." + matcher.group( 2 ),
                                      new JsonEventValue( Double.parseDouble( matcher.group( 1 ) ), null ),
                                      null,
                                      null );
        }
        return newEvent;
    }
}
