/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.api.channels;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface ChannelListenerNotifier
{
    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channels);

    public void unregisterListener(ChannelsContentChangedListener listener);

    public void notifyChannelChanged(RuntimeEventChannel<?> channel);

    public void notifyAllChannelsChanged();

    public void notifyChannelRemoved(RuntimeEventChannel<?> channel);
}
