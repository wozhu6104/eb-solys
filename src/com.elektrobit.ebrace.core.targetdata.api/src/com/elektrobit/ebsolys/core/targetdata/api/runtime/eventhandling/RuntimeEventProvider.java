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
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;

/**
 * A RuntimeEventProvider is a interface supposed to manage all Runtime events. In particular this class manages all
 * Event Channels and all Listeners which belong to this Channels.
 * 
 * In particular this is:
 * <ul>
 * <li>dynamically adding of RuntimeEventChannels</li>
 * <li>dynamically removing of RuntimeEventChannels</li>
 * <li>dynamically adding of of RuntimeChannelListener instances</li>
 * <li>dynamically removing of of RuntimeChannelListener instances</li>
 * </ul>
 * Because of it central management semantics there is just one sole RuntimeEventProvider in the system. Hence this
 * Object is implemented by using the singleton pattern.
 * 
 * @author pedu2501@elektrobit.com
 */
public interface RuntimeEventProvider
{
    /**
     * Get all Runtime event channels from this provider instance. The RuntimeEventChannels can be accessed <i>read
     * only</i> by this interface.
     * 
     * @return A set of all RuntimeEventChannels.
     */
    public List<RuntimeEventChannel<?>> getRuntimeEventChannels();

    /**
     * @deprecated Use <b>getRuntimeEventChannel(String name)</b> instead.
     * 
     *             Query the RuntimeEventChannel by name <b>and</b> which belongs to a particular class.
     * 
     * @param name
     *            The name of the Runtime Event Channel to query.
     * @param type
     *            The class type of the Runtime Event channel.
     * @return The RuntimeEvent Channel which belongs to name <b>and</b> type. If there is no match then <tt>null</tt>
     *         will be returned.
     */
    @Deprecated
    public <T> RuntimeEventChannel<T> getRuntimeEventChannel(String name, Class<T> type);

    /**
     * Query the RuntimeEventChannel by name.
     * 
     * @param name
     *            The name of the Runtime Event Channel to query.
     * @return The RuntimeEvent Channel given <b>name</b>. If there is no match then <tt>null</tt> will be returned.
     */
    public <T> RuntimeEventChannel<T> getRuntimeEventChannel(String name);

    /**
     * @deprecated Use getRuntimeEventChannelsForUnit() instead
     * 
     * @param type
     * @return
     */
    @Deprecated
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForType(Class<?> type);

    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForUnit(Unit<?> unit);

    /**
     * Returns all RuntimeEvents.
     * 
     * @return Returns a list of all RuntimeEvents.
     */
    public List<RuntimeEvent<?>> getAllRuntimeEvents();

    /**
     * Returns all RuntimeEvents in the given time span. Start and end time are included in the time span.
     * 
     * @param startTimestampInMillis
     *            The start time of the time span in milliseconds.
     * @param startTimestampInMillis
     *            The end time of the time span in milliseconds.
     * @return Returns a list of the found RuntimeEvents in the given time span.
     */
    public List<RuntimeEvent<?>> getRuntimeEventsOfTimespan(long startTimestampInMillis, long stopTimestampInMillis);

    /**
     * Returns the RuntimeEvent with the highest timestamp. If there isn't any RuntimeEvent stored, the return value is
     * null.
     * 
     * @return Returns the RuntimeEvent with the highest timestamp.
     */
    public RuntimeEvent<?> getLatestRuntimeEvent();

    /**
     * Returns the RuntimeEvent with the lowest timestamp. If there isn't any RuntimeEvent stored, the return value is
     * null.
     * 
     * @return Returns the RuntimeEvent with the lowest timestamp.
     */
    public RuntimeEvent<?> getFirstRuntimeEvent();

    /**
     * Returns all RuntimeEvents which are tagged with the given ModelElement.
     */
    public List<RuntimeEvent<?>> getRuntimeEventsOfModelElement(ModelElement modelElement);

    /**
     * Returns all RuntimeEvents which are tagged with ont of the given ModelElements.
     */
    public List<RuntimeEvent<?>> getRuntimeEventsOfModelElements(List<ModelElement> modelElements);

    /**
     * Returns all RuntimeEvents which are belongs to the given RuntimeEventChannel.
     */
    public List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannel(RuntimeEventChannel<?> runtimeEventChannel);

    public RuntimeEvent<?> getLastRuntimeEventForTimeStampInterval(long start, long end,
            RuntimeEventChannel<?> channel);

    public RuntimeEvent<?> getFirstRuntimeEventForTimeStampInterval(long start, long end,
            RuntimeEventChannel<?> channel);

    public List<RuntimeEvent<?>> getRuntimeEventForTimeStampIntervalForChannels(long start, long end,
            List<RuntimeEventChannel<?>> channels);

    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForModelElement(ModelElement element);

    public List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannels(
            List<RuntimeEventChannel<?>> runtimeEventChannel);

    /**
     * 
     * @param runtimeEventChannels
     *            the list of RuntimeEventChannels
     * @param timestamp
     *            the timestamp after which the first events are needed
     * @return Returns a map of RuntimeEvents for each channel. For numeric channels the value is the first RuntimeEvent
     *         after the given timestamp (timestamp has to be after the first and before the last event of each channel,
     *         otherwise value this is NULL). For other types of channels the value is the RuntimeEvent before the given
     *         timestamp. When no corresponding value is found, value of the corresponding channel key is null.
     */
    public Map<RuntimeEventChannel<?>, RuntimeEvent<?>> getRuntimeEventsOfChannelsForTimestamp(
            List<RuntimeEventChannel<?>> runtimeEventChannels, long timestamp);

    /**
     * 
     * @param runtimeEventChannels
     *            the list of RuntimeEventChannels
     * 
     * @return Returns a list of last RuntimeEvents from a list of RuntimeEventChannels
     */
    public List<RuntimeEvent<?>> getLatestRuntimeEventsOfChannels(List<RuntimeEventChannel<?>> runtimeEventChannels);

    <T> RuntimeEvent<T> getLatestRuntimeEventOfChannel(RuntimeEventChannel<T> runtimeEventChannel);

    /**
     * Returns data for chart chart. This contains list with data for x-axis (time stamps) and y-axis (actual data). All
     * lists with data have the same length so that the appropriate points can be created from these.
     * 
     * @param channels
     * @param startTimestamp
     * @param endTimestamp
     * @param dataAsBars
     *            Flag that indicates if for each value a bar (same value at 2 subsequent timestamps) should be created
     *            instead of simple point.
     * @param AggregationTime
     *            Time span that is used when aggregating values in big data sets. When -1, no aggregation is applied.
     * @param aggregateForStackedMode
     *            If true and aggregation is active, only maximum in each interval is taken, and it is assigned the
     *            first timestamp from interval, to keep all aggregated timestamps aligned. (When showing chart data as
     *            stacked, each channel should have value in each timestamp provided, otherwise chart is showing
     *            "bumps")
     * @return LineChartData object that contains data for channels and desired time span and also one value before and
     *         one after time span to provide data for visualizing lines that start in time span but end outside of it
     */
    public LineChartData getLineChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp, long endTimestamp,
            boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode);

    public GanttChartData getGanttChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, Long aggregationTime);

    @Deprecated
    public int getStateId();

    @Deprecated
    public boolean hasStateIdChanged(Integer lastStateId);

    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channel);

    public void unregisterListener(ChannelsContentChangedListener listener);

}
