/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElementPool;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelRemovedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

@Component(immediate = true)
public class RuntimeEventServiceProxy implements RuntimeEventAcceptor, RuntimeEventProvider, ClearChunkDataListener
{
    private RuntimeEventAcceptor runtimeEventAcceptorImpl;
    private ModelElementPool modelElementPool;
    private RuntimeEventChannelManager runtimeEventChannelManager;
    private ChannelListenerNotifier channelListenerNotifier;

    public RuntimeEventServiceProxy()
    {
    }

    @Override
    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channel)
    {
        runtimeEventAcceptorImpl.registerListener( listener, channel );
    }

    @Override
    public void unregisterListener(ChannelsContentChangedListener listener)
    {
        runtimeEventAcceptorImpl.unregisterListener( listener );
    }

    @Reference
    public void setModelElementPool(ModelElementPool modelElementPool)
    {
        this.modelElementPool = modelElementPool;
    }

    public void unsetModelElementPool(ModelElementPool modelElementPool)
    {
        this.modelElementPool = null;
    }

    @Reference
    public void setRuntimeEventChannelManager(RuntimeEventChannelManager runtimeEventChannelManager)
    {
        this.runtimeEventChannelManager = runtimeEventChannelManager;
    }

    public void unsetRuntimeEventChannelManager(RuntimeEventChannelManager runtimeEventChannelManager)
    {
        this.runtimeEventChannelManager = null;
    }

    @Reference
    public void bind(ChannelListenerNotifier channelListenerNotifier)
    {
        this.channelListenerNotifier = channelListenerNotifier;
    }

    public void unbind(ChannelListenerNotifier channelListenerNotifier)
    {
        this.channelListenerNotifier = null;
    }

    protected void activate(ComponentContext componentContext)
    {
        runtimeEventAcceptorImpl = new RuntimeEventAcceptorImpl( runtimeEventChannelManager,
                                                                 modelElementPool,
                                                                 new RuntimeEventNotifierImpl(),
                                                                 channelListenerNotifier );
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannels()
    {
        return runtimeEventAcceptorImpl.getRuntimeEventChannels();
    }

    @Override
    public <T> RuntimeEventChannel<T> getRuntimeEventChannel(String name, Class<T> type)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventChannel( name, type );
    }

    @Override
    public List<RuntimeEvent<?>> getAllRuntimeEvents()
    {
        return runtimeEventAcceptorImpl.getAllRuntimeEvents();
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfTimespan(long startTimestampInMillis, long stopTimestampInMillis)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventsOfTimespan( startTimestampInMillis, stopTimestampInMillis );
    }

    @Override
    public RuntimeEvent<?> getLatestRuntimeEvent()
    {
        return runtimeEventAcceptorImpl.getLatestRuntimeEvent();
    }

    @Override
    public RuntimeEvent<?> getFirstRuntimeEvent()
    {
        return runtimeEventAcceptorImpl.getFirstRuntimeEvent();
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfModelElement(ModelElement modelElement)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventsOfModelElement( modelElement );
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfModelElements(List<ModelElement> modelElements)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventsOfModelElements( modelElements );
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannel(RuntimeEventChannel<?> runtimeEventChannel)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventsOfRuntimeEventChannel( runtimeEventChannel );
    }

    @Override
    public RuntimeEvent<?> getLastRuntimeEventForTimeStampInterval(long start, long end, RuntimeEventChannel<?> channel)
    {
        return runtimeEventAcceptorImpl.getLastRuntimeEventForTimeStampInterval( start, end, channel );
    }

    @Override
    public RuntimeEvent<?> getFirstRuntimeEventForTimeStampInterval(long start, long end,
            RuntimeEventChannel<?> channel)
    {
        return runtimeEventAcceptorImpl.getFirstRuntimeEventForTimeStampInterval( start, end, channel );
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForModelElement(ModelElement element)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventChannelsForModelElement( element );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description)
    {
        return runtimeEventAcceptorImpl.createOrGetRuntimeEventChannel( context, name, unit, description );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription)
    {
        return runtimeEventAcceptorImpl.createOrGetRuntimeEventChannel( channelName, unit, channelDescription );
    }

    @Override
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description)
    {
        return runtimeEventAcceptorImpl.createRuntimeEventChannel( name, unit, description );
    }

    @Override
    public <T> RuntimeEvent<T> acceptEvent(long timestamp, RuntimeEventChannel<T> channel, ModelElement modelElement,
            T value)
    {
        return runtimeEventAcceptorImpl.acceptEvent( timestamp, channel, modelElement, value );
    }

    @Override
    public void dispose()
    {
        runtimeEventAcceptorImpl.dispose();
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannels(
            List<RuntimeEventChannel<?>> runtimeEventChannel)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventsOfRuntimeEventChannels( runtimeEventChannel );
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForType(Class<?> type)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventChannelsForType( type );
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventForTimeStampIntervalForChannels(long start, long end,
            List<RuntimeEventChannel<?>> channels)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventForTimeStampIntervalForChannels( start, end, channels );
    }

    @Override
    public <T> RuntimeEvent<T> getLatestRuntimeEventOfChannel(RuntimeEventChannel<T> runtimeEventChannel)
    {
        return runtimeEventAcceptorImpl.getLatestRuntimeEventOfChannel( runtimeEventChannel );
    }

    @Override
    public LineChartData getLineChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp, long endTimestamp,
            boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {
        return runtimeEventAcceptorImpl.getLineChartData( channels,
                                                          startTimestamp,
                                                          endTimestamp,
                                                          dataAsBars,
                                                          aggregationTime,
                                                          aggregateForStackedMode );
    }

    @Override
    public GanttChartData getGanttChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, Long aggregationTime)
    {
        return runtimeEventAcceptorImpl.getGanttChartData( channels, startTimestamp, endTimestamp, aggregationTime );
    }

    @Override
    public void onClearChunkData()
    {
        runtimeEventAcceptorImpl.dispose();
    }

    @Override
    public int getStateId()
    {
        return runtimeEventAcceptorImpl.getStateId();
    }

    @Override
    public boolean hasStateIdChanged(Integer stateIdBefore)
    {
        return runtimeEventAcceptorImpl.hasStateIdChanged( stateIdBefore );
    }

    protected void deactivate()
    {
        runtimeEventAcceptorImpl.dispose();
    }

    @Override
    public void renameRuntimeEventChannel(RuntimeEventChannel<?> channel, String newName)
    {
        runtimeEventAcceptorImpl.renameRuntimeEventChannel( channel, newName );
    }

    @Override
    public Map<RuntimeEventChannel<?>, RuntimeEvent<?>> getRuntimeEventsOfChannelsForTimestamp(
            List<RuntimeEventChannel<?>> runtimeEventChannels, long timestamp)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventsOfChannelsForTimestamp( runtimeEventChannels, timestamp );
    }

    @Override
    public List<RuntimeEvent<?>> getLatestRuntimeEventsOfChannels(List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        return runtimeEventAcceptorImpl.getLatestRuntimeEventsOfChannels( runtimeEventChannels );
    }

    @Override
    public <T> RuntimeEvent<T> acceptEventMicros(long timestampUS, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value, String summary)
    {
        return runtimeEventAcceptorImpl.acceptEventMicros( timestampUS, channel, modelElement, value, summary );
    }

    @Override
    public <T> RuntimeEvent<T> acceptEventMicros(long timestampUS, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value)
    {
        return runtimeEventAcceptorImpl.acceptEventMicros( timestampUS, channel, modelElement, value );
    }

    @Override
    public RuntimeEvent<?> setTag(RuntimeEvent<?> event, RuntimeEventTag tag, String tagDescription)
    {
        return runtimeEventAcceptorImpl.setTag( event, tag, tagDescription );
    }

    @Override
    public RuntimeEvent<?> clearTag(RuntimeEvent<?> event)
    {
        return runtimeEventAcceptorImpl.clearTag( event );
    }

    @Override
    public <T> RuntimeEventChannel<T> getRuntimeEventChannel(String name)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventChannel( name );
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForUnit(Unit<?> unit)
    {
        return runtimeEventAcceptorImpl.getRuntimeEventChannelsForUnit( unit );
    }

    @Override
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns)
    {
        return runtimeEventAcceptorImpl.createRuntimeEventChannel( name, unit, description, valueColumns );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description, List<String> valueColumns)
    {
        return runtimeEventAcceptorImpl
                .createOrGetRuntimeEventChannel( context, name, unit, description, valueColumns );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription, List<String> valueColumns)
    {
        return runtimeEventAcceptorImpl
                .createOrGetRuntimeEventChannel( channelName, unit, channelDescription, valueColumns );
    }

    @Override
    public <T> void removeRuntimeEventChannel(RuntimeEventChannel<T> channel)
    {
        runtimeEventAcceptorImpl.removeRuntimeEventChannel( channel );
    }

    @Override
    public void setParameter(RuntimeEventChannel<?> channel, String key, Object value)
    {
        runtimeEventAcceptorImpl.setParameter( channel, key, value );
    }

    @Override
    public void setParameters(RuntimeEventChannel<?> channel, Map<String, Object> parameters)
    {
        runtimeEventAcceptorImpl.setParameters( channel, parameters );
    }

    @Override
    public void addChannelRemovedListener(ChannelRemovedListener listener)
    {
        runtimeEventAcceptorImpl.addChannelRemovedListener( listener );
    }

    @Override
    public void removeChannelRemovedListener(ChannelRemovedListener listener)
    {
        runtimeEventAcceptorImpl.removeChannelRemovedListener( listener );
    }

}
