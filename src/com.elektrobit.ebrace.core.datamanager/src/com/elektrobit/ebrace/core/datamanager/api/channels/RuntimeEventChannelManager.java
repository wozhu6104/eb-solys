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

import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public interface RuntimeEventChannelManager
{
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description);

    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription);

    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns);

    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description);

    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description, List<String> valueColumns);

    public void removeRuntimeEventChannel(RuntimeEventChannel<?> channel);

    public List<RuntimeEventChannel<?>> getRuntimeEventChannels();

    public RuntimeEventChannel<?> getRuntimeEventChannelWithName(String channelName);

    public void clear();

    public void renameRuntimeEventChannel(RuntimeEventChannel<?> channel, String newName);

    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForUnit(Unit<?> unit);

    public boolean checkIfChannelWithNameExists(String name);
}
