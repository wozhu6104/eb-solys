/*******************************************************************************
 * Copyright (C) 2018 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package de.systemticks.ebrace.core.eventhook.registry.api;

import java.util.Set;

public interface EventHookRegistry
{
    public void register(EventHook hook);

    public void unregister(EventHook hook);

    public Set<EventHook> getAll();

    public void callFor(String eventJson);
}
