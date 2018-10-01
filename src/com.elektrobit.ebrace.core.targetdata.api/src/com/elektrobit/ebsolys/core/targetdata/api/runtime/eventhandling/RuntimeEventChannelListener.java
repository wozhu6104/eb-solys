/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

/**
 * This interface describes a RuntimeEvent Listener. A Runtime Event Listener will be notified by the
 * RuntimeEventAcceptor interface on a new runtime accept call.
 */
public interface RuntimeEventChannelListener
{
    /**
     * This method will be called when a new RuntimeEvent occurred.
     * 
     * @param e
     *            The runtime event which occurred.
     */
    public void occured(RuntimeEvent<?> e);
}
