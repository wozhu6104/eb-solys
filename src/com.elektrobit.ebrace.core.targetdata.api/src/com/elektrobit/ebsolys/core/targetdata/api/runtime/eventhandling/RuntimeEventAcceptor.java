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

import java.util.List;
import java.util.Map;

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;

/**
 * The Runtime Acceptor Interface extends the RuntimeEventProvider Interface in order to perform the following tasks:
 * <ul>
 * <li>It allows the creation of new Event channels.</li>
 * <li>It sends a new event to a certain RuntimeEventChannel.</li>
 * </ul>
 * 
 * @see RuntimeEventProvider
 * @see RuntimeEventChannel
 * 
 * @version 12.06
 */
public interface RuntimeEventAcceptor extends RuntimeEventProvider
{
    /**
     * Creates a new <i>typed</i> event channel with a certain <i>name</i>. If such an event channel already exists then
     * this method <b>does not</b> create a new channel but returns the already existing channel.
     * 
     * All RuntimeEventChannelLifecycleListener which are attached to the given RuntimeEventChannel will be notified
     * only <b>after</b> the Runtime Channel has been created. In this case its method
     * RuntimeEventChannel.runtimeChannelAdded(RuntimeEventChannel<T>) will be called().
     * 
     * @param context
     *            Data source context for data being added to this channel.
     * @param name
     *            The name of the new Event channel to create or to get.
     * @param unit
     *            Unit that specifies the data type of the channel.
     * @param description
     *            A Description of this event channel, in case a new channel is created.
     * @return The new RuntimeEventChannel Object for the given properties or the RuntimeEventChannel Object with the
     *         given name.
     */
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description);

    /**
     * Creates a new <i>typed</i> event channel with a certain <i>name</i>. If such an event channel already exists then
     * this method <b>does not</b> create a new channel but returns the already existing channel.
     * 
     * All RuntimeEventChannelLifecycleListener which are attached to the given RuntimeEventChannel will be notified
     * only <b>after</b> the Runtime Channel has been created. In this case its method
     * RuntimeEventChannel.runtimeChannelAdded(RuntimeEventChannel<T>) will be called().
     * 
     * @param context
     *            Data source context for data being added to this channel.
     * @param name
     *            The name of the new Event channel to create or to get.
     * @param unit
     *            Unit that specifies the data type of the channel.
     * @param description
     *            A Description of this event channel, in case a new channel is created.
     * @param valueColumns
     *            A value column describes which part of a json value shall be visualized in a table. E.g. if a event
     *            looks like this {"key1":"val1", "key2", "val2"} and the value column is {"key2"}, only key2 is visible
     *            as a value column in the UI table.
     * @return The new RuntimeEventChannel Object for the given properties or the RuntimeEventChannel Object with the
     *         given name.
     */
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description, List<String> valueColumns);

    /**
     * Creates a new <i>typed</i> event channel with a certain <i>name</i>. If such an event channel already exists then
     * this method <b>does not</b> create a new channel but returns the already existing channel.
     * 
     * All RuntimeEventChannelLifecycleListener which are attached to the given RuntimeEventChannel will be notified
     * only <b>after</b> the Runtime Channel has been created. In this case its method
     * RuntimeEventChannel.runtimeChannelAdded(RuntimeEventChannel<T>) will be called().
     * 
     * @param channelName
     *            The name of the new Event channel to create or to get.
     * @param unit
     *            Unit that specifies the data type of the channel.
     * @param description
     *            A Description of this event channel, in case a new channel is created.
     * @return The new RuntimeEventChannel Object for the given properties or the RuntimeEventChannel Object with the
     *         given name.
     */
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription);

    /**
     * Creates a new <i>typed</i> event channel with a certain <i>name</i>. If such an event channel already exists then
     * this method <b>does not</b> create a new channel but returns the already existing channel.
     * 
     * All RuntimeEventChannelLifecycleListener which are attached to the given RuntimeEventChannel will be notified
     * only <b>after</b> the Runtime Channel has been created. In this case its method
     * RuntimeEventChannel.runtimeChannelAdded(RuntimeEventChannel<T>) will be called().
     * 
     * @param channelName
     *            The name of the new Event channel to create or to get.
     * @param unit
     *            Unit that specifies the data type of the channel.
     * @param description
     *            A Description of this event channel, in case a new channel is created.
     * @param valueColumns
     *            A value column describes which part of a json value shall be visualized in a table. E.g. if a event
     *            looks like this {"key1":"val1", "key2", "val2"} and the value column is {"key2"}, only key2 is visible
     *            as a value column in the UI table.
     * @return The new RuntimeEventChannel Object for the given properties or the RuntimeEventChannel Object with the
     *         given name.
     */
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription, List<String> valueColumns);

    /**
     * Creates a new <i>typed</i> event channel with a certain <i>name</i>. If such an event channel already exists then
     * this method <b>does not</b> create a and returns null.
     * 
     * All RuntimeEventChannelLifecycleListener which are attached to the given RuntimeEventChannel will be notified
     * only <b>after</b> the Runtime Channel has been added. In this case its method
     * RuntimeEventChannel.runtimeChannelAdded(RuntimeEventChannel<T>) will be called().
     * 
     * @param name
     *            The name of the new Event channel to create to.
     * @param unit
     *            Unit that specifies the data type of the channel.
     * @param description
     *            A Description of this event channel.
     * @return The new RuntimeEventChannel Object for the given properties.
     */
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description);

    /**
     * Creates a new <i>typed</i> event channel with a certain <i>name</i>. If such an event channel already exists then
     * this method <b>does not</b> create a and returns null.
     * 
     * All RuntimeEventChannelLifecycleListener which are attached to the given RuntimeEventChannel will be notified
     * only <b>after</b> the Runtime Channel has been added. In this case its method
     * RuntimeEventChannel.runtimeChannelAdded(RuntimeEventChannel<T>) will be called().
     * 
     * @param name
     *            The name of the new Event channel to create to.
     * @param unit
     *            Unit that specifies the data type of the channel.
     * @param description
     *            A Description of this event channel.
     * @return The new RuntimeEventChannel Object for the given properties.
     */
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns);

    /**
     * Remove a runtime channel and all of its events.
     * 
     * @param channel
     *            The channel to be removed
     */
    public <T> void removeRuntimeEventChannel(RuntimeEventChannel<T> channel);

    /**
     * @deprecated Use acceptEventMicros() instead.
     * 
     *             Sends a new RuntimeEvent to a given RuntimeEventChannel object. The Event consists of a modelElement
     *             and a typed value. Note that an event channel is typed. This means that the events type and that of
     *             the event channel has to match!
     * 
     * @param timestamp
     *            The target-based {@link #Timestamp} of the correspondig event in absolute time in milliseconds.
     * @param channel
     *            The {@link #RuntimeEventChannel} to send the event to.
     * @param modelElement
     *            The model element for this event.
     * @param value
     *            The value for this event. The values type has to match the type of the event channel. *
     * @return The created event according to the given parameters
     * 
     * 
     */
    @Deprecated
    public <T> RuntimeEvent<T> acceptEvent(long timestampMS, RuntimeEventChannel<T> channel, ModelElement modelElement,
            T value);

    /**
     * Sends a new RuntimeEvent to a given RuntimeEventChannel object. The Event consists of a modelElement and a typed
     * value. Note that an event channel is typed. This means that the events type and that of the event channel has to
     * match!
     * 
     * @param timestamp
     *            The target-based {@link #Timestamp} of the correspondig event in absolute time in microseconds.
     * @param channel
     *            The {@link #RuntimeEventChannel} to send the event to.
     * @param modelElement
     *            The model element for this event.
     * @param value
     *            The value for this event. The values type has to match the type of the event channel.
     * 
     * @return The created event according to the given parameters
     * 
     */
    public <T> RuntimeEvent<T> acceptEventMicros(long timestampUS, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value);

    /**
     * Sends a new RuntimeEvent to a given RuntimeEventChannel object. The Event consists of a summary, a modelElement
     * and a typed value. Note that an event channel is typed. This means that the events type and that of the event
     * channel has to match!
     * 
     * @param timestamp
     *            The target-based {@link #Timestamp} of the correspondig event in absolute time in microseconds.
     * @param channel
     *            The {@link #RuntimeEventChannel} to send the event to.
     * @param modelElement
     *            The model element for this event.
     * @param value
     *            The value for this event. The values type has to match the type of the event channel.
     * @param summary
     *            The summary will be shown in the value cell of a RuntimeEventTable if present. Otherwise a string
     *            representation of the value is shown.
     * 
     * @return The created event according to the given parameters
     * 
     */
    public <T> RuntimeEvent<T> acceptEventMicros(long timestampUS, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value, String summary);

    public void dispose();

    @Override
    List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannels(List<RuntimeEventChannel<?>> runtimeEventChannel);

    public void renameRuntimeEventChannel(RuntimeEventChannel<?> channel, String newName);

    /**
     * Tag a RuntimeEvent with the given RuntimeEventTag and a meaningful description.
     * 
     * @return Returns the modified RuntimeEvent.
     */
    public RuntimeEvent<?> setTag(RuntimeEvent<?> event, RuntimeEventTag tag, String tagDescription);

    /**
     * Remove RuntimeEventTag from RuntimeEvent.
     * 
     * @return Returns the modified RuntimeEvent.
     */
    public RuntimeEvent<?> clearTag(RuntimeEvent<?> event);

    public void setParameter(RuntimeEventChannel<?> channel, String key, Object value);

    public void setParameters(RuntimeEventChannel<?> channel, Map<String, Object> parameters);

    public void addChannelRemovedListener(ChannelRemovedListener listener);

    public void removeChannelRemovedListener(ChannelRemovedListener listener);
}
