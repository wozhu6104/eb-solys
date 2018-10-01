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
 * The RuntimeEventLifecycleListener is a interface which will be served by the RuntimeEventAcceptor whenever a new
 * LivecycleEventChannel will be created or destroyed.
 * 
 * @see #RuntimeEventAcceptor
 * @author pedu2501@elektrobit.com
 * @version 11.07
 */
public interface RuntimeEventChannelsChangedListener
{
    public void onRuntimeEventChannelsChanged();
}
