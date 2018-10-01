/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.channels.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.ListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.RuntimeEventChannelObjectImpl;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

@Component
public class RuntimeEventChannelManagerImpl implements RuntimeEventChannelManager
{
    public static final String SOURCE_PREFIX_ACTIVE_FLAG = "--channelSourcePrefixOn";
    private final Map<String, RuntimeEventChannel<?>> channelCache = new ConcurrentHashMap<String, RuntimeEventChannel<?>>();
    private boolean sourcePrefixActive;
    private final ListenerNotifier listenerNotifier;

    public RuntimeEventChannelManagerImpl()
    {
        this( new RuntimeEventChannelLifecycleChangedNotifierImpl() );
    }

    public RuntimeEventChannelManagerImpl(ListenerNotifier listenerNotifier)
    {
        this.listenerNotifier = listenerNotifier;
    }

    @Reference
    public void bindCommandLineParser(CommandLineParser commandLineParser)
    {
        setSourcePrefixActiveFlag( commandLineParser );
    }

    public void unbindCommandLineParser(CommandLineParser commandLineParser)
    {

    }

    private void setSourcePrefixActiveFlag(CommandLineParser commandLineParser)
    {
        sourcePrefixActive = true;// commandLineParser.hasArg( SOURCE_PREFIX_ACTIVE_FLAG );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String description)
    {
        return createOrGetRuntimeEventChannel( channelName, unit, description, Collections.emptyList() );
    }

    @Override
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description)
    {
        return createRuntimeEventChannel( name, unit, description, Collections.emptyList() );
    }

    private <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns)
    {
        if (!checkIfChannelWithNameExists( name ))
        {
            RuntimeEventChannel<T> newRuntimeEventChannel = createAndAddNewChannel( name,
                                                                                    unit,
                                                                                    description,
                                                                                    valueColumns );
            listenerNotifier.notifyListeners();
            return newRuntimeEventChannel;
        }
        else
        {
            return null;
        }
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String channelName,
            Unit<T> unit, String description)
    {
        return createOrGetRuntimeEventChannel( context, channelName, unit, description, Collections.emptyList() );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description, List<String> valueColumns)
    {
        String sourcePrefix = sourcePrefixActive ? context.getSourceName() : "";
        return createOrGetRuntimeEventChannel( sourcePrefix + name, unit, description, valueColumns );
    }

    @Override
    public boolean checkIfChannelWithNameExists(final String name)
    {
        return channelCache.containsKey( name );
    }

    private <T> RuntimeEventChannel<T> createAndAddNewChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns)
    {
        RuntimeEventChannel<T> newRuntimeEventChannel = new RuntimeEventChannelObjectImpl<T>( name,
                                                                                              description,
                                                                                              unit,
                                                                                              valueColumns );
        channelCache.put( newRuntimeEventChannel.getName(), newRuntimeEventChannel );
        return newRuntimeEventChannel;
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannelWithName(String channelName)
    {
        return channelCache.get( channelName );
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForUnit(Unit<?> unit)
    {
        List<RuntimeEventChannel<?>> result = new ArrayList<RuntimeEventChannel<?>>();
        for (RuntimeEventChannel<?> channel : getRuntimeEventChannels())
        {
            if (unit.equals( channel.getUnit() ))
            {
                result.add( channel );
            }
        }
        return result;
    }

    @Override
    public void removeRuntimeEventChannel(RuntimeEventChannel<?> channel)
    {
        channelCache.remove( channel.getName() );

        listenerNotifier.notifyListeners();
    }

    @Override
    public void renameRuntimeEventChannel(RuntimeEventChannel<?> channel, String newName)
    {
        channelCache.remove( channel.getName() );

        RuntimeEventChannelObjectImpl<?> channelImpl = (RuntimeEventChannelObjectImpl<?>)channel;
        channelImpl.setName( newName );

        channelCache.put( channelImpl.getName(), channelImpl );
        listenerNotifier.notifyListeners();
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannels()
    {
        Collection<RuntimeEventChannel<?>> channels = channelCache.values();
        List<RuntimeEventChannel<?>> channelsList = new ArrayList<RuntimeEventChannel<?>>( channels );

        Collections.sort( channelsList, new Comparator<RuntimeEventChannel<?>>()
        {
            @Override
            public int compare(RuntimeEventChannel<?> o1, RuntimeEventChannel<?> o2)
            {
                return o1.getName().compareTo( o2.getName() );
            }
        } );

        return Collections.unmodifiableList( channelsList );
    }

    @Override
    public void clear()
    {
        channelCache.clear();
        listenerNotifier.notifyListeners();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns)
    {
        if (!checkIfChannelWithNameExists( name ))
        {
            return createRuntimeEventChannel( name, unit, description, valueColumns );
        }
        else
        {
            RuntimeEventChannel<?> runtimeEventChannel = getRuntimeEventChannelWithName( name );
            if (runtimeEventChannel.getUnit().equals( unit ))
            {
                return (RuntimeEventChannel<T>)runtimeEventChannel;
            }
            else
            {
                throw new IllegalArgumentException( "A runtime event channel (" + name
                        + ") with same name exists, but with another Unit. Existing channel Unit: "
                        + runtimeEventChannel.getUnit().getName() + ". New Unit: " + unit.getName() + "." );
            }
        }
    }

}
