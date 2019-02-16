/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.FilterScriptCallerListener;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRepeatedTask;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.interactor.tableinput.filter.FilterUtil;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.resources.api.manager.ResourceChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.listener.ChannelsContentChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventTag;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class RuntimeEventTableDataNotifyUseCaseImpl
        implements
            RuntimeEventTableDataNotifyUseCase,
            ResourceChangedListener,
            TimeMarkersChangedListener,
            UserInteractionPreferencesListener,
            FilterScriptCallerListener,
            ChannelsContentChangedListener
{
    private static final int UPDATE_TIMER_PERIOD_MS = 2000;

    private RuntimeEventTableDataNotifyCallback callback;

    private final List<RowFormatter> rowFormatters;

    private final RuntimeEventProvider runtimeEventProvider;
    private final AnalysisTimespanPreferences analysisTimespanPreferences;
    private final ResourcesModelManager resourcesModelManager;
    private final UserInteractionPreferences userInteractionPreferences;
    private final TableModel tableModel;
    private final TimeMarkerManager timeMarkerManager;
    private SEARCH_MODE searchMode = SEARCH_MODE.FILTER;
    private ServiceRegistration<?> serviceRegistration;

    private String filterText = "";

    private RuntimeEventTableDataWorker plannedRunnable;
    private volatile boolean runnableRunning = false;
    private volatile boolean initialTimeMarkerPosted = false;

    private UseCaseRepeatedTask repeatedTask;
    private RaceScriptMethod filterMethod;

    private final ScriptExecutorService scriptExecutorService;

    public RuntimeEventTableDataNotifyUseCaseImpl(RuntimeEventTableDataNotifyCallback callback,
            List<RowFormatter> rowFormatters, RuntimeEventProvider runtimeEventProvider,
            AnalysisTimespanPreferences analysisTimespanPreferences, ResourcesModelManager resourcesModelManager,
            TableModel tableModel, UserInteractionPreferences userInteractionPreferences,
            TimeMarkerManager timeMarkerManager, ScriptExecutorService scriptExecutorService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertReferenceParameterNotNull( "rowFormatters", rowFormatters );
        RangeCheckUtils.assertReferenceParameterNotNull( "runtimeEventProvider", runtimeEventProvider );
        RangeCheckUtils.assertReferenceParameterNotNull( "analysisTimespanPreferences", analysisTimespanPreferences );
        RangeCheckUtils.assertReferenceParameterNotNull( "tableModel", tableModel );
        RangeCheckUtils.assertReferenceParameterNotNull( "userInteractionPreferences", userInteractionPreferences );
        RangeCheckUtils.assertReferenceParameterNotNull( "timeMarkerManager", timeMarkerManager );
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptExecutorService", scriptExecutorService );

        this.callback = callback;
        this.rowFormatters = rowFormatters;
        this.runtimeEventProvider = runtimeEventProvider;
        this.analysisTimespanPreferences = analysisTimespanPreferences;
        this.resourcesModelManager = resourcesModelManager;
        this.tableModel = tableModel;
        this.userInteractionPreferences = userInteractionPreferences;
        this.timeMarkerManager = timeMarkerManager;
        this.scriptExecutorService = scriptExecutorService;

        registerListeners();
        createDataPostTimer();
        planNewWorkerRunnable();
    }

    private void registerListeners()
    {
        timeMarkerManager.registerListener( this );
        resourcesModelManager.registerResourceListener( this );
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // runtimeEventProvider.register( this, tableModel.getChannels() );
    }

    private void createDataPostTimer()
    {
        repeatedTask = UseCaseExecutor.scheduleRepeated( new UseCaseRepeatedTask()
        {
            private Integer stateId = null;

            @Override
            public void execute()
            {
                if (runtimeEventProvider.hasStateIdChanged( stateId ))
                {
                    stateId = runtimeEventProvider.getStateId();
                    planNewWorkerRunnable();
                }
            }
        }, UPDATE_TIMER_PERIOD_MS );
    }

    private void planNewWorkerRunnable()
    {
        plannedRunnable = new RuntimeEventTableDataWorker( filterText, filterMethod, searchMode, this );
        postWaitingRunnableIfAny();
    }

    private void postWaitingRunnableIfAny()
    {
        if (runnableRunning)
        {
            return;
        }
        if (plannedRunnable != null)
        {
            runnableRunning = true;
            RuntimeEventTableDataWorker runnableCopy = plannedRunnable;
            plannedRunnable = null;
            UseCaseExecutor
                    .schedule( new UseCaseRunnable( "RuntimeEventTableDataNotifyUseCase.postWaitingRunnableIfAny",
                                                    runnableCopy ) );
        }
    }

    public void collectAndPostData(String searchText, RaceScriptMethod filterMethod, SEARCH_MODE searchMode)
    {
        notifyFilteringStarted();
        List<RuntimeEvent<?>> collectedTableItems = collectData();

        if (filterMethod != null)
        {
            startScriptFiltering( collectedTableItems, filterMethod, searchMode );
        }
        else
        {
            TableData result = applySearchOrFilter( collectedTableItems, searchText, searchMode );
            result = mixInTimeMarkers( result );
            addTaggedRuntimeEvents( result, collectedTableItems );
            postCollectedData( result );
        }
    }

    private void addTaggedRuntimeEvents(TableData result, List<RuntimeEvent<?>> allRuntimeEvnets)
    {
        List<RuntimeEvent<?>> taggedEvents = new ArrayList<RuntimeEvent<?>>();
        for (RuntimeEvent<?> event : allRuntimeEvnets)
        {
            if (event.isTagged() && RuntimeEventTag.ERROR.equals( event.getTag() ))
            {
                taggedEvents.add( event );
            }
        }
        result.setTaggedEvents( taggedEvents );
    }

    private List<RuntimeEvent<?>> collectData()
    {
        long timespanStart = analysisTimespanPreferences.getFullTimespanStart();
        long timespanEnd = analysisTimespanPreferences.getFullTimespanEnd();
        List<RuntimeEventChannel<?>> channels = tableModel.getChannels();

        List<RuntimeEvent<?>> events = runtimeEventProvider
                .getRuntimeEventForTimeStampIntervalForChannels( timespanStart, timespanEnd, channels );

        return events;
    }

    private TableData applySearchOrFilter(List<RuntimeEvent<?>> collectedTableItems, String searchText,
            SEARCH_MODE searchMode)
    {
        if (searchMode == SEARCH_MODE.FILTER)
        {
            return FilterUtil.filter( collectedTableItems, searchText, rowFormatters );
        }
        else
        {
            if (searchMode == SEARCH_MODE.SEARCH)
            {
                return FilterUtil.search( collectedTableItems, searchText, rowFormatters );
            }
            else
            {
                throw new IllegalArgumentException( "unknown search mode " + searchMode );
            }
        }
    }

    private void startScriptFiltering(List<RuntimeEvent<?>> collectedTableItems, RaceScriptMethod methodForThisRun,
            SEARCH_MODE searchMode)
    {
        RaceScriptResourceModel scriptModel = getScriptByName( methodForThisRun.getScriptName() );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        scriptExecutorService
                .runFilterScript( script, methodForThisRun, collectedTableItems, this, searchMode, rowFormatters );
    }

    private RaceScriptResourceModel getScriptByName(String scriptName)
    {
        List<RaceScriptResourceModel> allScripts = resourcesModelManager.getAllScripts();
        for (RaceScriptResourceModel resourceModel : allScripts)
        {
            if (resourceModel.getName().equals( scriptName ))
            {
                return resourceModel;
            }
        }
        return null;
    }

    private TableData mixInTimeMarkers(TableData result)
    {
        List<TimeMarker> timemarkers = new ArrayList<TimeMarker>( timeMarkerManager.getAllVisibleTimemarkers() );

        @SuppressWarnings("unchecked")
        List<TimebasedObject> tableItems = TimeMarkerMixer
                .mixAndSortTimemarkers( (List<TimebasedObject>)result.getItemsToBeDisplayed(), timemarkers );
        TableData resultWithTimeMarkers = new TableDataImpl( tableItems,
                                                             result.getSearchPositionMap(),
                                                             result.getTaggedEvents() );
        return resultWithTimeMarkers;
    }

    public void onWorkerRunnableDone()
    {
        runnableRunning = false;
        postWaitingRunnableIfAny();
    }

    private void notifyFilteringStarted()
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onFilteringStarted();
                }
            }
        } );
    }

    private void postCollectedData(final TableData filterResultData)
    {
        final boolean jumpToTableEnd = userInteractionPreferences.isLiveMode();
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onTableInputCollected( filterResultData, jumpToTableEnd );
                }
                jumpToActiveTimeMarkerAfterFirstDataPosted();
            }

        } );
    }

    private void jumpToActiveTimeMarkerAfterFirstDataPosted()
    {
        if (!initialTimeMarkerPosted)
        {
            initialTimeMarkerPosted = true;
            if (!userInteractionPreferences.isLiveMode())
            {
                jumpToSelectedTimeMarker();
            }
        }
    }

    private void jumpToSelectedTimeMarker()
    {
        TimeMarker selectedTimeMarker = timeMarkerManager.getCurrentSelectedTimeMarker();
        if (selectedTimeMarker != null)
        {
            timeMarkerSelected( selectedTimeMarker );
        }
    }

    @Override
    public void setFilterText(String text)
    {
        filterMethod = null;
        filterText = text;
        planNewWorkerRunnable();
    }

    @Override
    public void setFilterMethod(RaceScriptMethod method)
    {
        this.filterMethod = method;
        filterText = "";
        planNewWorkerRunnable();
    }

    @Override
    public void onResourceModelChannelsChanged(ResourceModel resourceModel)
    {
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // runtimeEventProvider.register( this, resourceModel.getChannels() );
        if (tableModel.equals( resourceModel ))
        {
            planNewWorkerRunnable();
        }
    }

    @Override
    public void setSearchMode(SEARCH_MODE searchMode)
    {
        this.searchMode = searchMode;
        planNewWorkerRunnable();
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        planNewWorkerRunnable();
    }

    @Override
    public void unregister()
    {
        repeatedTask.cancel();
        resourcesModelManager.unregisterResourceListener( this );
        timeMarkerManager.unregisterListener( this );
        callback = null;
        RangeCheckUtils.assertReferenceParameterNotNull( "Service registration", serviceRegistration );
        serviceRegistration.unregister();
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // runtimeEventProvider.unregister( this );
    }

    public void setServiceRegistration(ServiceRegistration<?> serviceRegistration)
    {
        this.serviceRegistration = serviceRegistration;
    }

    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
        planNewWorkerRunnable();
    }

    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
        planNewWorkerRunnable();
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
        planNewWorkerRunnable();
    }

    @Override
    public void timeMarkerSelected(final TimeMarker timeMarker)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onJumpToTimeMarker( timeMarker );
                }
            }
        } );
    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
        planNewWorkerRunnable();
    }

    @Override
    public void allTimeMarkersRemoved()
    {
        planNewWorkerRunnable();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
        planNewWorkerRunnable();
    }

    @Override
    public void timeMarkerNameChanged(final TimeMarker timeMarker)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onTimeMarkerRenamed( timeMarker );
                }
            }
        } );
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
        planNewWorkerRunnable();
    }

    @Override
    public void onScriptFilteringDone(List<RuntimeEvent<?>> result,
            Map<?, Map<RowFormatter, List<Position>>> searchPositionList)
    {
        List<?> resultNoType = result;
        TableData tableResult = new TableDataImpl( resultNoType, searchPositionList );
        tableResult = mixInTimeMarkers( tableResult );
        postCollectedData( tableResult );
    }

    @Override
    public void onChannelsContentChanged()
    {
        // TODO: uncomment when we switch back to callback. See EBRACE-2810
        // planNewWorkerRunnable();
    }

    @Override
    public void onResourceModelSelectedChannelsChanged(ResourceModel resourceModel)
    {
    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        // TODO Auto-generated method stub

    }
}
