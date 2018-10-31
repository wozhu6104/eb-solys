/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package de.systemticks.ebrace.core.eventhook.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.core.eventhook.registry.api.EventHookCollector;
import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

@Component(immediate = true, enabled = true)
public class EventHookRegistryServiceImpl implements EventHookRegistry
{
    private final EventHookCollector collector;

    public EventHookRegistryServiceImpl()
    {
        collector = new EventHookCollector()
        {
            @Override
            public List<EventHook> getEventHooks()
            {
                GenericOSGIServiceTracker<EventHook> serviceTracker = new GenericOSGIServiceTracker<EventHook>( EventHook.class );
                Map<Object, Properties> servicesMap = serviceTracker.getServices( EventHook.class.getName() );
                Set<Object> eventHookObjects = servicesMap.keySet();
                List<EventHook> eventHooks = new ArrayList<EventHook>();
                for (Object eventHookObject : eventHookObjects)
                {
                    eventHooks.add( (EventHook)eventHookObject );
                }
                return eventHooks;
            }
        };
    }

    @Override
    public void callFor(String event)
    {
        collector.getEventHooks().forEach( hook -> hook.onEvent( event ) );
    }
}
