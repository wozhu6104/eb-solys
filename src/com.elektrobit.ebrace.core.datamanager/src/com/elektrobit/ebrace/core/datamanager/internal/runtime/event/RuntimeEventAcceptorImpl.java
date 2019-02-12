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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.memory.MemoryObserver;
import com.elektrobit.ebrace.common.profiling.PerformanceUtils;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.datamanager.api.channels.ChannelListenerNotifier;
import com.elektrobit.ebrace.core.datamanager.api.channels.RuntimeEventChannelManager;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.api.RuntimeEventNotifier;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElementPool;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelRemovedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.GanttChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.LineChartData;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.util.RuntimeEventTimestampComparator;

import de.systemticks.ebrace.core.eventhook.registry.api.EventHookRegistry;

public class RuntimeEventAcceptorImpl implements RuntimeEventAcceptor, RuntimeEventProvider
{
    private final static Logger LOG = Logger.getLogger( RuntimeEventAcceptorImpl.class );
    private final static int EVENT_DELETE_PORTION = 50000;

    private final GenericOSGIServiceTracker<AnalysisTimespanPreferences> analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class,
                                                                                                                                                                   new AnalysisTimespanPreferences()
                                                                                                                                                                   {

                                                                                                                                                                       @Override
                                                                                                                                                                       public long getFullTimespanStart()
                                                                                                                                                                       {
                                                                                                                                                                           return 1;
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public long getFullTimespanEnd()
                                                                                                                                                                       {
                                                                                                                                                                           return 60000000;
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public long getAnalysisTimespanStart()
                                                                                                                                                                       {
                                                                                                                                                                           return 1;
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public long getAnalysisTimespanEnd()
                                                                                                                                                                       {
                                                                                                                                                                           return 60000000;
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public long getAnalysisTimespanLength()
                                                                                                                                                                       {
                                                                                                                                                                           return 60000000;
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public void addTimespanPreferencesChangedListener(
                                                                                                                                                                               AnalysisTimespanChangedListener analysisTimespanChangedListenerToAdd)
                                                                                                                                                                       {
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public void removeTimespanPreferencesChangedListener(
                                                                                                                                                                               AnalysisTimespanChangedListener analysisTimespanChangedListenerToRemove)
                                                                                                                                                                       {
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public void setAnalysisTimespanEnd(
                                                                                                                                                                               long newValue,
                                                                                                                                                                               ANALYSIS_TIMESPAN_CHANGE_REASON reason)
                                                                                                                                                                       {
                                                                                                                                                                       }

                                                                                                                                                                       @Override
                                                                                                                                                                       public void setAnalysisTimespanLength(
                                                                                                                                                                               long analysisTimespanInMillis)
                                                                                                                                                                       {
                                                                                                                                                                       }

                                                                                                                                                                   } );

    private final List<RuntimeEvent<?>> runtimeEvents = Collections
            .synchronizedList( new ArrayList<RuntimeEvent<?>>() );

    private volatile int stateID = 0;
    private volatile boolean sortNeeded = false;

    private final ModelElementPool modelElementPool;

    private final RuntimeEventNotifier runtimeEventNotifier;
    private final RuntimeEventChannelManager runtimeEventChannelManager;

    private final ChannelListenerNotifier channelListenerNotifier;
    private final List<ChannelRemovedListener> channelRemovedListeners;
    private final EventHookRegistry eventhookRegistry;

    private long lastRemoveOutdatedEvents = System.currentTimeMillis();

    public RuntimeEventAcceptorImpl(RuntimeEventChannelManager runtimeEventChannelManager,
            ModelElementPool modelElementPool, RuntimeEventNotifier runtimeEventNotifier,
            ChannelListenerNotifier channelListenerNotifier, EventHookRegistry eventhookRegistry)
    {
        this.runtimeEventChannelManager = runtimeEventChannelManager;
        this.modelElementPool = modelElementPool;
        this.runtimeEventNotifier = runtimeEventNotifier;
        this.channelListenerNotifier = channelListenerNotifier;
        this.eventhookRegistry = eventhookRegistry;
        channelRemovedListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void registerListener(ChannelsContentChangedListener listener, List<RuntimeEventChannel<?>> channel)
    {
        channelListenerNotifier.registerListener( listener, channel );
    }

    @Override
    public void unregisterListener(ChannelsContentChangedListener listener)
    {
        channelListenerNotifier.unregisterListener( listener );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description)
    {
        return runtimeEventChannelManager.createOrGetRuntimeEventChannel( context, name, unit, description );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription)
    {
        return runtimeEventChannelManager.createOrGetRuntimeEventChannel( channelName, unit, channelDescription );
    }

    @Override
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description)
    {
        return runtimeEventChannelManager.createRuntimeEventChannel( name, unit, description );
    }

    @Override
    public void renameRuntimeEventChannel(RuntimeEventChannel<?> channel, String newName)
    {
        runtimeEventChannelManager.renameRuntimeEventChannel( channel, newName );
    }

    @Override
    public synchronized <T> RuntimeEvent<T> acceptEvent(long timestamp, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value)
    {
        return acceptEventMicros( timestamp * 1000, channel, modelElement, value );
    }

    @Override
    public synchronized <T> RuntimeEvent<T> acceptEventMicros(long timestampUS, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value, String summary)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "RuntimeEventChannel", channel );
        if (!runtimeEventChannelManager.checkIfChannelWithNameExists( channel.getName() ))
        {
            throw new IllegalArgumentException( "Channel '" + channel.getName() + "' was removed." );
        }
        if (value instanceof Float)
        {
            throw new IllegalArgumentException( "Please use Double instead of Float as a channel type" );
        }

        long modelElementId = getNullSaveModelElementId( modelElement );

        RuntimeEventObjectImpl<T> newRuntimeEvent = new RuntimeEventObjectImpl<T>( timestampUS,
                                                                                   channel,
                                                                                   modelElementId,
                                                                                   value,
                                                                                   summary,
                                                                                   modelElementPool );

        runtimeEvents.add( newRuntimeEvent );
        noteIfSortBrokenByLastInsert();
        removeOutdatedEvents();
        runtimeEventNotifier.notifyAboutNewEvent( newRuntimeEvent );
        stateID++;

        channelListenerNotifier.notifyChannelChanged( channel );
        eventhookRegistry.callFor( newRuntimeEvent );

        return newRuntimeEvent;
    }

    private void removeOutdatedEvents()
    {
        long now = System.currentTimeMillis();
        long timeSinceLAstRemoveOutdatedCall = now - lastRemoveOutdatedEvents;
        long analysisTimespanLengthMicros = analysisTimespanPreferences.getService().getAnalysisTimespanLength();
        long analysisTimespanLengthMillis = analysisTimespanLengthMicros / 1000;

        if (timeSinceLAstRemoveOutdatedCall > analysisTimespanLengthMillis)
        {
            if (sortNeeded)
            {
                sortEvents();
            }
            boolean mustDelete = true;
            int index = 0;
            while ((runtimeEvents.get( index )
                    .getTimestamp()) < (runtimeEvents.get( runtimeEvents.size() - 1 ).getTimestamp()
                            - analysisTimespanLengthMicros))
            {
                index++;
                if (index >= runtimeEvents.size() - 1)
                {
                    mustDelete = false;
                    break;
                }
            }
            if (mustDelete)
            {
                runtimeEvents.subList( 0, index ).clear();
                LOG.info( "Removing " + index + " outdated events." );
            }
            lastRemoveOutdatedEvents = System.currentTimeMillis();
        }
    }

    @Override
    public <T> RuntimeEvent<T> acceptEventMicros(long timestampUS, RuntimeEventChannel<T> channel,
            ModelElement modelElement, T value)
    {
        return acceptEventMicros( timestampUS, channel, modelElement, value, null );
    }

    private void noteIfSortBrokenByLastInsert()
    {
        if (runtimeEvents.size() < 2 || sortNeeded)
        {
            return;
        }
        RuntimeEvent<?> lastEvent = runtimeEvents.get( runtimeEvents.size() - 1 );
        RuntimeEvent<?> secondLastEvent = runtimeEvents.get( runtimeEvents.size() - 2 );

        if (lastEvent.getTimestamp() < secondLastEvent.getTimestamp())
        {
            sortNeeded = true;
        }
    }

    /**
     * Sorts runtime events if necessary. Shall be use by each query method.
     * 
     * @return Returns a sorted copied read only list of all runtime events.
     */
    private List<RuntimeEvent<?>> getSortedReadOnlyListOfRuntimeEvents()
    {
        if (sortNeeded)
        {
            sortEvents();
            sortNeeded = false;
        }

        return Collections.unmodifiableList( new ArrayList<RuntimeEvent<?>>( runtimeEvents ) );
    }

    private void removeEventsIfMemoryFull()
    {
        if (MemoryObserver.isEnoughFreeMemoryAvailable())
        {
            return;
        }
        removeOldEvents();
    }

    private synchronized void removeOldEvents()
    {
        LOG.warn( "Limit reached: removing events from Data Manager" );

        if (sortNeeded)
        {
            sortEvents();
            sortNeeded = false;
        }

        int eventsSize = runtimeEvents.size();
        if (eventsSize > 2 * EVENT_DELETE_PORTION)
        {
            runtimeEvents.subList( 0, EVENT_DELETE_PORTION ).clear();
        }
        else
        {
            int deletePortion = eventsSize / 2;
            runtimeEvents.subList( 0, deletePortion ).clear();
        }
    }

    private synchronized void sortEvents()
    {
        PerformanceUtils.startMeasure( "Sort time" );
        Collections.sort( runtimeEvents, new RuntimeEventTimestampComparator() );
        PerformanceUtils.stopMeasure( "Sort time" );
        PerformanceUtils.printTimingResult( "Sort time" );
        PerformanceUtils.clearTimingResult( "Sort time" );
    }

    private long getNullSaveModelElementId(ModelElement modelElement)
    {
        long modelElementId = ModelElement.NULL_MODEL_ELEMENT.getUniqueModelElementID();
        if (modelElement != null)
        {
            modelElementId = modelElement.getUniqueModelElementID();
        }
        return modelElementId;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public <T> RuntimeEventChannel<T> getRuntimeEventChannel(String name, Class<T> type)
    {
        return (RuntimeEventChannel<T>)runtimeEventChannelManager.getRuntimeEventChannelWithName( name );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> RuntimeEventChannel<T> getRuntimeEventChannel(String name)
    {
        return (RuntimeEventChannel<T>)runtimeEventChannelManager.getRuntimeEventChannelWithName( name );
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannels()
    {
        return runtimeEventChannelManager.getRuntimeEventChannels();
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfTimespan(long startTimestamp, long endTimestamp)
    {
        List<RuntimeEvent<?>> resultRuntimeEventList = new ArrayList<RuntimeEvent<?>>();
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        for (RuntimeEvent<?> nextRuntimeEvent : sortedEvents)
        {
            if (nextRuntimeEvent.getTimestamp() >= startTimestamp && nextRuntimeEvent.getTimestamp() <= endTimestamp)
            {
                resultRuntimeEventList.add( nextRuntimeEvent );
            }

            if (nextRuntimeEvent.getTimestamp() > endTimestamp)
            {
                break;
            }
        }

        return resultRuntimeEventList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> RuntimeEvent<T> getLatestRuntimeEventOfChannel(RuntimeEventChannel<T> runtimeEventChannel)
    {
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        RuntimeEvent<T> lastRuntimeEvent = null;
        for (RuntimeEvent<?> runtimeEvent : sortedEvents)
        {
            if (runtimeEvent.getRuntimeEventChannel().equals( runtimeEventChannel ))
            {
                if (lastRuntimeEvent == null || lastRuntimeEvent.getTimestamp() < runtimeEvent.getTimestamp())
                {
                    lastRuntimeEvent = (RuntimeEvent<T>)runtimeEvent;
                }
            }
        }
        return lastRuntimeEvent;
    }

    @Override
    public RuntimeEvent<?> getLatestRuntimeEvent()
    {
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        if (sortedEvents.isEmpty())
        {
            return null;
        }
        else
        {
            return sortedEvents.get( sortedEvents.size() - 1 );
        }
    }

    @Override
    public RuntimeEvent<?> getFirstRuntimeEvent()
    {
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        if (sortedEvents.isEmpty())
        {
            return null;
        }
        else
        {
            return sortedEvents.get( 0 );
        }
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfModelElement(ModelElement modelElement)
    {
        return getRuntimeEventsOfModelElements( Arrays.asList( modelElement ) );
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfModelElements(List<ModelElement> modelElements)
    {
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        List<RuntimeEvent<?>> resultRuntimeEventList = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> nextRuntimeEvent : sortedEvents)
        {
            ModelElement modelElement = nextRuntimeEvent.getModelElement();
            if (modelElements.contains( modelElement ))
            {
                resultRuntimeEventList.add( nextRuntimeEvent );
            }
        }

        return resultRuntimeEventList;
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannel(RuntimeEventChannel<?> runtimeEventChannel)
    {
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        List<RuntimeEvent<?>> resultRuntimeEventList = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> nextRuntimeEvent : sortedEvents)
        {
            if (nextRuntimeEvent.getRuntimeEventChannel().equals( runtimeEventChannel ))
            {
                resultRuntimeEventList.add( nextRuntimeEvent );
            }
        }

        return resultRuntimeEventList;
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventsOfRuntimeEventChannels(List<RuntimeEventChannel<?>> channels)
    {
        List<RuntimeEvent<?>> cachedSortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        List<RuntimeEvent<?>> resultRuntimeEventList = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> nextRuntimeEvent : cachedSortedEvents)
        {
            RuntimeEventChannel<?> channel = nextRuntimeEvent.getRuntimeEventChannel();
            if (channels.contains( channel ))
            {
                resultRuntimeEventList.add( nextRuntimeEvent );
            }
        }

        return resultRuntimeEventList;
    }

    @Override
    public synchronized void dispose()
    {
        runtimeEventChannelManager.clear();
        runtimeEvents.clear();
        stateID++;
        // eventListenerNotifier.notifyAll();
    }

    @Override
    public RuntimeEvent<?> getLastRuntimeEventForTimeStampInterval(long start, long end, RuntimeEventChannel<?> channel)
    {
        RangeCheckUtils.assertCorrectIntervalBoundaries( "Timestamp interval", start, end );

        List<RuntimeEvent<?>> eventsInTimeSpan = getRuntimeEventsOfTimespan( start, end );

        for (int i = eventsInTimeSpan.size() - 1; i >= 0; --i)
        {
            RuntimeEvent<?> runtimeEvent = eventsInTimeSpan.get( i );
            if (runtimeEvent.getRuntimeEventChannel().equals( channel ))
            {
                return runtimeEvent;
            }
        }
        return null;
    }

    @Override
    public RuntimeEvent<?> getFirstRuntimeEventForTimeStampInterval(long start, long end,
            RuntimeEventChannel<?> channel)
    {
        RangeCheckUtils.assertCorrectIntervalBoundaries( "Timestamp interval", start, end );

        List<RuntimeEvent<?>> eventsInTimeSpan = getRuntimeEventsOfTimespan( start, end );

        for (RuntimeEvent<?> runtimeEvent : eventsInTimeSpan)
        {
            if (runtimeEvent.getRuntimeEventChannel().equals( channel ))
            {
                return runtimeEvent;
            }
        }
        return null;
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForModelElement(ModelElement element)
    {
        List<RuntimeEventChannel<?>> channelsForME = new ArrayList<RuntimeEventChannel<?>>();
        for (RuntimeEvent<?> event : getRuntimeEventsOfModelElement( element ))
        {
            RuntimeEventChannel<?> channel = event.getRuntimeEventChannel();
            if (!channelsForME.contains( channel ))
            {
                channelsForME.add( channel );
            }
        }
        return channelsForME;
    }

    @Override
    public List<RuntimeEvent<?>> getAllRuntimeEvents()
    {
        return getSortedReadOnlyListOfRuntimeEvents();
    }

    @Override
    @Deprecated
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForType(Class<?> type)
    {
        List<RuntimeEventChannel<?>> result = new ArrayList<RuntimeEventChannel<?>>();
        for (RuntimeEventChannel<?> channel : getRuntimeEventChannels())
        {

            if (type.isAssignableFrom( channel.getUnit().getDataType() ))
            {
                result.add( channel );
            }
        }
        return result;
    }

    @Override
    public List<RuntimeEventChannel<?>> getRuntimeEventChannelsForUnit(Unit<?> unit)
    {
        return runtimeEventChannelManager.getRuntimeEventChannelsForUnit( unit );
    }

    @Override
    public List<RuntimeEvent<?>> getRuntimeEventForTimeStampIntervalForChannels(long start, long end,
            List<RuntimeEventChannel<?>> channelsList)
    {
        Set<RuntimeEventChannel<?>> channels = new HashSet<RuntimeEventChannel<?>>( channelsList );
        RangeCheckUtils.assertCorrectIntervalBoundaries( "Timestamp interval", start, end );
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        List<RuntimeEvent<?>> resultRuntimeEventList = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> nextRuntimeEvent : sortedEvents)
        {
            RuntimeEventChannel<?> channel = nextRuntimeEvent.getRuntimeEventChannel();
            long timestamp = nextRuntimeEvent.getTimestamp();
            if (channels.contains( channel ) && start <= timestamp && timestamp <= end)
            {
                resultRuntimeEventList.add( nextRuntimeEvent );
            }
        }

        return resultRuntimeEventList;
    }

    @Override
    public LineChartData getLineChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp, long endTimestamp,
            boolean dataAsBars, Long aggregationTime, boolean aggregateForStackedMode)
    {
        if (startTimestamp > endTimestamp)
        {
            LOG.warn( "Start timestamp after end timestamp. No data will be collected. Ignore this warning on reset." );
            return null;
        }
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();

        LineChartDataBuilder chartDataBuilder = new LineChartDataBuilder( channels,
                                                                          startTimestamp,
                                                                          endTimestamp,
                                                                          sortedEvents,
                                                                          dataAsBars,
                                                                          aggregationTime,
                                                                          aggregateForStackedMode );
        return chartDataBuilder.build();
    }

    @Override
    public GanttChartData getGanttChartData(List<RuntimeEventChannel<?>> channels, long startTimestamp,
            long endTimestamp, Long aggregationTime)
    {
        RangeCheckUtils.assertCorrectIntervalBoundaries( "Timestamp interval", startTimestamp, endTimestamp );
        return new GanttChartDataBuilder( channels, startTimestamp, endTimestamp, this, aggregationTime ).build();
    }

    @Override
    public int getStateId()
    {
        return stateID;
    }

    @Override
    public boolean hasStateIdChanged(Integer lastStateId)
    {
        if (lastStateId == null)
        {
            return true;
        }
        return stateID != lastStateId;
    }

    @Override
    public Map<RuntimeEventChannel<?>, RuntimeEvent<?>> getRuntimeEventsOfChannelsForTimestamp(
            List<RuntimeEventChannel<?>> runtimeEventChannels, long timestamp)
    {
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();
        RuntimeEventsOfChannelsForTimestampDataBuilder builder = new RuntimeEventsOfChannelsForTimestampDataBuilder( runtimeEventChannels,
                                                                                                                     timestamp,
                                                                                                                     sortedEvents );
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result = builder.build();
        return result;
    }

    @Override
    public List<RuntimeEvent<?>> getLatestRuntimeEventsOfChannels(List<RuntimeEventChannel<?>> runtimeEventChannels)
    {
        List<RuntimeEvent<?>> result = new ArrayList<RuntimeEvent<?>>();
        List<RuntimeEvent<?>> sortedEvents = getSortedReadOnlyListOfRuntimeEvents();

        List<RuntimeEventChannel<?>> channelsCopy = new ArrayList<RuntimeEventChannel<?>>( runtimeEventChannels );
        for (int i = sortedEvents.size() - 1; i >= 0; i--)
        {
            if (channelsCopy.isEmpty())
            {
                break;
            }

            if (channelsCopy.contains( sortedEvents.get( i ).getRuntimeEventChannel() ))
            {
                result.add( sortedEvents.get( i ) );
                channelsCopy.remove( sortedEvents.get( i ).getRuntimeEventChannel() );
            }
        }
        return result;
    }

    @Override
    public RuntimeEvent<?> setTag(RuntimeEvent<?> event, RuntimeEventTag tag, String tagDescription)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "RuntimeEvent", event );
        RangeCheckUtils.assertReferenceParameterNotNull( "RuntimeEventTag", tag );

        ((RuntimeEventObjectImpl<?>)event).setTagWithDescription( tag, tagDescription );
        stateID++;
        // eventListenerNotifier.notifyAll();
        return event;
    }

    @Override
    public RuntimeEvent<?> clearTag(RuntimeEvent<?> event)
    {
        ((RuntimeEventObjectImpl<?>)event).clearTag();
        stateID++;
        // eventListenerNotifier.notifyAll();
        return event;
    }

    @Override
    public <T> RuntimeEventChannel<T> createRuntimeEventChannel(String name, Unit<T> unit, String description,
            List<String> valueColumns)
    {
        return runtimeEventChannelManager.createOrGetRuntimeEventChannel( name, unit, description, valueColumns );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(DataSourceContext context, String name,
            Unit<T> unit, String description, List<String> valueColumns)
    {
        return runtimeEventChannelManager
                .createOrGetRuntimeEventChannel( context, name, unit, description, valueColumns );
    }

    @Override
    public <T> RuntimeEventChannel<T> createOrGetRuntimeEventChannel(String channelName, Unit<T> unit,
            String channelDescription, List<String> valueColumns)
    {
        return runtimeEventChannelManager
                .createOrGetRuntimeEventChannel( channelName, unit, channelDescription, valueColumns );
    }

    @Override
    public void setParameter(RuntimeEventChannel<?> channel, String key, Object value)
    {
        ((RuntimeEventChannelObjectImpl<?>)channel).setParameter( key, value );
        channelListenerNotifier.notifyChannelChanged( channel );
    }

    @Override
    public void setParameters(RuntimeEventChannel<?> channel, Map<String, Object> parameters)
    {
        ((RuntimeEventChannelObjectImpl<?>)channel).setParameters( parameters );
        channelListenerNotifier.notifyChannelChanged( channel );
    }

    @Override
    public <T> void removeRuntimeEventChannel(RuntimeEventChannel<T> channel)
    {
        runtimeEventChannelManager.removeRuntimeEventChannel( channel );
        removeAssociatedEvents( channel );
        notifyChannelRemovedListeners( channel );
        // TODO: check this
        // channelListenerNotifier.notifyChannelRemoved( channel );
    }

    private <T> void notifyChannelRemovedListeners(RuntimeEventChannel<T> channel)
    {
        Iterator<ChannelRemovedListener> iterator = channelRemovedListeners.iterator();
        while (iterator.hasNext())
        {
            iterator.next().onRuntimeEventChannelRemoved( channel );
        }
    }

    private <T> void removeAssociatedEvents(RuntimeEventChannel<T> channel)
    {
        Iterator<RuntimeEvent<?>> iterator = runtimeEvents.iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().getRuntimeEventChannel().equals( channel ))
            {
                iterator.remove();
            }
        }
    }

    @Override
    public void addChannelRemovedListener(ChannelRemovedListener listener)
    {
        channelRemovedListeners.add( listener );

    }

    @Override
    public void removeChannelRemovedListener(ChannelRemovedListener listener)
    {
        channelRemovedListeners.remove( listener );
    }

}
