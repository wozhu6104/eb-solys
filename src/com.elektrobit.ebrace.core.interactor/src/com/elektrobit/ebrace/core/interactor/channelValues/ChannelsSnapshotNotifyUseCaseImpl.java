/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.channelValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.ChannelsSnapshotNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelvalues.SortColumn;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.channelValues.comparator.DecodedRuntimeEventValueEntryComparator;
import com.elektrobit.ebrace.core.interactor.channelValues.comparator.RuntimeEventChannelNameComparator;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourceChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;
import com.elektrobit.ebsolys.decoder.common.api.DecoderServiceManager;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

public class ChannelsSnapshotNotifyUseCaseImpl
        implements
            ChannelsSnapshotNotifyUseCase,
            TimeMarkersChangedListener,
            AnalysisTimespanChangedListener,
            ResourceChangedListener
{

    private ChannelsSnapshotNotifyCallback callback;
    private final RuntimeEventProvider runtimeEventProvider;
    private final TimeMarkerManager timemarkerManager;
    private final AnalysisTimespanPreferences analysisTimespanPreferences;
    private final UserInteractionPreferences userInteractionPreferences;
    private final DecoderServiceManager decoderServiceManager;
    private final ResourcesModelManager resourcesModelManager;

    private SortColumn sortColumn;
    private ResourceModel model;
    private long timestampOfResult;

    public ChannelsSnapshotNotifyUseCaseImpl(ChannelsSnapshotNotifyCallback callback,
            RuntimeEventProvider runtimeEventProvider, TimeMarkerManager timemarkerManager,
            AnalysisTimespanPreferences analysisTimespanPreferences,
            UserInteractionPreferences userInteractionPreferences, DecoderServiceManager decoderServiceManager,
            ResourcesModelManager resourcesModelManager)
    {
        this.callback = callback;
        this.runtimeEventProvider = runtimeEventProvider;
        this.timemarkerManager = timemarkerManager;
        this.analysisTimespanPreferences = analysisTimespanPreferences;
        this.userInteractionPreferences = userInteractionPreferences;
        this.decoderServiceManager = decoderServiceManager;
        this.resourcesModelManager = resourcesModelManager;
        registerListeners();
    }

    private void registerListeners()
    {
        timemarkerManager.registerListener( this );
        resourcesModelManager.registerResourceListener( this );
        analysisTimespanPreferences.addTimespanPreferencesChangedListener( this );
    }

    @Override
    public void register(ResourceModel model)
    {
        this.model = model;
    }

    @Override
    public void unregister()
    {
        unregisterListeners();
        callback = null;
    }

    private void unregisterListeners()
    {
        timemarkerManager.unregisterListener( this );
        resourcesModelManager.unregisterResourceListener( this );
        analysisTimespanPreferences.removeTimespanPreferencesChangedListener( this );
    }

    private void updateRuntimeEventValuesForTimestamp()
    {
        UseCaseExecutor
                .schedule( new UseCaseRunnable( "ChannelsSnapshotNotifyUseCase.updateRuntimeEventValuesForTimestamp",
                                                () -> processEventsAndPostInput() ) );
    }

    private void processEventsAndPostInput()
    {
        Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodedEventsNEW = getDecodedEvents();

        final Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> sortedMap = sortInput( decodedEventsNEW );

        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onNewInput( sortedMap, timestampOfResult );
                }
            }
        } );
    }

    private Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> getDecodedEvents()
    {
        final List<RuntimeEventChannel<?>> enabledChannels = model.getChannels();

        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result;
        if (userInteractionPreferences.isLiveMode())
        {
            List<RuntimeEvent<?>> lastEvents = runtimeEventProvider.getLatestRuntimeEventsOfChannels( enabledChannels );
            result = createMapOfResults( lastEvents, enabledChannels );
            timestampOfResult = analysisTimespanPreferences.getFullTimespanEnd();
        }
        else
        {
            TimeMarker selectedTimeMarker = timemarkerManager.getCurrentSelectedTimeMarker();
            if (selectedTimeMarker != null)
            {
                timestampOfResult = selectedTimeMarker.getTimestamp();
                result = runtimeEventProvider.getRuntimeEventsOfChannelsForTimestamp( enabledChannels,
                                                                                      timestampOfResult );
            }
            else
            {
                timestampOfResult = analysisTimespanPreferences.getFullTimespanEnd();
                result = new LinkedHashMap<RuntimeEventChannel<?>, RuntimeEvent<?>>();
                for (RuntimeEventChannel<?> runtimeEventChannel : enabledChannels)
                {
                    result.put( runtimeEventChannel, null );
                }
            }
        }

        addDisabledChannels( result );
        Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodedResult = decodeEvents( result );
        return decodedResult;
    }

    private Map<RuntimeEventChannel<?>, RuntimeEvent<?>> createMapOfResults(List<RuntimeEvent<?>> lastEvents,
            List<RuntimeEventChannel<?>> channels)
    {
        Map<RuntimeEventChannel<?>, RuntimeEvent<?>> map = new LinkedHashMap<RuntimeEventChannel<?>, RuntimeEvent<?>>();
        for (RuntimeEventChannel<?> runtimeEventChannel : channels)
        {
            map.put( runtimeEventChannel, null );
        }

        for (RuntimeEvent<?> runtimeEvent : lastEvents)
        {
            map.put( runtimeEvent.getRuntimeEventChannel(), runtimeEvent );
        }
        return map;
    }

    private void addDisabledChannels(Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result)
    {
        List<RuntimeEventChannel<?>> disabledChannels = model.getDisabledChannels();
        for (RuntimeEventChannel<?> disabledChannel : disabledChannels)
        {
            result.put( disabledChannel, null );
        }
    }

    private Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodeEvents(
            Map<RuntimeEventChannel<?>, RuntimeEvent<?>> result)
    {
        Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodedResult = new LinkedHashMap<RuntimeEventChannel<?>, DecodedRuntimeEvent>();

        for (Entry<RuntimeEventChannel<?>, RuntimeEvent<?>> entry : result.entrySet())
        {
            RuntimeEventChannel<?> channel = entry.getKey();
            RuntimeEvent<?> event = entry.getValue();
            if (event != null)
            {
                DecodedRuntimeEvent decodedEvent = decodeEvent( event );
                decodedResult.put( channel, decodedEvent );
            }
            else
            {
                decodedResult.put( channel, null );
            }
        }
        return decodedResult;
    }

    private DecodedRuntimeEvent decodeEvent(RuntimeEvent<?> event)
    {
        DecoderService decoderService = decoderServiceManager.getDecoderServiceForEvent( event );
        if (decoderService != null)
        {
            return decoderService.decode( event );
        }
        else
        {
            return null;
        }
    }

    private Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> sortInput(
            Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodedEventsNEW)
    {
        if (sortColumn == null)
        {
            return decodedEventsNEW;
        }

        if (sortColumn.equals( SortColumn.CHANNEL_ASC ) || sortColumn.equals( SortColumn.CHANNEL_DESC ))
        {
            return sortByChannel( decodedEventsNEW );
        }
        else if (sortColumn.equals( SortColumn.VALUE_ASC ) || sortColumn.equals( SortColumn.VALUE_DESC ))
        {
            return sortByValue( decodedEventsNEW );
        }
        return null;
    }

    private Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> sortByChannel(
            Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodedEvents)
    {
        Set<RuntimeEventChannel<?>> channelsSet = decodedEvents.keySet();
        List<RuntimeEventChannel<?>> channels = new ArrayList<RuntimeEventChannel<?>>( channelsSet );
        RuntimeEventChannelNameComparator channelNameComparator = new RuntimeEventChannelNameComparator();
        if (sortColumn.equals( SortColumn.CHANNEL_ASC ))
        {
            Collections.sort( channels, channelNameComparator );
        }
        else if (sortColumn.equals( SortColumn.CHANNEL_DESC ))
        {
            Collections.sort( channels, Collections.reverseOrder( channelNameComparator ) );
        }

        Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> sortedResult = new LinkedHashMap<RuntimeEventChannel<?>, DecodedRuntimeEvent>();
        for (RuntimeEventChannel<?> channel : channels)
        {
            sortedResult.put( channel, decodedEvents.get( channel ) );
        }
        return sortedResult;
    }

    private Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> sortByValue(
            Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> decodedEvents)
    {
        Set<Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent>> entrySet = decodedEvents.entrySet();
        List<Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent>> entryList = new ArrayList<Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent>>( entrySet );

        DecodedRuntimeEventValueEntryComparator decodedEntryComparator = new DecodedRuntimeEventValueEntryComparator();

        if (sortColumn.equals( SortColumn.VALUE_ASC ))
        {
            Collections.sort( entryList, decodedEntryComparator );
        }
        else if (sortColumn.equals( SortColumn.VALUE_DESC ))
        {
            Collections.sort( entryList, Collections.reverseOrder( decodedEntryComparator ) );
        }

        Map<RuntimeEventChannel<?>, DecodedRuntimeEvent> sortedResult = new LinkedHashMap<RuntimeEventChannel<?>, DecodedRuntimeEvent>();

        for (Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent> entry : entryList)
        {
            sortedResult.put( entry.getKey(), entry.getValue() );
        }
        return sortedResult;
    }

    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerSelected(TimeMarker timeMarker)
    {
        updateRuntimeEventValuesForTimestamp();
    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerNameChanged(TimeMarker timeMarker)
    {
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
        if (timeMarker.equals( timemarkerManager.getCurrentSelectedTimeMarker() ))
        {
            updateRuntimeEventValuesForTimestamp();
        }
    }

    @Override
    public void allTimeMarkersRemoved()
    {
        updateRuntimeEventValuesForTimestamp();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
    }

    @Override
    public void analysisTimespanLengthChanged(long timespanMicros)
    {
    }

    @Override
    public void fullTimespanEndTimeChanged(long timespanEndMicros)
    {
        updateRuntimeEventValuesForTimestamp();
    }

    @Override
    public void onAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
    }

    @Override
    public void setSorting(SortColumn sorting)
    {
        sortColumn = sorting;
        updateRuntimeEventValuesForTimestamp();
    }

    @Override
    public void onResourceModelChannelsChanged(ResourceModel resourceModel)
    {
        if (model != null)
        {
            if (model.equals( resourceModel ))
            {
                updateRuntimeEventValuesForTimestamp();
            }
        }
    }

    @Override
    public void onResourceModelSelectedChannelsChanged(ResourceModel resourceModel)
    {
    }
}
