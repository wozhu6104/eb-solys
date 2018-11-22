/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.structure.RuntimeEventsOfSelectedComrelationNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.RuntimeEventsOfSelectedComrelationNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.common.UseCaseExecutor;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRepeatedTask;
import com.elektrobit.ebrace.core.interactor.common.UseCaseRunnable;
import com.elektrobit.ebrace.core.interactor.tableinput.TimeMarkerMixer;
import com.elektrobit.ebrace.core.interactor.tableinput.filter.FilterUtil;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionListener;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureSelectionService;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class RuntimeEventsOfSelectedComrelationNotifyUseCaseImpl
        implements
            RuntimeEventsOfSelectedComrelationNotifyUseCase,
            StructureSelectionListener,
            TimeMarkersChangedListener
{
    private static final long UPDATE_PERIOD_MS = 2000;
    private RuntimeEventsOfSelectedComrelationNotifyCallback callback;
    private final StructureSelectionService structureSelectionService;
    private final ComRelationProvider comRelationProvider;
    private final RuntimeEventProvider runtimeEventProvider;
    private UseCaseRepeatedTask runningTask;

    private List<ComRelation> previousComrelations = null;
    private String filterText;

    private volatile boolean filterTextChanged = false;
    private volatile boolean timemarkerChanged = false;;
    private volatile Integer runtimeEventProviderStateId = null;

    private final List<RowFormatter> rowFormatters;
    private final TimeMarkerManager timeMarkerManager;

    public RuntimeEventsOfSelectedComrelationNotifyUseCaseImpl(
            RuntimeEventsOfSelectedComrelationNotifyCallback callback, List<RowFormatter> rowFormatters,
            StructureSelectionService structureSelectionService, ComRelationProvider comRelationProvider,
            RuntimeEventProvider runtimeEventProvider, TimeMarkerManager timeMarkerManager)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );
        RangeCheckUtils.assertListIsNotEmpty( "rowFormatters", rowFormatters );
        RangeCheckUtils.assertReferenceParameterNotNull( "structureSelectionService", structureSelectionService );
        RangeCheckUtils.assertReferenceParameterNotNull( "comRelationProvider", comRelationProvider );
        RangeCheckUtils.assertReferenceParameterNotNull( "runtimeEventProvider", runtimeEventProvider );
        RangeCheckUtils.assertReferenceParameterNotNull( "timeMarkerManager", timeMarkerManager );
        this.callback = callback;
        this.rowFormatters = rowFormatters;
        this.structureSelectionService = structureSelectionService;
        this.comRelationProvider = comRelationProvider;
        this.runtimeEventProvider = runtimeEventProvider;
        this.timeMarkerManager = timeMarkerManager;
        register();
    }

    private void register()
    {
        structureSelectionService.registerListener( this );
        timeMarkerManager.registerListener( this );
    }

    @Override
    public void onComRelationsSelected(List<ComRelation> selectedComRelations)
    {
        if (isTimerTaskActive())
        {
            triggerSingleDataCollection();
        }
        else
        {
            startPeriodicUpdates();
        }
    }

    private boolean isTimerTaskActive()
    {
        return runningTask != null;
    }

    private void triggerSingleDataCollection()
    {
        List<ComRelation> selectedComRelations = structureSelectionService.getSelectedComRelations();
        collectAndPostData( selectedComRelations );
    }

    private void startPeriodicUpdates()
    {
        if (isTimerTaskActive())
        {
            return;
        }

        runningTask = UseCaseExecutor.scheduleRepeated( new UseCaseRepeatedTask()
        {
            @Override
            public void execute()
            {
                triggerSingleDataCollection();
            }
        }, UPDATE_PERIOD_MS );
    }

    private void stopPeriodicUpdates()
    {
        if (runningTask != null)
        {
            runningTask.cancel();
            runningTask = null;
        }
    }

    private void collectAndPostData(final List<ComRelation> selectedComRelations)
    {
        if (hasDataInDataManagerChanged() || hasSelectionChanged( selectedComRelations ) || filterTextChanged
                || timemarkerChanged)
        {
            UseCaseExecutor
                    .schedule( new UseCaseRunnable( "RuntimeEventsOfSelectedComrelationNotifyUseCase.collectAndPostData",
                                                    () -> {
                                                        filterTextChanged = false;
                                                        timemarkerChanged = false;
                                                        runtimeEventProviderStateId = runtimeEventProvider.getStateId();
                                                        List<?> runtimeEvents = collectData( selectedComRelations );
                                                        postResult( runtimeEvents );
                                                    } ) );
        }
    }

    private boolean hasDataInDataManagerChanged()
    {
        boolean hasDataInDataManagerChanged = (runtimeEventProviderStateId == null)
                || (runtimeEventProvider.hasStateIdChanged( runtimeEventProviderStateId ));
        return hasDataInDataManagerChanged;
    }

    private boolean hasSelectionChanged(List<ComRelation> selectedComRelations)
    {
        boolean hasSelectionChanged = (previousComrelations == null)
                || (!previousComrelations.equals( selectedComRelations ));
        previousComrelations = selectedComRelations;
        return hasSelectionChanged;
    }

    @SuppressWarnings("unchecked")
    protected List<?> collectData(List<ComRelation> selectedComRelations)
    {
        List<ComRelation> comRelationsList = comRelationProvider
                .getChildrenComRelationsRecusivly( selectedComRelations );

        List<RuntimeEvent<?>> events = runtimeEventProvider
                .getRuntimeEventsOfModelElements( new ArrayList<ModelElement>( comRelationsList ) );

        List<? extends TimebasedObject> result;

        if (filterText != null && !filterText.isEmpty())
        {
            TableData filterResultData = FilterUtil.filter( events, filterText, rowFormatters );
            result = (List<TimebasedObject>)filterResultData.getItemsToBeDisplayed();
        }
        else
        {
            result = events;
        }

        List<TimeMarker> timemarkers = new ArrayList<TimeMarker>( timeMarkerManager.getAllVisibleTimemarkers() );

        List<TimebasedObject> tableItems = TimeMarkerMixer.mixAndSortTimemarkers( (List<TimebasedObject>)result,
                                                                                  timemarkers );

        return tableItems;
    }

    protected void postResult(final List<?> runtimeEvents)
    {
        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                callback.onNewComRelationEventData( runtimeEvents );
            }
        } );
    }

    @Override
    public void onSelectionCleared()
    {
        stopPeriodicUpdates();
        postResult( Collections.EMPTY_LIST );
    }

    @Override
    public void onNodesSelected(List<TreeNode> nodes)
    {
    }

    @Override
    public void unregister()
    {
        stopPeriodicUpdates();
        structureSelectionService.unregisterListener( this );
        timeMarkerManager.unregisterListener( this );
        callback = null;
    }

    @Override
    public void setFilterText(String filterText)
    {
        this.filterText = filterText;
        filterTextChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
        timemarkerChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
        timemarkerChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
        timemarkerChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void timeMarkerSelected(final TimeMarker timeMarker)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                callback.onJumpToTimeMarker( timeMarker );
            }
        } );
    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
        timemarkerChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void allTimeMarkersRemoved()
    {
        timemarkerChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
        timemarkerChanged = true;
        triggerSingleDataCollection();
    }

    @Override
    public void timeMarkerNameChanged(final TimeMarker timeMarker)
    {
        UIExecutor.post( new Runnable()
        {

            @Override
            public void run()
            {
                callback.onTimeMarkerRenamed( timeMarker );
            }
        } );
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
        triggerSingleDataCollection();
    }
}
