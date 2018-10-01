/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.decoder;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

/**
 * A decoded runtime event is an event which shows the value of a runtime event in as a tree structure. Classes which
 * have to represent a decoded runtime event have to implement this interface.
 * 
 */
public interface DecodedRuntimeEvent
{
    /** Returns the tree which represents the decoded value of the event. */
    DecodedTree getDecodedTree();

    /** Returns the string representation (signature) of the value of the runtime event. */
    String getSummary();

    /** Returns the runtime event type of the decoded runtime event (e.g. request,response, broadcast, etc). */
    @Deprecated
    RuntimeEventType getRuntimeEventType();

    /** Returns the runtime event channel */
    @Deprecated
    RuntimeEventChannel<?> getRuntimeEventChannel();

    /** Returns the value of the runtimeevent */
    Object getRuntimeEventValue();

    /** return runtime event **/
    RuntimeEvent<?> getRuntimeEvent();
}
