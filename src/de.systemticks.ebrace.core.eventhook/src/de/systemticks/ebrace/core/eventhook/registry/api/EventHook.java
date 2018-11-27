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

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public interface EventHook
{
    void onEvent(RuntimeEvent<?> event);
}
