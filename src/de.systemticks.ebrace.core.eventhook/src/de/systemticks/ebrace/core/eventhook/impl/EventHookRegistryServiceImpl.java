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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHook;
import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

@Component(immediate = true, enabled = true)
public class EventHookRegistryServiceImpl implements EventHookRegistry
{
    private final Set<EventHook> hooks = new CopyOnWriteArraySet<>();

    public EventHookRegistryServiceImpl()
    {
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void bindEventHook(EventHook hook)
    {
        hooks.add( hook );
    }

    public void unbindEventHook(EventHook hook)
    {
        hooks.remove( hook );
    }

    @Override
    public void callFor(RuntimeEvent<?> event)
    {
        hooks.forEach( hook -> hook.onEvent( event ) );
    }
}
