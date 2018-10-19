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

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.google.gson.JsonObject;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

@Component(immediate = true, enabled = true)
public class EventHookRegistryServiceImpl implements EventHookRegistry
{

    private final Set<EventHook> hooks = new HashSet<EventHook>();

    @Override
    public void register(EventHook hook)
    {
        hooks.add( hook );
    }

    @Override
    public void unregister(EventHook hook)
    {
        hooks.remove( hook );
    }

    @Override
    public void callFor(JsonObject event)
    {
        hooks.forEach( hook -> hook.onEvent( event ) );
    }

    @Override
    public Set<EventHook> getAll()
    {
        return hooks;
    }

}
